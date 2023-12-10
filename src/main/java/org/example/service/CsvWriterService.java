package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.jena.ontology.OntClass;
import org.example.exception.CsvFileException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.Stack;

import static org.example.utils.StaticVariables.*;

@RequiredArgsConstructor
public class CsvWriterService {
    private final OwlReader reader;
    private final OwlClassesService classesService;


    public void write(String writeUri, Set<OntClass> read) {
        checkFile(writeUri);
        try (FileWriter writer = new FileWriter(writeUri)) {
            createDocumentHeader(writer);
            for (OntClass subclass : read) {
                writeSubclassToCsv(writer, subclass, START_POSITION);
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
        writer.append(FOODON_URI_HEAD_TEXT);
    }

    private void writeSubclassToCsv(FileWriter writer, OntClass ontClass, int level) throws IOException {
        StringBuilder line = new StringBuilder();
        Stack<OntClass> parentClasses = classesService.getParentClasses(ontClass);
        while (!parentClasses.isEmpty()) {
            OntClass popped = parentClasses.pop();
            String label = popped.getLabel(EN).replaceAll(COMA, "");
            line.append(label);
            writeUri(popped, line, level - START_POSITION);

            level = level + 1;
            for (int i = 1; i < level; i++) {
                line.append(COMA);
            }
        }
        writer.append(line.toString());
        writer.append(LINE_BREAK);
    }

    private void writeUri(OntClass ontClass, StringBuilder line, int level) {
        String uri = ontClass.getLocalName();
        String wikiLink = reader.getWikipediaLink(ontClass);
        if (wikiLink != null) {
            uri = ontClass.getURI();
        }
        setUriLevel(level, line);
        line.append(uri).append(LINE_BREAK).toString();
    }

    private void setUriLevel(int level, StringBuilder line) {
        for (int i = START_POSITION; i < URI_POSITION - level; i++) {
            line.append(COMA);
        }
    }
}
