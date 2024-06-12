package com.omarabdelrehim8.accounts.repository;

import com.omarabdelrehim8.accounts.audit.AuditorProvider;
import com.omarabdelrehim8.accounts.entity.Account;
import com.omarabdelrehim8.accounts.entity.Customer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Import(AuditorProvider.class)
public class CustomerRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Container
    @ServiceConnection
    private static final MySQLContainer mysql = new MySQLContainer<>(DockerImageName.parse("mysql"))
            .withDatabaseName("accountsdb")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"), "/docker-entrypoint-initdb.d/");

    @Test
    @Order(1)
    void Connection_Established() {
        assertThat(mysql.isCreated()).isTrue();
        assertThat(mysql.isRunning()).isTrue();
    }

    @BeforeTransaction
    void init() {
        List<Customer> customers = List.of(new Customer(1L, "Regis Aether", "regisaether@gmail.com", "0123456781"),
                new Customer(2L, "Arthur Leywin", "arthurleywin@gmail.com", "0123456782"),
                new Customer(3L, "Sylvie Idrath", "sylvieidrath@gmail.com", "0123456783"));

        customerRepository.saveAll(customers);

        List<Account> accounts = List.of(new Account(customers.get(0), 1234567891L, "Savings", "123 Main Street, New York", false),
                new Account(customers.get(0), 1234567892L, "Savings", "123 Main Street, New York", false),
                new Account(customers.get(0), 1234567893L, "Savings", "123 Main Street, New York", false),
                new Account(customers.get(1), 1234567894L, "Savings", "123 Main Street, New York", false),
                new Account(customers.get(2), 1234567895L, "Savings", "123 Main Street, New York", false));

        accountRepository.saveAll(accounts);
    }

    @Test
    void Should_Return_Who_Inserted_The_Customer_Details() {
        String auditor = customerRepository.findById(1L).get().getCreatedBy();

        assertThat(auditor).isEqualTo("ACCOUNTS_MS");
    }

    @Test
    void Should_Return_Customer_Details_Creation_Date() {
        LocalDate date = customerRepository.findById(1L).get().getCreatedAt().toLocalDate();

        assertThat(date).isEqualTo(LocalDate.now());
    }
    @Test
    void Should_Return_Who_Updated_Customer_Details() {
        Customer customer = customerRepository.findById(1L).get();
        customer.setEmail("Test@gmail.com");
        customerRepository.saveAndFlush(customer);

        assertThat(customer.getUpdatedBy()).isEqualTo("ACCOUNTS_MS");
    }

    @Test
    void Should_Return_When_Customer_Details_Were_Updated() {
        Customer customer = customerRepository.findById(1L).get();
        customer.setEmail("Test@gmail.com");
        customerRepository.saveAndFlush(customer);

        LocalDate date = customer.getUpdatedAt().toLocalDate();

        assertThat(date).isEqualTo(LocalDate.now());
    }
}
