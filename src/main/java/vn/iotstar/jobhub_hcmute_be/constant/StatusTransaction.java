package vn.iotstar.jobhub_hcmute_be.constant;

public enum StatusTransaction {
     PENDING("pending", "Đang chờ"),
    SUCCESS("success", "Thành công"),
    CANCEL("cancel", "Hủy");
    private String code;
    private String name;


    StatusTransaction(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
