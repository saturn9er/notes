package com.saturn9er.notes.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

@Entity
@Table(name = "share")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trustee_id")
    private User trustee;

    @Enumerated(EnumType.ORDINAL)
    private Permission permissions;

    public Share() {
    }

    public Share(Note note, User trustee, Permission permission) {
        setNote(note);
        setTrustee(trustee);
        setPermissions(permission);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public User getTrustee() {
        return trustee;
    }

    public void setTrustee(User trustee) {
        this.trustee = trustee;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Share share = (Share) o;

        return new EqualsBuilder()
                .append(id, share.id)
                .append(note, share.note)
                .append(trustee, share.trustee)
                .append(permissions, share.permissions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(note)
                .append(trustee)
                .append(permissions)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Share{" +
                "id=" + id +
                ", note=" + note +
                ", granter=" + note.getOwner().getUsername() +
                ", trustee=" + trustee +
                ", permissions=" + permissions +
                '}';
    }
}

