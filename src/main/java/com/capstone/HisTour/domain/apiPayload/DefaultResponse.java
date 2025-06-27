package com.capstone.HisTour.domain.apiPayload;

import com.capstone.HisTour.domain.apiPayload.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "responseMessage", "data"})
public class DefaultResponse<T> {

    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> DefaultResponse<T> response(final String responseMessage){
        return response(responseMessage, null);
    }

    public static <T> DefaultResponse<T> onSuccess(T result){
        return new DefaultResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), result);
    }

    public static <T> DefaultResponse<T> onFailure(String code, String message, T result) {
        return new DefaultResponse<> (false, code, message, result);
    }

    public static <T> DefaultResponse<T> response(final String responseMessage, final T result){
        return DefaultResponse.<T>builder()
                .message(responseMessage)
                .result(result)
                .build();
    }
}
