package com.webmuseum.museum.entity;

import java.util.ArrayList;
import java.util.Date;
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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String imgFileName;

    @Column(nullable = true, length = 1000)
    private String description;

    @Column(nullable = true)
    private Date date;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "event_category",
            joinColumns = { @JoinColumn(name = "event_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "category_id", referencedColumnName = "id") }
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "subscribers_event",
            joinColumns = { @JoinColumn(name = "event_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }
    )
    private List<User> subscribers = new ArrayList<>();
}
