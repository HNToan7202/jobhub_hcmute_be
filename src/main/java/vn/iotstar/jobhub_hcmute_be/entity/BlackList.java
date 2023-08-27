package vn.iotstar.jobhub_hcmute_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlackList implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String blackId;
    @Nationalized
    private String reason;

    private Date date;

    @OneToOne
    @JoinColumn(name = "candidateId")
    @ToString.Exclude
    private Student student;

    @PrePersist
    void date() {
        this.date = new Date();
    }

    @OneToMany(mappedBy = "blackList")
    private List<Reason> reasons;


}
