package com.kiks.dishdashapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "users")
@Entity
public class user {

    @Id
    private int id;
    private String fullName;
    private String password;
    private String email;
    private String dateOfBirth;
}
