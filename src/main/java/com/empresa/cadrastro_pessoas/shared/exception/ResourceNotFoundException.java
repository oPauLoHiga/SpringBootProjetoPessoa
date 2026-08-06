package com.empresa.cadrastro_pessoas.shared.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String mensagem){
        super(mensagem);
    }
}

