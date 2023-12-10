package org.example.handler;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

import java.util.*;

import static org.example.utils.StaticVariables.*;

public class DuplicateRemoverHandler {

    public void runRemover(Stack<OntClass> ontClassStack, OntModel ontModel, OntClass headClass) {
        String duplicateUri;
        removeElementsAfterHead(ontClassStack, headClass);
        do {
            duplicateUri = findDuplicateUri(ontClassStack);
            if (duplicateUri != null) {
                removeElementsBetweenDuplicated(ontClassStack, ontModel, duplicateUri);
            }
        } while (duplicateUri != null);
        removeElementsFromExtraHierarchy(ontClassStack);

    }

    private void removeElementsFromExtraHierarchy(Stack<OntClass> ontClassStack) {
        Stack<OntClass> tempStack = new Stack<>();
        while (!ontClassStack.isEmpty()) {
            OntClass currentOntClass = ontClassStack.pop();
            if (shouldRemove(currentOntClass)) {

            } else {
                tempStack.push(currentOntClass);
            }
        }
        while (!tempStack.isEmpty()) {
            ontClassStack.push(tempStack.pop());
        }
        removeContinuant(ontClassStack);
    }

    private void removeContinuant(Stack<OntClass> ontClassStack) {
        Stack<OntClass> reverse = new Stack<>();
        while (!ontClassStack.isEmpty()) {
            reverse.push(ontClassStack.pop());
        }
        Iterator<OntClass> iterator = reverse.iterator();
        while (iterator.hasNext()) {
            OntClass currentOntClass = iterator.next();
            String label = (currentOntClass.getLabel(EN) != null) ? currentOntClass.getLabel(EN).trim() : null;
            if (CONTINUANT.equals(label)) {
                iterator.remove();
                while (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
                break;
            }
        }
        while (!reverse.isEmpty()) {
            ontClassStack.push(reverse.pop());
        }
    }

    private boolean shouldRemove(OntClass ontClass) {
        String label = (ontClass.getLabel(EN) != null) ? ontClass.getLabel(EN).trim() : null;
        return THING.equals(label) || MATERIAL_ENTITY.equals(label) || label == null || HEAD_CLASS_URI.equals(ontClass.getURI());
    }

    private void removeElementsAfterHead(Stack<OntClass> ontClassStack, OntClass ontClassToFind) {
        while (!ontClassStack.isEmpty()) {
            OntClass currentOntClass = ontClassStack.pop();
            String className = currentOntClass.getURI();
            if (className != null && className.equals(ontClassToFind.getURI())) {
                ontClassStack.push(currentOntClass);
                break;
            }
        }
    }

    public void removeElementsBetweenDuplicated(Stack<OntClass> ontClassStack, OntModel ontModel, String uriToRemove) {
        Stack<OntClass> tempStack = new Stack<>();

        if (uriToRemove != null) {
            boolean foundFirst = false;
            while (!ontClassStack.isEmpty()) {
                OntClass currentOntClass = ontClassStack.pop();
                if (uriToRemove.equals(currentOntClass.getLocalName())) {
                    foundFirst = true;
                    break;
                }
                tempStack.push(currentOntClass);
            }
            while (!ontClassStack.isEmpty()) {
                OntClass currentOntClass = ontClassStack.pop();
                if (uriToRemove.equals(currentOntClass.getLocalName())) {
                    ontClassStack.push(currentOntClass);
                    break;
                }
            }
            while (!tempStack.isEmpty()) {
                ontClassStack.push(tempStack.pop());
            }
            if (!foundFirst) {
                ontClassStack.push(ontModel.createClass(uriToRemove));
            }
        }


    }

    private String findDuplicateUri(Stack<OntClass> ontClassStack) {
        Set<String> seen = new HashSet<>();
        for (OntClass ontClass : ontClassStack) {
            String uri = ontClass.getLocalName();
            if (uri != null) {
                if (!seen.add(uri)) {
                    return uri;
                }
            }
        }
        return null;
    }
}