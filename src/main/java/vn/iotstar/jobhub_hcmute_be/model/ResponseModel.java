package vn.iotstar.jobhub_hcmute_be.model;




import lombok.Data;
import org.springframework.http.HttpStatus;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;

import java.io.Serializable;

@Data
public class ResponseModel implements Serializable {
    private final HttpStatus status;
    private final int code;
    private String message;
    private final long time = System.currentTimeMillis();
    private Object result;

    public ResponseModel() {
        this(ErrorCodeEnum.OK);
    }

    public ResponseModel(final ErrorCodeEnum returnCode) {
        this.status = returnCode.getHttpStatus();
        this.code = returnCode.getCode();
        this.message = returnCode.getMessage();
    }

    public ResponseModel(Object data) {
        this(ErrorCodeEnum.OK);
        this.result = data;
    }

    public ResponseModel(final ErrorCodeEnum returnCode, Object data) {
        this(returnCode);
        this.result = data;
    }

    public ResponseModel(final HttpStatus status, final int code, final String message, Object data) {
        this.status = status;
        this.code = code;
        this.message = message == null ? "" : message;
        this.result = data;
    }

    public ResponseModel setResponse(final Object data) {
        this.result = data;
        return this;
    }

    @Override
    public String toString() {
        return "ResponseModel{" + "status=" + status + ", code=" + code + ", message='" + message + '\'' + ", time="
                + time + ", data=" + result + '}';
    }
}
