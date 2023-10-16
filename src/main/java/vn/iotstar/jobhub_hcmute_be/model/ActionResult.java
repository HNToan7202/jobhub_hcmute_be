package vn.iotstar.jobhub_hcmute_be.model;


import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;

@Data
public class ActionResult {
    private ErrorCodeEnum errorCode;
    private Object data ;
}
