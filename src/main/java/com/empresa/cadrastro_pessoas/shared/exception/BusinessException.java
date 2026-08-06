package com.empresa.cadrastro_pessoas.shared.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
