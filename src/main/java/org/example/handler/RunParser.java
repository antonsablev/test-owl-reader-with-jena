package org.example.handler;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.example.service.CsvWriterService;
import org.example.service.OwlClassesService;
import org.example.service.OwlReader;

import java.util.Set;

public class RunParser {
    public void run(String readUri, String writeUri) {
        CreateOntModelHandler createOntModelHandler = new CreateOntModelHandler();
        OntModel model = createOntModelHandler.getOntologyModelByUrl(readUri);
        DuplicateRemover duplicateRemover = new DuplicateRemover();
        OwlReader reader = new OwlReader(model);
        OwlClassesService classesService = new OwlClassesService(duplicateRemover, model);
        CsvWriterService writerService = new CsvWriterService(reader, classesService);

        Set<OntClass> read = reader.read();
        writerService.write(writeUri, read);
    }
}
