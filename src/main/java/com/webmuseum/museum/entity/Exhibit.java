package com.webmuseum.museum.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "exhibits")
public class Exhibit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String imgFileName;

    @Column(nullable = true)
    private String qrFileName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "exhibit_category",
            joinColumns = { @JoinColumn(name = "exhibit_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "category_id", referencedColumnName = "id") }
    )
    private List<Category> categories = new ArrayList<>();

    @OneToMany(
        targetEntity=ExhibitAuthor.class,
        mappedBy = "exhibit",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ExhibitAuthor> authors = new ArrayList<>();

    @OneToMany(targetEntity=ExhibitDescription.class, mappedBy="exhibit", cascade = CascadeType.ALL)
    private List<ExhibitDescription> descriptions = new ArrayList<>();

}
