package com.saturn9er.notes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Note {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<NotesHistory> history = new ArrayList<>();

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User owner;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "trustee")
    private List<Share> shares = new ArrayList<>();

    @Column(nullable = false, length = 50)
    @Length(min = 1, max = 50)
    private String title;

    @Column(nullable = false, length = 1000)
    @Length(min = 1, max = 1000)
    private String content;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime creationDateTime;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime modificationDateTime;

    public Note() { }

    public Note(String title, String content) {
        final LocalDateTime now = LocalDateTime.now();
        setTitle(title);
        setContent(content);
        setCreationDateTime(now);
        setModificationDateTime(now);
    }

    public Note(String title, String content, LocalDateTime creationDateTime, LocalDateTime modificationDateTime) {
        setTitle(title);
        setContent(content);
        setCreationDateTime(creationDateTime);
        setModificationDateTime(modificationDateTime);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<NotesHistory> getHistory() {
        return history;
    }

    public void setHistory(List<NotesHistory> history) {
        this.history = history;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getModificationDateTime() {
        return modificationDateTime;
    }

    public void setModificationDateTime(LocalDateTime modificationDateTime) {
        this.modificationDateTime = modificationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        return new EqualsBuilder()
                .append(id, note.id)
                .append(owner, note.owner)
                .append(title, note.title)
                .append(creationDateTime, note.creationDateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(owner)
                .append(title)
                .append(creationDateTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", owner=" + owner +
                ", title='" + title + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", modificationDateTime=" + modificationDateTime +
                '}';
    }
}
