package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.*;

/**
 * Created by @ssz on 08.05.2021.
 */
@Entity
@Table(name = "users")
public class User implements HasID {
    public static final User SYSTEM_USER = EntityFactory.newUser(42L, "demo", "demo", 2);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login", nullable = false, unique = true)
    private String login;
    @Column(name = "pwd", nullable = false)
    private String password;
    @Column(name = "role")
    private Integer role;

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Integer getRole() {
        return role;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
