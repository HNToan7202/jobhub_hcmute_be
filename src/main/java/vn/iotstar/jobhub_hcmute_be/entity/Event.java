package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Nationalized
    @Column(columnDefinition = "Nvarchar(max)")
    private String title;
    @Nationalized
    @Column(columnDefinition = "Nvarchar(max)")
    private String description;
    @Nationalized
    @Column(columnDefinition = "Nvarchar(max)")
    private String location;
    private String img;
    private Date createdAt;
    private LocalDate startAt;
    private LocalDate deadline;
    private Date updatedAt;
    private LocalTime time;
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private Employer employer;


    @PrePersist
    void createdAt() {
        this.createdAt = this.updatedAt = new Date();
        if (this.time == null){
            this.time = LocalTime.parse("00:00:00");
        }
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }


}
