package com.example.enums;

public enum SubscriptionStatus {
    ACTIVE("Активна"),
    EXPIRED("Истекла"),
    CANCELLED("Отменена"),
    PENDING("В ожидании");

    private final String displayName;

    SubscriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 