package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Student extends User{
    @OneToOne(mappedBy = "student", fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private BlackList blackList;

    @OneToMany(mappedBy = "student")
    @JsonBackReference
    @ToString.Exclude
    private List<Resume> resumes;

    @OneToMany(mappedBy = "student")
    @JsonBackReference
    @ToString.Exclude
    private List<ResumeUpload> resumeUploads;

    @OneToMany(mappedBy = "student")
    @JsonBackReference
    @ToString.Exclude
    private List<JobApply> jobApplies;

}
