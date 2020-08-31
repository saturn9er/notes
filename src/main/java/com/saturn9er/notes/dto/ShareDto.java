package com.saturn9er.notes.dto;

import com.saturn9er.notes.model.Permission;

public class ShareDto {
    private Long noteId;
    private String trusteeUsername;
    private String permission;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getTrusteeUsername() {
        return trusteeUsername;
    }

    public void setTrusteeUsername(String trusteeUsername) {
        this.trusteeUsername = trusteeUsername;
    }

    public Permission getPermissionValue() {
        return Permission.valueOf(permission);
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }



}
