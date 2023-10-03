package vn.iotstar.jobhub_hcmute_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ShortList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String resume;

    @OneToOne
    private Job job;

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
}
