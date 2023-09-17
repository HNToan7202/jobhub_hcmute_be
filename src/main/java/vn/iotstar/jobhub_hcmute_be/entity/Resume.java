package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resume")
public class Resume implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String resumeId;

    //private String education;

    //@OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<Education> educations;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<Experience> experiences;


    //private String experience;

//    private String certificate;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<Certificate> certificates;

    //private String prize;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<Prize> prizes;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<Course> courses;

    //private String course;

    //private String project;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<Project> projects;

    //private String socialActivity;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<SocialActivity> socialActivities;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private List<Social> socials;

    @OneToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private Student student;

    private Date createAt;

    private Date updateAt;

    @JsonBackReference
    private Boolean isActive;

    @ManyToMany
    @JoinTable(name = "resume_skill",
            joinColumns = @JoinColumn(name = "resume_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @JsonBackReference
    @ToString.Exclude
    private List<Skill> skills;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private List<JobApply> jobApplies;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private List<ResumeUpload> resumeUploads;

}
