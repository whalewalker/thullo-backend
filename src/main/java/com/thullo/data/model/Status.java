package com.thullo.data.model;

import lombok.Getter;

@Getter
public enum Status {
    BACKLOG("backlog"),
    IN_PROGRESS("in progress"),
    IN_REVIEW("in review"),
    COMPLETED("completed");

    private final String content;

    Status(String content) {
        this.content = content;
    }

    public static Status getStatus(String content) {
        for (Status status : Status.values())
            if (status.getContent().equals(content))
                return status;
        return Status.BACKLOG;
    }
}
