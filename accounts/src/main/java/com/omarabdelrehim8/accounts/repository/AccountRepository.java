package com.omarabdelrehim8.accounts.repository;

import com.omarabdelrehim8.accounts.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<List<Account>> findByCustomerId(Long customerId);

    Optional<Account> findByAccountNumber(Long accountNumber);

    @Query("SELECT COUNT(a) FROM Account a JOIN a.customer c WHERE a.customer.id = :customerId")
    int countAccountsByCustomerId(@Param("customerId") Long customerId);
}
