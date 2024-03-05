package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;

    @Column(columnDefinition = "nvarchar(255)")
    private String content;

    private String photos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @JsonBackReference
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;
    private int totalLike;
    private int totalLove;
    private int totalHaha;
    private int totalWow;
    private int totalSad;
    private int totalAngry;
    private int totalEmotions;


    @OneToMany(mappedBy = "commentReply", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment> subComments;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_reply_id")
    private Comment commentReply;
    private Boolean isReply;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shareId")
    private Share share;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private Date createdAt;
    private Date updatedAt;
    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
        this.totalLike = 0;
        this.totalLove = 0;
        this.totalHaha = 0;
        this.totalWow = 0;
        this.totalAngry = 0;
        this.totalSad = 0;
        this.totalEmotions = 0;

    }
    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }



}
