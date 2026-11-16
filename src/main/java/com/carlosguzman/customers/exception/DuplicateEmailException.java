package com.carlosguzman.customers.exception;

/**
 * Excepción lanzada cuando se intenta registrar un cliente con un correo
 * electrónico que ya existe en la lista de clientes.
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Crea una nueva instancia de la excepción con un mensaje descriptivo.
     */
    public DuplicateEmailException() {
        super("Ya existe un cliente con ese correo.");
    }

}
