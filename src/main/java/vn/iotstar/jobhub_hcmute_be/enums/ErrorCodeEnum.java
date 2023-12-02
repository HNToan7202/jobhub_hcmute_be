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

    JOB_EXISTED(409, 409, "Job existed"),

    GET_JOB_BY_FILTER_SUCCESS(200, 20021, "Get job by filter success"),

    GET_JOB_APPLY_SUCCESSFULLY(200, 200, "Get job apply successfully"),

    GET_POSITION_SUCCESS(200, 200, "Get position success"),

    APPLICATION_SUCCESSFULLY(200, 200, "Application successfully"),

    CV_NOT_FOUND(404, 404, "CV not found"),

    ALREADY_APPLY(409, 409, "Already apply"),

    JOB_NOT_FOUND(404, 404, "Job not found"),

    CANDIDATE_NOT_FOUND(404, 404, "Candidate not found"),

    JOB_EXPIRED(400, 400, "Job expired"),

    RESET_PASSWORD_SUCCESS(200, 2007, "Reset password success"),

    USER_NOT_FOUND(404, 404, "User not found"),

    CHECK_MAIL_TO_RESET_PASSWORD(200, 200021, "Please check your email to reset your password!"),

    UPDATE_STATE_APPLY_SUCCESSFULLY(200, 20022, "Update state apply successfully"),

    INVALID_STATE_VALUE(400, 400, "Invalid state value"),

    UPDATE_PROFILE_SUCCESS(200, 200, "Update profile success"),

    GET_APPLY_BY_STATE_SUCCESSFULLY(200, 20022, "Get apply by state successfully"),

    SEND_MAIL_SUCCESSFULLY(200, 20220, "Send mail successfully"),

    GET_PROFILE_SUCCESSFULLY(200, 200, "Get profile successfully"),

    MAXIMUM_BACKGROUND(400, 400, "Maximum background"),

    UPDATE_BACKGROUND_SUCCESSFULLY(200, 200, "Update background successfully"),

    USER_IS_TRANSACTION(400, 400, "Customers are still making transactions, please complete the transaction or try again after 15 minutes"),

    TRANSACTION_SAVED(200, 200, "Transaction saved"),

    DETECTION_OF_VIOLATIONS(400, 400, "Detection of violations"),

    PAGE_INDEX_MUST_NOT_BE_LESS_THAN_0(400, 400, "Page index must not be less than 0"),


    GET_ALL_USER_SUCCESSFULLY(200, 200, "Get all user successfully"),

    GET_ADMIN_PROFILE_SUCCESSFULLY(200, 200, "Get admin profile successfully"),

    USER_IS_ACTIVED(400, 400, "User is actived"),

    EMPLOYR_ACCEPT_SUCCESS(200, 200, "Accept employer success"),

    EMPLOYER_NOT_ACTIVE_SUCCESS(200, 200, "Employer not active success"),

    ADMIN_NOT_FOUND(404, 404, "Admin not found"),

    POST_EVENT_SUCCESS(200, 200, "Post event success"),

    GET_ALL_EMPLOYER_SUCCESSFULLY(200, 200, "Get all employer successfully"),

    GET_PROFILE_EMPLOYER_SUCCESSFULLY(200, 200, "Get profile employer successfully"),

    CREATE_INTERVIEW_SUCCESSFULLY(200, 200, "Create interview successfully"),

    INTERVIEW_HAS_BEEN_CREATED(400, 400, "Interview has been created"),

    INTERVIEW_NOT_FOUND(404, 404, "Interview not found"),

    DELETE_SHORT_LIST_SUCCESS(200, 200, "Delete short list success"),
    ADD_SHORT_LIST_SUCCESS(200, 200, "Add short list success"),

    FAIL_ADD_BLACKLIST(400, 400, "Fail add blacklist"),

    GET_DASHBOARD_SUCCESS(200, 200, "Get dashboard success"),
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