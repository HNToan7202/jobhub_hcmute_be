package vn.iotstar.jobhub_hcmute_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iotstar.jobhub_hcmute_be.enums.PrivacyLevel;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "share")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shareId;
    @Column(columnDefinition = "nvarchar(255)")
    private String content;

    @Enumerated(EnumType.STRING)
    private PrivacyLevel privacyLevel;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;
    private int totalLike;
    private int totalLove;
    private int totalAngry;
    private int totalHappy;
    private int totalSad;
    private int totalSurprise;
    private int totalFear;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    private int totalComment;

    private Date createdAt;
    private Date updatedAt;

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
        this.totalLike = 0;
        this.totalLove = 0;
        this.totalAngry = 0;
        this.totalHappy = 0;
        this.totalSad = 0;
        this.totalSurprise = 0;
        this.totalFear = 0;
        this.totalComment = 0;
    }
    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }
}
