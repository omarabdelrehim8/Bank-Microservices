package com.omarabdelrehim8.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Id
    private Long accountNumber;

    private String accountType;

    private String branchAddress;

    private Boolean communicationSw;
}
