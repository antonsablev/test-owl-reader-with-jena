package org.example.handler;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.example.service.CsvWriterService;
import org.example.service.OwlReaderService;

import java.util.Set;

public class RunParser {
    public void run(String readUri, String writeUri) {
        CreateOntModelHandler createOntModelHandler = new CreateOntModelHandler();
        OntModel model = createOntModelHandler.getOntologyModelByUrl(readUri);
        DuplicateRemoverHandler duplicateRemover = new DuplicateRemoverHandler();
        OwlReaderService reader = new OwlReaderService(model);
        OwlParentClassesHandler classesService = new OwlParentClassesHandler(duplicateRemover, model);
        CsvWriterService writerService = new CsvWriterService(reader, classesService);

        Set<OntClass> read = reader.read();
        writerService.write(writeUri, read);
    }
}
