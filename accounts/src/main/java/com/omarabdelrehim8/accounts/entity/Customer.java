package com.omarabdelrehim8.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor @RequiredArgsConstructor
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long customerId;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String mobileNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST)
    private List<Account> accounts;
}
