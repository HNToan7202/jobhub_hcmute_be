package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.TypeQuestion;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question")
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String questionId;

    @Nationalized
    private String content;

    @Nationalized
    private String note;

    @Enumerated(EnumType.STRING)
    private TypeQuestion typeQuestion;

    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private Interviewer interviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Skill skill;

    @OneToMany(mappedBy = "question")
    @JsonBackReference
    @ToString.Exclude
    private List<QuestionCandidate> questionCandidates;
}
