package vn.iotstar.jobhub_hcmute_be.constant;

public enum Rating {
    NORMAL("normal", "Bình thường", 5000000),
    STANDARD("standard", "Tiêu chuẩn", 10000000),
    SILVER("silver", "Bạc", 20000000),
    GOLD("gold", "Vàng", 50000000),
    DIAMOND("diamond", "Kim cương", 100000000);

    private String code;
    private String name;
    private long amount;

    Rating(String code, String name, int amount) {
        this.code = code;
        this.name = name;
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public long getAmount() {
        return amount;
    }
    public static String getCode(long amount) {
        if (amount < 5000000) {
            return NORMAL.getCode();
        } else if (amount < 10000000) {
            return STANDARD.getCode();
        } else if (amount < 20000000) {
            return SILVER.getCode();
        } else if (amount < 50000000) {
            return GOLD.getCode();
        } else {
            return DIAMOND.getCode();
        }
    }
    public static String getName(String code) {
        for (Rating rating : Rating.values()) {
            if (rating.getCode().equals(code)) {
                return rating.getName();
            }
        }
        return null;
    }

}
