package com.carlosguzman.customers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO que transporta los datos de un nuevo cliente. Representa el JSON que envía el usuario al crear un nuevo cliente.
 */
public class CustomerRequest {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es requerido")
    private String email;

    public CustomerRequest() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
