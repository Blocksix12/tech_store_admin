package com.teamforone.tech_store.dto.response;

<<<<<<< HEAD
import lombok.*;
=======
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;
>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb

@Getter
@Setter
@Builder
<<<<<<< HEAD
@AllArgsConstructor  // ← Thêm dòng này
@NoArgsConstructor
public class Response {
=======
public class Response<T> {
>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb
    private int status;
    private String message;
    private T data;
}
