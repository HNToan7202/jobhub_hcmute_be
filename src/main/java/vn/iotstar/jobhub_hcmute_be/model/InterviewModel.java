package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.Interview;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class InterviewModel implements Serializable {
    private String interviewId;
    private String note;
    private Date startTime;
    private Date endTime;
    private String interviewLink;
    private String jobApplyId;
    private String nameCandidate;

    public static InterviewModel transform(Interview interview) {
    	return InterviewModel.builder()
    			.interviewId(interview.getInterviewId())
    			.note(interview.getNote())
    			.startTime(interview.getStartTime())
    			.endTime(interview.getEndTime())
    			.interviewLink(interview.getInterviewLink())
    			.jobApplyId(interview.getJobApply().getJobApplyId())
                .nameCandidate(interview.getJobApply().getFullName())
    			.build();
    }

}
