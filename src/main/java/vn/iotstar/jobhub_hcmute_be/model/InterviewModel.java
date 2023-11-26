package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.Interview;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class InterviewModel implements Serializable {
    private String interviewId;
    private String note;
    private Date time;
    private String startTime;
    private String endTime;
    private String interviewLink;
    private JobApply jobApply;
    private String nameCandidate;
    private String nameJob;
    private String jobId;

    public static InterviewModel transform(Interview interview) {
    	return InterviewModel.builder()
    			.interviewId(interview.getInterviewId())
    			.note(interview.getNote())
    			.startTime(interview.getStartTime())
    			.endTime(interview.getEndTime())
    			.interviewLink(interview.getInterviewLink())
    			.jobApply(interview.getJobApply())
                .nameCandidate(interview.getJobApply().getFullName())
                .nameJob(interview.getJobApply().getJob().getName())
                .jobId(interview.getJobApply().getJob().getJobId())
                .time(interview.getTime())
    			.build();
    }

}
