package com.teamforone.tech_store.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

@Getter
@Setter
@Builder
public class Response<T> {
    private int status;
    private String message;
    private T data;
}
