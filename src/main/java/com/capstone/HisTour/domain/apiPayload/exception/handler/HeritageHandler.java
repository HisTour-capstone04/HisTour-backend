package com.capstone.HisTour.domain.apiPayload.exception.handler;

import com.capstone.HisTour.domain.apiPayload.code.BaseErrorCode;
import com.capstone.HisTour.domain.apiPayload.exception.GeneralException;

public class HeritageHandler extends GeneralException {

    public HeritageHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
