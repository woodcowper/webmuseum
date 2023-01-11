package com.webmuseum.museum.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "languages")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @OneToMany(targetEntity=CategoryDescription.class, mappedBy="language")
    private List<CategoryDescription> categoryDescriptions = new ArrayList<>();

    @OneToMany(targetEntity=CollectionDescription.class, mappedBy="language")
    private List<CollectionDescription> collectionDescriptions = new ArrayList<>();

    @OneToMany(targetEntity=AuthorDescription.class, mappedBy="language")
    private List<AuthorDescription> authorDescriptions = new ArrayList<>();

    @OneToMany(targetEntity=ExhibitDescription.class, mappedBy="language")
    private List<ExhibitDescription> exhibitDescriptions = new ArrayList<>();
}
