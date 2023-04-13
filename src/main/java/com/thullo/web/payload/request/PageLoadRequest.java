package com.thullo.web.payload.request;

import lombok.Getter;

@Getter
public class PageLoadRequest {
    private int pageSize;
    private int pageNumber;
}
