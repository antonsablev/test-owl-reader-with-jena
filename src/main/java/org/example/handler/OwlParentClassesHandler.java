package org.example.handler;

import lombok.RequiredArgsConstructor;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

import java.util.Stack;

import static org.example.utils.StaticVariables.HEAD_CLASS_URI;

@RequiredArgsConstructor
public class OwlParentClassesHandler {

    private final DuplicateRemoverHandler duplicateRemover;
    private final OntModel model;

    public Stack<OntClass> getParentClasses(OntClass ontClass) {
        Stack<OntClass> parentClasses = new Stack<>();
        collectParentClasses(ontClass, parentClasses);
        OntClass headClass = model.getOntClass(HEAD_CLASS_URI);
        duplicateRemover.runRemover(parentClasses, model, headClass);
        return parentClasses;
    }

    private void collectParentClasses(OntClass ontClass, Stack<OntClass> parentClasses) {
        if (ontClass != null) {
            String title = ontClass.getURI();
            if (title != null) {
                parentClasses.push(ontClass);
                ontClass.listSuperClasses().forEachRemaining(superClass -> {
                    collectParentClasses(superClass.as(OntClass.class), parentClasses);
                });
            }
        }
    }

}
