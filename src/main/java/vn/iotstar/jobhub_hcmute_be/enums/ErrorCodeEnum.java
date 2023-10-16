package vn.iotstar.jobhub_hcmute_be.enums;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCodeEnum {
    OK(200, 200, "OK/Success"),
    NO_CONTENT(204, 204, "Not have data"),
    BAD_REQUEST(400, 400, "Bad request"),
    NOT_FOUND(404, 404, "Not found"),
    UNAUTHORIZED(401, 401, "Unauthorized");
    private static Map<Integer, ErrorCodeEnum> map = new HashMap<>();
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