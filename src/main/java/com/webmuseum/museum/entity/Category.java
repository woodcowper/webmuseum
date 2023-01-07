package com.webmuseum.museum.entity;

import java.util.ArrayList;
import java.util.List;

import com.webmuseum.museum.models.ECategoryType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    private ECategoryType type;

    @ManyToMany(targetEntity=Exhibit.class, mappedBy = "categories")
    private List<Exhibit> exhibits = new ArrayList<>();

    @ManyToMany(targetEntity=Event.class, mappedBy = "categories")
    private List<Event> events = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "subscribers_event_category",
            joinColumns = { @JoinColumn(name = "category_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }
    )
    private List<User> subscribers = new ArrayList<>();


    @OneToMany(targetEntity=CategoryDescription.class, mappedBy="category", cascade = CascadeType.ALL)
    private List<CategoryDescription> descriptions = new ArrayList<>();

}
