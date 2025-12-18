package com.teamforone.tech_store.enums;

public enum Gender {
    Nam("Nam"),
    Nu("Nữ"),
    Khac("Khác");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }
}
