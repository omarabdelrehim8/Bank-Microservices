package com.omarabdelrehim8.accounts.repository;

import com.omarabdelrehim8.accounts.audit.AuditorProvider;
import com.omarabdelrehim8.accounts.entity.Account;
import com.omarabdelrehim8.accounts.entity.Customer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
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
public class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    ApplicationContext context;

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
        assertThat(context.containsBean("auditorProvider")).isTrue();
    }

    @BeforeTransaction
    void init() {
      List<Customer> customers = List.of(new Customer(1L, "Regis Aether", "regisaether@gmail.com", "0123456781"),
                                      new Customer(2L, "Arthur Leywin", "arthurleywin@gmail.com", "0123456782"),
                                      new Customer(3L, "Sylvie Idrath", "sylvieidrath@gmail.com", "0123456783"));

      customerRepository.saveAll(customers);

      List<Account> accounts = List.of(new Account(customers.get(0), 1234567891L, "Savings", "123 Main Street, New York"),
                            new Account(customers.get(0), 1234567892L, "Savings", "123 Main Street, New York"),
                            new Account(customers.get(0), 1234567893L, "Savings", "123 Main Street, New York"),
                            new Account(customers.get(1), 1234567894L, "Savings", "123 Main Street, New York"),
                            new Account(customers.get(2), 1234567895L, "Savings", "123 Main Street, New York"));

        accountRepository.saveAll(accounts);
    }

    @Test
    void Should_Return_All_Accounts_Of_A_Customer() {
        List<Account> accountsList = accountRepository.findByCustomerId(1L).get();

        assertThat(accountsList).size().isEqualTo(3);
        assertThat(accountsList).extracting(Account::getAccountNumber).isEqualTo(List.of(1234567891L, 1234567892L, 1234567893L));
    }

    @Test
    void Should_Return_Count_Of_Accounts_Count_Of_A_Customer() {
        int count = accountRepository.countAccountsByCustomerId(2L);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void Should_Return_Who_Created_The_Account() {
        String auditor = accountRepository.findByAccountNumber(1234567891L).get().getCreatedBy();

        assertThat(auditor).isEqualTo("ACCOUNTS_MS");
    }

    @Test
    void Should_Return_Account_Creation_Date() {
        LocalDate date = accountRepository.findByAccountNumber(1234567891L).get().getCreatedAt().toLocalDate();

        assertThat(date).isEqualTo(LocalDate.now());
    }
    @Test
    void Should_Return_Who_Updated_The_Account() {
        Account account = accountRepository.findByAccountNumber(1234567891L).get();
        account.setAccountType("Test");
        accountRepository.saveAndFlush(account);

        assertThat(account.getUpdatedBy()).isEqualTo("ACCOUNTS_MS");
    }

    @Test
    void Should_Return_When_Account_Was_Updated() {
        Account account = accountRepository.findByAccountNumber(1234567891L).get();
        account.setAccountType("Test");
        accountRepository.saveAndFlush(account);

        LocalDate date = account.getUpdatedAt().toLocalDate();

        assertThat(date).isEqualTo(LocalDate.now());
    }
}

