package com.thullo.data.model;

import com.thullo.web.exception.BadRequestException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum BoardVisibility {
    PRIVATE,
    PUBLIC;


    public static BoardVisibility getBoardVisibility(String input) throws BadRequestException {
        List<BoardVisibility> visiblityList = Arrays.asList(
                PRIVATE,
                PUBLIC);

        for (BoardVisibility visibility : visiblityList) {
            if(Objects.equals(visibility.name(), input.toUpperCase())) return visibility;

        }
        throw new BadRequestException("Visibility Status does not Exist");
    }
}