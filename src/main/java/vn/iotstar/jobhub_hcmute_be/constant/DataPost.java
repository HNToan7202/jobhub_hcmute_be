package vn.iotstar.jobhub_hcmute_be.constant;

public enum DataPost {
    Last_hour("Last hour",1),
    Last_24_hours("Last 24 hours",24),
    Last_7_days("Last 7 days",168),
    Last_14_days("Last 14 days",336),
    Last_30_days("Last 30 days",720),
    All_time("All time",0);

    private String name;
    private int value;

    DataPost(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
