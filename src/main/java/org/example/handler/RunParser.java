package org.example.handler;

import org.apache.jena.ontology.OntModel;
import org.example.service.CsvWriterService;

public class RunParser {
    public void run(String readUri, String writeUri) {
        CreateOntModelHandler createOntModelHandler = new CreateOntModelHandler();
        OntModel model = createOntModelHandler.getOntologyModelByUrl(readUri);

        CsvWriterService writerService = new CsvWriterService(model);
        writerService.write(writeUri);
    }
}
