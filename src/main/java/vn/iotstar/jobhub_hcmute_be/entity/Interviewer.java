package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Interviewer extends User{
    @ManyToMany(mappedBy = "interviewer")
    @JsonBackReference
    @ToString.Exclude
    private List<Interview> interviews;

    @OneToMany(mappedBy = "interviewer")
    @JsonBackReference
    @ToString.Exclude
    private List<Question> questions;

    @OneToMany(mappedBy = "owner")
    @JsonBackReference
    @ToString.Exclude
    private List<QuestionCandidate> questionCandidates;
}
