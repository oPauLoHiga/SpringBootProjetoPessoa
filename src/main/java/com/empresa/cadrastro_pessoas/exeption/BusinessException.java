package com.empresa.cadrastro_pessoas.exeption;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
