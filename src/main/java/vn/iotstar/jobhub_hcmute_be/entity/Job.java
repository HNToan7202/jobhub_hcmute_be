package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.JobType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job")
public class Job implements Serializable {

    // attribute
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String jobId;

    private String name;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private Integer quantity;

    @Column(columnDefinition = "Nvarchar(max)")
    private String benefit;

    @Column(columnDefinition = "Nvarchar(max)")
    private String salaryRange;

    @Column(columnDefinition = "Nvarchar(max)")
    private String requirement;

    @Nationalized
    private String location;

    @Column(columnDefinition = "Nvarchar(max)")
    private String description;

    private Boolean isActive;

    private Date createdAt;

    private Date updatedAt;

    private Date deadline;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private Employer employer;

    @OneToMany(mappedBy = "job",fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private List<JobApply> jobApplies;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "job_skill", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @JsonManagedReference
    private List<Skill> skills;

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }

    public Job(String name, JobType jobType, Integer quantity, String benefit, String salaryRange, String requirement, String location, String description, Date deadline, Position position, List<Skill> skills) {
        this.name = name;
        this.jobType = jobType;
        this.quantity = quantity;
        this.benefit = benefit;
        this.salaryRange = salaryRange;
        this.requirement = requirement;
        this.location = location;
        this.description = description;
        this.deadline = deadline;
        this.position = position;
        this.skills = skills;
    }

}
