package vn.iotstar.jobhub_hcmute_be.constant;

public enum Transaction {
    BASIC_PACKAGE("BASIC", 5000000, "Gói cơ bản"),
    STANDARD_PACKAGE("STANDARD", 10000000, "Gói tiêu chuẩn"),
    PREMIUM_PACKAGE("PREMIUM", 20000000, "Gói cao cấp");

    private String code;
    private long amount;
    private String name;

    Transaction(String code, long amount, String name) {
        this.code = code;
        this.amount = amount;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public long getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public static long getAmount(String code) {
        for (Transaction transaction : Transaction.values()) {
            if (transaction.getCode().equals(code)) {
                return transaction.getAmount();
            }
        }
        return 0;
    }

    public static String getName(String code) {
        for (Transaction transaction : Transaction.values()) {
            if (transaction.getCode().equals(code)) {
                return transaction.getName();
            }
        }
        return null;
    }

    public static String getCode(long amount) {
        for (Transaction transaction : Transaction.values()) {
            if (transaction.getAmount() == amount) {
                return transaction.getCode();
            }
        }
        return null;
    }
}
