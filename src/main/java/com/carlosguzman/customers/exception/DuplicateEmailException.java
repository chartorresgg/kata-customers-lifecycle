package com.carlosguzman.customers.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Ya existe un cliente con ese correo.");
    }

}
