package com.saturn9er.notes.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NotesHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime modificationDateTime;

    @ManyToOne()
    @JoinColumn(name = "submitter_id")
    private User submitter;


    public NotesHistory() {
    }

    public NotesHistory(Note note) {
        setTitle(note.getTitle());
        setContent(note.getContent());
        setModificationDateTime(note.getModificationDateTime());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public LocalDateTime getModificationDateTime() {
        return modificationDateTime;
    }

    public void setModificationDateTime(LocalDateTime modificationDateTime) {
        this.modificationDateTime = modificationDateTime;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NotesHistory that = (NotesHistory) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(title, that.title)
                .append(content, that.content)
                .append(modificationDateTime, that.modificationDateTime)
                .append(submitter, that.submitter)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(title)
                .append(content)
                .append(modificationDateTime)
                .append(submitter)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "NotesHistory{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", modificationDateTime=" + modificationDateTime +
                ", submitter=" + submitter +
                '}';
    }
}
