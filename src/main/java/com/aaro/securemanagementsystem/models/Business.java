package com.aaro.securemanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "business")
@Data
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
