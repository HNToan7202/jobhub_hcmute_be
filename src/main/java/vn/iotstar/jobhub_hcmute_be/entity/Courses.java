package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String courseName;
    private String courseLink;
    private String image;
    private String description;
    //status là trạng thái của khóa học true là đang mở, false là đã đóng
    private boolean status;
    // active là trạng thái của khóa học true là đã kích hoạt, false là chưa kích hoạt
    private boolean active;
    private String type;
    private Date createdAt;
    private Date updatedAt;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private Employer employer;
    private String email;
    private boolean admin;
    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
    }
    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }
}
