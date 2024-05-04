package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

@Data
public class RecommendJobWithCVDTO {
    private String job_id;
    private String job_type;
    private String name;
    private String requirement;
    private String description;
    private String benefit;
    private String location;
    private String logo;
    private String salary_range;
    private int quantity;
    private int experience;
    private String time;
    private String All;
    private String positionName;
    private double KNN;
    private double TF_IDF;
    private double CV;
    private double Final;
}
