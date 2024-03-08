package vn.iotstar.jobhub_hcmute_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follower")
public class Follower  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String followerId;

    //follower người thực hiện theo dõi
    @ManyToOne
    @JoinColumn(name = "follower_id_user")
    private User follower;

    //followee người được theo dõi
    @ManyToOne
    @JoinColumn(name = "followee_id")
    private User followee;

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
