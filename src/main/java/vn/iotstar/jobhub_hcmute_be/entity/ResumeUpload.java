package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String resumeId;

    @Nationalized
    private String name;

    private String linkUpload;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private Resume resume;

    private Boolean isMain;

    @JsonBackReference
    private Boolean isActive;

    private Date createAt;

    private Date updateAt;

    @PrePersist
    void createdAt() {
        this.createAt = new Date();
    }

    @PreUpdate
    void updatedAt() {
        this.updateAt = new Date();
    }

}
