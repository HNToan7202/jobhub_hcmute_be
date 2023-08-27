package vn.iotstar.jobhub_hcmute_be.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;

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

    @Nationalized
    private String fullName;

    //Ẩn đi khi hiển thị json
    @JsonBackReference
    private String password;

    private String phone;

    @Email
    private String email;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = true)
    private boolean isVerified = false;

    @Nationalized
    private String address;

    private Date dateOfBirth;

    @Nationalized
    private String about;

    private boolean isActive = true;

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
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }

    @JsonBackReference
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private VerificationToken verificationToken;
}
