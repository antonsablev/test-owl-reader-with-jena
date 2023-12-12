package org.example.handler;

import lombok.RequiredArgsConstructor;
import org.apache.jena.ontology.OntClass;

import java.util.*;

@RequiredArgsConstructor
public class SorterHandler implements Comparator<Stack<OntClass>> {
    @Override
    public int compare(Stack<OntClass> stack1, Stack<OntClass> stack2) {
        if (!stack1.isEmpty() && !stack2.isEmpty()) {
            OntClass ontClass1 = stack1.peek();
            OntClass ontClass2 = stack2.peek();
            return ontClass1.getLabel(null).compareTo(ontClass2.getLabel(null));
        }
        return Integer.compare(stack1.size(), stack2.size());
    }
}
