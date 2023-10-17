package vn.iotstar.jobhub_hcmute_be.enums;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCodeEnum {
    OK(200, 200, "OK/Success"),
    NO_CONTENT(204, 204, "Not have data"),
    BAD_REQUEST(400, 400, "Bad request"),
    NOT_FOUND(404, 404, "Not found"),
    UNAUTHORIZED(401, 401, "Unauthorized"),

    INTERNAL_SERVER_ERROR(500, 500, "Internal server error"),

    GET_ALL_JOB_SUCCESS(200, 200, "Get all job success"),

    GET_JOB_BY_EMPLOYER_SUCCESS(200, 200, "Get job by employer success"),

    GET_JOB_DETAIL_SUCCESS(200, 200, "Get job detail success"),

    GET_SKILL_SUCCESS(200, 200, "Get skill success"),

    DUPPLICATE_SKILL(409, 409, "Duplicate skill"),

    NOT_FOUND_SKILL(404, 404, "Not found skill"),

    UPDATE_JOB_SUCCESS(200, 200, "Update job success"),

    UPDATE_JOB_FAILED(400, 400, "Update job failed"),

    GET_POSITION_SUCCESS(200, 200, "Get position success"),

    POST_JOB_SUCCESS(200, 200, "Post job success");


    private static final Map<Integer, ErrorCodeEnum> map = new HashMap<>();
    static {
        for (ErrorCodeEnum returnCode : ErrorCodeEnum.values()) {
            map.put(returnCode.code, returnCode);
        }
    }
    private int status;
    private int code;
    private String message;

    ErrorCodeEnum(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
    public static ErrorCodeEnum valueOf(int code) {
        return map.get(code);
    }
    public int getStatus() {
        return status;
    }
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(status);
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}