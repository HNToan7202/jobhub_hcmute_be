package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

import java.util.Date;

@Data
public class InterViewDTO {
    private Date startTime;
    private Date endTime;
    private String interviewLink;
    private String note;
}
