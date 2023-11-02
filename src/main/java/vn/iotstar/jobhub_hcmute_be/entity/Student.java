package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Student extends User implements Serializable {

    @Nationalized
    private String fullName;

    private String avatar;

    private Date dateOfBirth;

    @Nationalized
    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(mappedBy = "student", fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private BlackList blackList;

    @OneToOne(mappedBy = "student", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private Resume resume;

    @OneToMany(mappedBy = "student")
    @JsonBackReference
    @ToString.Exclude
    private List<JobApply> jobApplies;

    @Size(min = 8, max = 8, message = "Student ID must have exactly 8 characters")
    @Pattern(regexp = "\\d{8}", message = "Student ID must be an 8-digit number")
    private String studentId;

}
