package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iotstar.jobhub_hcmute_be.enums.PrivacyLevel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @JsonBackReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;
    private int havePictures ;


    private String files;
    private Integer haveFiles;

    @Column(columnDefinition = "nvarchar(255)")
    private String location;
    @Column(columnDefinition = "nvarchar(255)")
    private String content;

    @Enumerated(EnumType.STRING)
    private PrivacyLevel privacyLevel;

    private Date createdAt;
    private Date updatedAt;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    // orphanRemoval = true : khi xóa bài viết thì xóa hết share
    @JsonBackReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Share> share;
    private int totalShare;

    @JsonBackReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;
    private int totalLike;
    private int totalLove;
    private int totalHaha;
    private int totalWow;
    private int totalSad;
    private int totalAngry;
    private int totalEmotions;

    @JsonBackReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    private int totalComment;

    private Boolean isActive;

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
        this.totalLike = 0;
        this.totalLove = 0;
        this.totalHaha = 0;
        this.totalWow = 0;
        this.totalAngry = 0;
        this.totalSad = 0;
        this.totalShare = 0;
        this.totalComment = 0;
        this.totalEmotions = 0;
        this.isActive = true;
    }
    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }

}
