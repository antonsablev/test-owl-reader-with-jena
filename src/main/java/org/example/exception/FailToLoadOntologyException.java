package org.example.exception;

public class FailToLoadOntologyException extends RuntimeException{
    public FailToLoadOntologyException(String message) {
        super(message);
    }
}
