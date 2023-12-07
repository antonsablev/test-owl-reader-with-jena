package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.example.exception.CsvFileException;
import org.example.exception.ItemNotFoundException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RequiredArgsConstructor
public class CsvWriterService {
    private static final int START_POSITION = 1;
    private static final int URI_POSITION = 4;
    private static final String HEAD_CLASS_URI = "http://purl.obolibrary.org/obo/FOODON_00001002";
    private final OntModel model;

    public void write(String writeUri) {
        OntClass headClass = model.getOntClass(HEAD_CLASS_URI);
        if (headClass != null) {
            checkFile(writeUri);
            ExtendedIterator<OntClass> subclasses = headClass.listSubClasses(true);
            try (FileWriter writer = new FileWriter(writeUri)) {
                createDocumentHeader(writer);
                while (subclasses.hasNext()) {
                    OntClass subclass = subclasses.next();
                    writeSubclassToCsv(writer, subclass, START_POSITION);
                }
                System.out.println("CSV file successfully created: " + writeUri);
            } catch (IOException e) {
                throw new CsvFileException("Помилка при роботі з файлом CSV");
            }
        } else {
            throw new ItemNotFoundException("Клас не знайдено: " + HEAD_CLASS_URI);
        }
    }

    private void checkFile(String writeUri) {
        File file = new File(writeUri);
        try {
            if (!file.exists()) {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                    throw new CsvFileException("Can't create directories for csv file: " + parentDir.getAbsolutePath());
                }
                if (file.createNewFile()) {
                    System.out.println("File created: " + writeUri);
                } else {
                    System.out.println("File not created: " + writeUri);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CsvFileException("Can't create csv file: " + e.getMessage());
        }
    }

    private void createDocumentHeader(FileWriter writer) throws IOException {
        for (int i = START_POSITION; i < URI_POSITION; i++) {
            writer.append(i + " Level,");
        }
        writer.append("FoodON URI\n");
    }

    private void writeSubclassToCsv(FileWriter writer, OntClass ontClass, int level) throws IOException {
        String wikiLink = getWikipediaLink(ontClass);
        StringBuilder line = new StringBuilder();
        boolean hasSubClass = false;

        for (int i = START_POSITION; i < level; i++) {
            line.append(",");
        }

        if (wikiLink != null) {
            hasSubClass = true;
            String en = null;
            if (ontClass.getLabel("en") != null) {
                en = ontClass.getLabel("en").replaceAll(",", "");
                line.append(en).append(",");
            } else {
                line.append("not set").append(",");
            }
            writeUri(ontClass, line, level);
            writer.append(line.toString());
        }

        ExtendedIterator<OntClass> subclasses = ontClass.listSubClasses(false);
        while (subclasses.hasNext()) {
            OntClass subclass = subclasses.next();
            if (hasSubClass) {
                level = START_POSITION + level;
                hasSubClass = false;
            } else {
                level = START_POSITION;
            }
            writeSubclassToCsv(writer, subclass, level);
        }
    }

    private void writeUri(OntClass ontClass, StringBuilder line, int level) {
        setUriLevel(level, line);
        line.append(ontClass.getURI()).append("\n");
    }

    private void setUriLevel(int level, StringBuilder line) {
        for (int i = START_POSITION; i < URI_POSITION - level; i++) {
            line.append(",");
        }
    }

    private String getWikipediaLink(OntClass ontClass) {
        Resource foodProductResource = model.getResource(ontClass.getURI());
        StmtIterator stmtIterator = foodProductResource.listProperties();
        String result = null;
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            if (statement.getPredicate().getLocalName().equals("IAO_0000119")) {
                result = statement.getObject().toString();
            }
        }
        return result;
    }
}

