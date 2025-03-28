package com.capstone.HisTour.global;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DefaultResponse<T> {

    private String responseMessage;
    private T data;

    public DefaultResponse(final String responseMessage) {
        this.responseMessage = responseMessage;
        this.data = null;
    }

    public static <T> DefaultResponse<T> response(final String responseMessage){
        return response(responseMessage, null);
    }

    public static <T> DefaultResponse<T> response(final String responseMessage, final T data){
        return DefaultResponse.<T>builder()
                .responseMessage(responseMessage)
                .data(data)
                .build();
    }
}
