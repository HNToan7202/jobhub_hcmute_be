package vn.iotstar.jobhub_hcmute_be.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionCandidateId implements Serializable {
    private String interviewId;
    private String questionId;

    public QuestionCandidateId(String interviewId, String questionId) {
        this.interviewId = interviewId;
        this.questionId = questionId;
    }


    public QuestionCandidateId() {

    }

    public String getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(String interviewId) {
        this.interviewId = interviewId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof QuestionCandidateId that)) return false;
        return Objects.equals(getQuestionId(), that.getQuestionId()) &&
                Objects.equals(getInterviewId(), that.getInterviewId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionId(), getInterviewId());
    }
}
