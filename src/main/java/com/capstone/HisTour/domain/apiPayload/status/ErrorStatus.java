package com.capstone.HisTour.domain.apiPayload.status;

import com.capstone.HisTour.domain.apiPayload.code.BaseErrorCode;
import com.capstone.HisTour.domain.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // Normal Error
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // Member Error
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "MEMBER4003", "중복된 이메일이 존재합니다."),
    LOGIN_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBER4004", "유효하지 않은 이메일 혹은 비밀번호입니다."),

    // Heritage Error
    HERITAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "HERITAGE4001", "유적지가 없습니다."),

    // Bookmark Error
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK4001", "북마크가 없습니다."),
    BOOKMARK_DUPLICATE(HttpStatus.EXPECTATION_FAILED, "BOOKMARK4002", "이미 북마크된 유적지입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
