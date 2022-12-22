package com.webmuseum.museum.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ExhibitAuthorId implements Serializable {
    @Column(name = "exhibit_id")
    private Long exhibitId;
 
    @Column(name = "author_id")
    private Long authorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        ExhibitAuthorId that = (ExhibitAuthorId) o;
        return Objects.equals(exhibitId, that.exhibitId) &&
               Objects.equals(authorId, that.authorId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(exhibitId, authorId);
    }
    
}
