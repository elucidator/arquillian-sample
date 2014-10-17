package com.cortez.samples.javaee7angular.business;

/**
 * Class SortDirection
 */
public enum SortDirection {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private final String direction;

    SortDirection(final String desc) {
        direction = desc;
    }

    public static SortDirection fromString(String text) {
        if (text != null) {
            for (SortDirection b : SortDirection.values()) {
                if (text.equalsIgnoreCase(b.direction)) {
                    return b;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return direction;
    }
}
