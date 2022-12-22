package com.webmuseum.museum.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
@Table(name = "exhibit_author")
public class ExhibitAuthor {
    @EmbeddedId
    private ExhibitAuthorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exhibitId")
    private Exhibit exhibit;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("authorId")
    private Author author;

    @ManyToOne
    @JoinColumn(name="collection_id", referencedColumnName = "id", nullable=true)
    private Collection collection;
    
}
