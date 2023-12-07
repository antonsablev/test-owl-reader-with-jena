package org.example;

import org.example.handler.RunParser;

public class Main {
    private static final String ONTOLOGY_URL = "http://purl.obolibrary.org/obo/foodon.owl";
    private static final String CSV_RESULT_URL = "csv/csv-result.csv";
    public static void main(String[] args) {
        RunParser parser = new RunParser();
        parser.run(ONTOLOGY_URL, CSV_RESULT_URL);
    }
}