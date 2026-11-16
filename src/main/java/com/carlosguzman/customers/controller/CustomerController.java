package com.carlosguzman.customers.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carlosguzman.customers.dto.CustomerRequest;
import com.carlosguzman.customers.model.Customer;
import com.carlosguzman.customers.service.CustomerService;

import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints para la gestión de clientes.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Construye el controlador inyectando el servicio de clientes.
     *
     * @param customerService servicio encargado de la lógica de negocio de
     *                        clientes.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Crea un nuevo cliente a partir de los datos enviados en el cuerpo de la
     * petición.
     * Si los datos no pasan la validación responde {@code 400}; si el correo ya
     * existe, {@code 409}.
     *
     * @param request datos validados del cliente a registrar (nombre y correo
     *                electrónico).
     * @return respuesta HTTP {@code 201 Created} con el cliente creado y su ID
     *         asignado.
     */
    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerRequest request) {
        Customer customer = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    /**
     * Obtiene el listado completo de clientes registrados.
     *
     * @return lista con todos los clientes existentes en el sistema, ordenada por
     *         ID y con {@code 200}
     */
    @GetMapping
    public List<Customer> findAll() {
        return customerService.findAll();
    }

}
