package com.omarabdelrehim8.accounts.repository;

import com.omarabdelrehim8.accounts.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByMobileNumber(String mobileNumber);

    Optional<Customer> findByMobileNumberOrEmail(String mobileNumber, String email);

    Optional<Customer> findByNameAndMobileNumberAndEmail(String lastName, String mobileNumber, String email);
}
