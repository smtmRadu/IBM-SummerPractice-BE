package com.solid.solidbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solid.solidbackend.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonProperty("name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("role")
    private Role role;

    @Column(name = "picture_url")
    @JsonProperty("pictureUrl")
    private String pictureUrl;

    public User() {
    }


    public User(String name, Role role, String pictureUrl) {
        this.name = name;
        this.role = role;
        this.pictureUrl = pictureUrl;
    }

    public User(Long id, String name, Role role, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.pictureUrl = pictureUrl;
    }
}

