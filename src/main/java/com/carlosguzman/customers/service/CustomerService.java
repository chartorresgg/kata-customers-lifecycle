package com.carlosguzman.customers.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.carlosguzman.customers.dto.CustomerRequest;
import com.carlosguzman.customers.exception.DuplicateEmailException;
import com.carlosguzman.customers.model.Customer;
import com.carlosguzman.customers.repository.CustomerRepository;

/**
 * Reglas de negocio de los clientes
 */
@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    /**
     * Crea el servicio con el repositorio inyectado por Spring.
     * 
     * @param customerRepository Recibe el repositorio por inyección de dependencias
     */
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Registra un nuevo cliente verificando previamente que su correo electrónico
     * no exista.
     *
     * @param request datos de entrada del cliente a registrar (nombre y correo).
     * @return el cliente guardado en memoria con su ID asignado.
     * @throws DuplicateEmailException si ya existe un cliente registrado con el
     *                                 mismo correo.
     */
    public Customer create(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        Customer customer = customerRepository.save(request.getName(), request.getEmail());

        log.info("Cliente creado con id={}", customer.getId());
        return customer;
    }

    /**
     * Obtiene todos los clientes creados ordenados por ID y que están almacenados
     * en el Repositorio.
     * 
     * @return Listado de clientes existentes ordenados por ID.
     */
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

}
