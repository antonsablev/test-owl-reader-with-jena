package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.example.exception.ItemNotFoundException;
import org.example.handler.OwlParentClassesHandler;
import org.example.handler.SorterHandler;

import java.util.*;

import static org.example.utils.StaticVariables.*;

@RequiredArgsConstructor
public class OwlReaderService {
    private final OntModel model;
    private final OwlParentClassesHandler parentClassesHandler;

    public List<Stack<OntClass>> read() {
        OntClass headClass = model.getOntClass(HEAD_CLASS_URI);
        if (headClass != null) {
            return createList(headClass);
        } else {
            throw new ItemNotFoundException("Can't find head class: " + HEAD_CLASS_URI);
        }
    }

    private Set<OntClass> createUniqueSet(OntClass ontClass) {
        Set<OntClass> uniqueClasses = new LinkedHashSet<>();
        String wikiLink = getWikipediaLink(ontClass);

        if (wikiLink != null && wikiLink.contains(WIKI_LINK)) {
            uniqueClasses.add(ontClass);
        }

        ExtendedIterator<OntClass> subclasses = ontClass.listSubClasses(false);
        while (subclasses.hasNext()) {
            OntClass subclass = subclasses.next();
            Set<OntClass> subclassesWithWikiLink = createUniqueSet(subclass);
            uniqueClasses.addAll(subclassesWithWikiLink);
        }
        return uniqueClasses;
    }

    public List<Stack<OntClass>> createList(OntClass headClass) {
        List<Stack<OntClass>> parentsList = new ArrayList<>();
        Set<OntClass> unicSet = createUniqueSet(headClass);
        for (OntClass ontClass : unicSet) {
            Stack<OntClass> parentClasses = parentClassesHandler.getParentClasses(ontClass);
            parentsList.add(parentClasses);
        }
        return sortList(parentsList);
    }

    private List<Stack<OntClass>> sortList(List<Stack<OntClass>> list) {
        list.sort(new SorterHandler());
        return list;
    }

    public String getWikipediaLink(OntClass ontClass) {
        Resource foodProductResource = model.getResource(ontClass.getURI());
        StmtIterator stmtIterator = foodProductResource.listProperties();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            if (WIKI_PROPERTY_NAME.equals(statement.getPredicate().getLocalName())) {
                return statement.getObject().toString();
            }
        }
        return null;
    }
}