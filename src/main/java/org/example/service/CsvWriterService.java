package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.jena.ontology.OntClass;
import org.example.exception.CsvFileException;
import org.example.handler.OwlParentClassesHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static org.example.utils.StaticVariables.*;

@RequiredArgsConstructor
public class CsvWriterService {
    private final OwlReaderService reader;
    private int currentLevel;
    private String previousClassLabel;

    public void write(String writeUri, List<Stack<OntClass>> read) {
        checkFile(writeUri);
        try (FileWriter writer = new FileWriter(writeUri)) {
            createDocumentHeader(writer);
            for (Stack<OntClass> eachStack : read) {
                writeSubclassToCsv(writer, eachStack, currentLevel);
            }

            System.out.println(FILE_CREATED_MESSAGE + writeUri);
        } catch (IOException e) {
            throw new CsvFileException(CSV_ERROR_MESSAGE);
        }
    }


    private void checkFile(String writeUri) {
        File file = new File(writeUri);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new CsvFileException(DIRECTORY_ERROR_MESSAGE + parentDir.getAbsolutePath());
            }
        }

    }

    private void createDocumentHeader(FileWriter writer) throws IOException {
        for (int i = START_POSITION; i < URI_POSITION; i++) {
            writer.append(i + LEVEL);
        }
        writer.append(FOODON_URI_HEAD_TEXT).append(COMA);
        writer.append(WIKI_HEAD_TEXT);
    }


    private void writeSubclassToCsv(FileWriter writer, Stack<OntClass> parentClasses, int level) throws IOException {
        StringBuilder line = new StringBuilder();
        boolean hasSameParent = checkParentCategory(parentClasses);
            if (hasSameParent) {
                while (parentClasses.size() > 1) {
                    parentClasses.pop();
                }
            } else {
                writer.append(LINE_BREAK);
                level = START_POSITION;
            }

        while (!parentClasses.isEmpty()) {
            OntClass popped = parentClasses.pop();
            String label = popped.getLabel(EN).replaceAll(COMA, "");
            line.append(label);
            writeUri(popped, line, level - START_POSITION);
            if (!hasSameParent && !parentClasses.isEmpty()) {
                level = level + 1;
            }
            for (int i = START_POSITION; i < level; i++) {
                line.append(COMA);
            }
            currentLevel = level;
        }

        writer.append(line.toString());
    }

    private boolean checkParentCategory(Stack<OntClass> parentClasses) {
        if (parentClasses.size() > 2) {
            OntClass ontClass = parentClasses.get(1);
            String currentLabel = ontClass.getLabel(null);
            if (currentLabel.equals(previousClassLabel)) {
                previousClassLabel = currentLabel;
                return true;
            }
            previousClassLabel = currentLabel;
        }
        return false;
    }

    private void writeUri(OntClass ontClass, StringBuilder line, int level) {
        String uri = ontClass.getLocalName();
        String wikiLink = reader.getWikipediaLink(ontClass);
        if (wikiLink != null) {
            uri = ontClass.getURI();
        }
        setUriLevel(level, line);
        line.append(uri);
        if (wikiLink != null) {
            line.append(COMA);
            line.append(wikiLink);
        }

        line.append(LINE_BREAK).toString();
    }

    private void setUriLevel(int level, StringBuilder line) {
        for (int i = START_POSITION; i < URI_POSITION - level; i++) {
            line.append(COMA);
        }
    }
}
