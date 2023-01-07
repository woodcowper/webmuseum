package com.webmuseum.museum.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "collections")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(targetEntity=ExhibitAuthor.class, mappedBy="collection")
    private List<ExhibitAuthor> exhibitAuthors = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name="author_id", referencedColumnName = "id", nullable=false)
    private Author author;

    @OneToMany(targetEntity=CollectionDescription.class, mappedBy="collection", cascade = CascadeType.ALL)
    private List<CollectionDescription> descriptions = new ArrayList<>();
}
