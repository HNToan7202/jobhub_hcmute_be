package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.JobType;
import vn.iotstar.jobhub_hcmute_be.constant.State;

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

    //Tên công việc
    @Nationalized
    private String name;

    //private String jobTitle;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    //thơi gian làm việc trong 1 ngày
    private String time;

    //link dẫn qua trang web của công ty
    private String link;

    private String logo;

    private Integer experience;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JsonBackReference
    @ToString.Exclude
    private Position position;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private Employer employer;

    //Để test xoá được hay không
    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    int status;
}
