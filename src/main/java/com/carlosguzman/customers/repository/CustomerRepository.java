package com.carlosguzman.customers.repository;

import org.springframework.stereotype.Repository;

import com.carlosguzman.customers.model.Customer;

import java.util.Map;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio de clientes. Implementa el almacenamiento en memoria de los
 * clientes.
 */
@Repository
public class CustomerRepository {

    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();

    /**
     * Crea un nuevo cliente inmutable, lo almacena en memoria con un ID único
     * generado automáticamente y devuelve la instancia creada.
     *
     * @param name  Nombre completo del cliente.
     * @param email Correo electrónico del cliente.
     * @return El objeto {@link Customer} recién creado y almacenado.
     */
    public Customer save(String name, String email) {
        long id = sequence.incrementAndGet();

        Customer customer = new Customer(id, name, email);
        customers.put(id, customer);
        return customer;
    }

    /**
     * Obtiene todos los clientes almacenados, ordenados de forma ascendente por su
     * ID.
     * 
     * @return Una lista de objetos {@link Customer} ordenados por ID,
     *         o una lista vacía si no hay clientes registrados.
     */
    public List<Customer> findAll() {
        return customers.values().stream()
                .sorted(Comparator.comparing(Customer::getId))
                .toList();
    }

    /**
     * Verifica si ya existe un cliente registrado con el correo electrónico indicado,
     * sin distinguir entre mayúsculas y minúsculas.
     * 
     * @param email Correo a validar.
     * @return {@code true} si existe un cliente con ese correo; {@code false} en
     *         caso contrario.
     */
    public boolean existsByEmail(String email) {
        return customers.values().stream()
                .anyMatch(customer -> customer.getEmail().equalsIgnoreCase(email));
    }
}
