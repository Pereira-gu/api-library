package com.gu_pereira.api_libary.infrastructure.exceptions;

public class NegocioException extends RuntimeException {
    public NegocioException(String message) {
        super(message);
    }
}