package com.example.book.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "emails", unique = true)
    private String email;
    @NotNull
    @Column(name = "passwords")
    private String password;
    @NotNull
    @Column(name = "first_names")
    private String firstName;
    @NotNull
    @Column(name = "last_names")
    private String lastName;
    @Column(name = "shipping_addresses")
    private String shippingAddress;
}
