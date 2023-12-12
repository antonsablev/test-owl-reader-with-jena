package org.example.handler;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.example.service.CsvWriterService;
import org.example.service.OwlReaderService;

import java.util.List;
import java.util.Set;
import java.util.Stack;

public class RunParser {
    public void run(String readUri, String writeUri) {
        CreateOntModelHandler createOntModelHandler = new CreateOntModelHandler();
        OntModel model = createOntModelHandler.getOntologyModelByUrl(readUri);
        DuplicateRemoverHandler duplicateRemover = new DuplicateRemoverHandler();
        OwlParentClassesHandler classesService = new OwlParentClassesHandler(duplicateRemover, model);
        OwlReaderService reader = new OwlReaderService(model, classesService);
        CsvWriterService writerService = new CsvWriterService(reader);

        List<Stack<OntClass>> read = reader.read();
        writerService.write(writeUri, read);
    }
}
