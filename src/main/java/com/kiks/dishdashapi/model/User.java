package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
package com.kiks.dishdashapi.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private Boolean isVerified = false;
}
