package com.capstone.HisTour.domain.apiPayload.exception.handler;

import com.capstone.HisTour.domain.apiPayload.code.BaseErrorCode;
import com.capstone.HisTour.domain.apiPayload.exception.GeneralException;

public class MemberHandler extends GeneralException {

    public MemberHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
