package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.example.exception.ItemNotFoundException;

import java.util.HashSet;
import java.util.Set;

import static org.example.utils.StaticVariables.*;

@RequiredArgsConstructor
public class OwlReaderService {
    private final OntModel model;

    public Set<OntClass> read() {
        OntClass headClass = model.getOntClass(HEAD_CLASS_URI);
        if (headClass != null) {
            return createUniqueSet(headClass);
        } else {
            throw new ItemNotFoundException("Can't find head class: " + HEAD_CLASS_URI);
        }
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

    private Set<OntClass> createUniqueSet(OntClass ontClass) {
        Set<OntClass> uniqueClasses = new HashSet<>();
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
}
