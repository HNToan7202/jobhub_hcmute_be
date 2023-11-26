package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

import java.util.Date;

@Data
public class InterViewDTO {
    private Date time;
    private String startTime;
    private String endTime;
    private String interviewLink;
    private String note;
}
