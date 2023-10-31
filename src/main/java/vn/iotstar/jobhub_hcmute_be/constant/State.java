package vn.iotstar.jobhub_hcmute_be.constant;

public enum State {
    PENDING(1, "PENDING"),
    RECEIVED(2, "RECEIVED"),
    REJECTED(3, "REJECTED"),
    ACCEPTED(4, "ACCEPTED"),
    CANCELED(5, "CANCELED"),
    DONE(6, "DONE");

    int status;
    String statusName;

    State(int status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    //Đưa vào 1 số, trả về tên trạng thái
    public static State getStatusName(int status) {
        for (State state : State.values()) {
            if (state.getStatus() == status) {
                return state;
            }
        }
        return null;
    }
}
