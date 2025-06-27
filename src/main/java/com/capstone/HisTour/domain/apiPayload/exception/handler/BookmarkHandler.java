package com.capstone.HisTour.domain.apiPayload.exception.handler;

import com.capstone.HisTour.domain.apiPayload.code.BaseErrorCode;
import com.capstone.HisTour.domain.apiPayload.exception.GeneralException;

public class BookmarkHandler extends GeneralException {

    public BookmarkHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
