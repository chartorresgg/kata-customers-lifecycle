package com.carlosguzman.customers.repository;

import org.junit.jupiter.api.Test;
import com.carlosguzman.customers.model.Customer;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

/**
 * Prueba Unitaria del Repositorio
 */
class CustomerRepositoryTest {

    @Test
    void saveAsignaIdsConsecutivos() {
        CustomerRepository repository = new CustomerRepository();
        Customer first = repository.save("Antonia", "antonia@gmail.com");
        Customer second = repository.save("Charlie", "charlie@gmail.com");
        assertThat(second.getId()).isGreaterThan(first.getId());
    }

    @Test
    void findAllDevuelveLosClientesOrdenadosPorId() {
        CustomerRepository repository = new CustomerRepository();
        repository.save("Antonia", "antonia@gmail.com");
        repository.save("Charlie", "charlie@gmail.com");
        repository.save("Thommy", "thommy@gmail.com");
        List<Customer> customers = repository.findAll();

        assertThat(customers)
                .extracting(Customer::getId)
                .containsExactly(1L, 2L, 3L);
    }

    @Test
    void existsByEmailNoDistingueMayusculas() {
        CustomerRepository repository = new CustomerRepository();
        repository.save("Charlie", "charlie@gmail.com");
        assertThat(repository.existsByEmail("CHARLIE@GMAIL.COM")).isTrue();
    }

    @Test
    void existsByEmailDevuelveFalseSiNoExiste() {
        CustomerRepository repository = new CustomerRepository();
        repository.save("Charlie", "charlie@gmail.com");
        assertThat(repository.existsByEmail("OTRO@GMAIL.COM")).isFalse();
    }
}
