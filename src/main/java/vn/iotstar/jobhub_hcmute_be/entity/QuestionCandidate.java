package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questionCandidate")
public class QuestionCandidate implements Serializable {
    @EmbeddedId
    private QuestionCandidateId questionCandidateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("interviewId")
    @JsonBackReference
    @ToString.Exclude
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionId")
    @JsonBackReference
    @ToString.Exclude
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private Interviewer owner;

    private Float score;

    @Nationalized
    private String note;

    public QuestionCandidate(QuestionCandidateId questionCandidateId, Interview interview, Question question, Interviewer owner) {
        this.questionCandidateId = questionCandidateId;
        this.interview = interview;
        this.question = question;
        this.owner = owner;
    }
}
