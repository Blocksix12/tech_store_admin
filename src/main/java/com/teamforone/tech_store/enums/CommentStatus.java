package com.teamforone.tech_store.enums;


public enum CommentStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static CommentStatus fromString(String value) {
        for (CommentStatus status : CommentStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) return status;
        }
        return null;
    }
}
