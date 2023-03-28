package com.thullo.data.model;

public enum BoardVisibility {
    PRIVATE,
    PUBLIC;

    public static BoardVisibility getBoardVisibility(String input){
        var boardVisibilityList = BoardVisibility.values();

        for (BoardVisibility visibility : boardVisibilityList) {
            if (visibility.name().equalsIgnoreCase(input)) return visibility;
        }
        return PRIVATE;
    }
}