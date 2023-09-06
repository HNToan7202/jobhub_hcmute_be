package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "skill")
public class Skill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String skillId;

    private String name;

    @ManyToMany(mappedBy = "skills")
    @JsonBackReference
    @ToString.Exclude
    private List<Resume> resumes;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private List<Job> jobs;

    @OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private List<Question> questions;

}
