package com.webmuseum.museum.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private Date birthDate;

    @Column(nullable = true)
    private Date dieDate;

    @OneToMany(
        targetEntity=ExhibitAuthor.class,
        mappedBy = "author",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ExhibitAuthor> exhibits = new ArrayList<>();
}
