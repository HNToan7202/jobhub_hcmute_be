package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "position")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Position implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String positionId;

    @Column(columnDefinition = "nvarchar(255)")
    @NotNull
    private String name;

    private Long amountPosition = 0L;

    private String icon;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private List<Job> jobs;

}
