package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import vn.iotstar.jobhub_hcmute_be.constant.State;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobApply")
public class JobApply implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String jobApplyId;

    private String email;

    private String phone;

    @Nationalized
    private String fullName;

    @Nationalized
    private String address;

    @Nationalized
    private String note;

    private Date createdAt;

    private Date updatedAt;

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private Job job;

    @ManyToOne(fetch = FetchType.EAGER )
    @JsonBackReference
    @ToString.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private Resume resume;

    @OneToOne(mappedBy = "jobApply", fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private Interview interview;

    @Enumerated(EnumType.STRING)
    private State state;

    private String resumeUpoad;

}
