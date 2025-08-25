package com.portal.service;

/**
 * Exceção personalizada para erros da camada de serviço.
 * Utilizada para encapsular erros de validação e regras de negócio.
 */
public class ServiceException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}