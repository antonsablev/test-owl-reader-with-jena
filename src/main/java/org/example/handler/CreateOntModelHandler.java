package org.example.handler;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.example.exception.FailToLoadOntologyException;

import java.io.IOException;
import java.io.InputStream;

public class CreateOntModelHandler {
    public OntModel getOntologyModelByUrl(String owlUrl) {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        try (InputStream inputStream = FileManager.get().open(owlUrl)) {
            System.setProperty("entityExpansionLimit", "200000");
            if (inputStream != null) {
                model.read(inputStream, null);
                return model;
            }
        } catch (IOException e) {
            throw new FailToLoadOntologyException("Can't create ontology");
        }
        return null;
    }
}