package vn.iotstar.jobhub_hcmute_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userJobViews")
public class UserJobViews {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobId")
    private Job job;

    private Integer views;

    private Date createdAt;
    private Date updatedAt;
    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
        this.views = 1;
    }
    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }
}
