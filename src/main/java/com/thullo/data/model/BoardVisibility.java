package com.thullo.data.model;

public enum BoardVisibility {
    PRIVATE,
    PUBLIC;

    public static BoardVisibility getBoardVisibility(String input){

        for (BoardVisibility visibility : BoardVisibility.values()) {
            if (visibility.name().equalsIgnoreCase(input))
                return visibility;
        }
        return null;
    }
}