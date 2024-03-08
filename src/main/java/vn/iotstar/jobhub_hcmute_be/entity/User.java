package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

//    @Nationalized
//    private String fullName;

    //Ẩn đi khi hiển thị json
    @JsonBackReference
    private String password;

    private String phone;

    @Email
    private String email;

    @Column(nullable = true)
    private boolean isVerified = false;

//    @Nationalized
//    private String address;

    @Nationalized
    @Column(columnDefinition = "Nvarchar(max)")
    private String about;

    private Boolean isActive = true;

    private Date createdAt;

    private Date updatedAt;

    private Date lastLoginAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<RefreshToken> refreshTokens;

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
        this.friendCount = 0;
        this.senderCount = 0;
        this.receiverCount = 0;
        this.isPublicFriend = true;

    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }

    @JsonBackReference
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private VerificationToken verificationToken;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Share> shares;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Like> likes;
    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments;

    //senders la danh sách ma user này gửi lời mời kết bạn
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<Friend> senders;
    private Integer senderCount;

    //receivers là danh sách gửi lời mời kết bạn với user này
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Friend> receivers;
    private Integer receiverCount;

    private Integer friendCount;
    private Boolean isPublicFriend;

//  following: Danh sách các người dùng mà người dùng hiện tại đang theo dõi.
//  followers: Danh sách các người dùng khác đang theo dõi người dùng hiện tại.
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follower> following;
    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL)
    private List<Follower> followers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FriendUser> friends;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserJobViews> userJobViews;


}
