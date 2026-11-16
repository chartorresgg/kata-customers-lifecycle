package com.carlosguzman.customers.repository;

import org.junit.jupiter.api.Test;
import com.carlosguzman.customers.model.Customer;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

/**
 * Prueba Unitaria del Repositorio: Comprueba el comportamiento del
 * almacenamiento.
 */
class CustomerRepositoryTest {

    @Test
    void saveAsignaIdsConsecutivos() {
        CustomerRepository repository = new CustomerRepository();
        Customer first = repository.save("Antonia", "antonia@example.com");
        Customer second = repository.save("Charlie", "charlie@example.com");

        assertThat(first.getId()).isEqualTo(1L);
        assertThat(second.getId()).isEqualTo(2L);

    }

    @Test
    void saveGuardaNombreYEmail() {
        CustomerRepository repository = new CustomerRepository();
        Customer saved = repository.save("Antonia", "antonia@example.com");

        assertThat(saved.getName()).isEqualTo("Antonia");
        assertThat(saved.getEmail()).isEqualTo("antonia@example.com");
    }

    @Test
    void findAllDevuelveLosClientesOrdenadosPorId() {
        CustomerRepository repository = new CustomerRepository();
        repository.save("Antonia", "antonia@example.com");
        repository.save("Charlie", "charlie@example.com");
        repository.save("Thommy", "thommy@example.com");
        List<Customer> customers = repository.findAll();

        assertThat(customers)
                .extracting(Customer::getId)
                .containsExactly(1L, 2L, 3L);
    }

    @Test
    void findAllDevuelveListaVaciaSiNoHayClientes() {
        CustomerRepository repository = new CustomerRepository();

        assertThat(repository.findAll()).isEmpty();
    }
    
    @Test
    void existsByEmailNoDistingueMayusculas() {
        CustomerRepository repository = new CustomerRepository();
        repository.save("Charlie", "charlie@example.com");
        assertThat(repository.existsByEmail("CHARLIE@EXAMPLE.COM")).isTrue();
    }



    @Test
    void existsByEmailDevuelveFalseSiNoExiste() {
        CustomerRepository repository = new CustomerRepository();
        repository.save("Charlie", "charlie@example.com");
        assertThat(repository.existsByEmail("otro@example.com")).isFalse();
    }
}
