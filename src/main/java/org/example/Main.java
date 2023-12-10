package org.example;

import org.example.handler.RunParser;

import static org.example.utils.StaticVariables.CSV_RESULT_URL;
import static org.example.utils.StaticVariables.ONTOLOGY_URL;

public class Main {
    public static void main(String[] args) {
        RunParser parser = new RunParser();
        parser.run(ONTOLOGY_URL, CSV_RESULT_URL);
    }
}