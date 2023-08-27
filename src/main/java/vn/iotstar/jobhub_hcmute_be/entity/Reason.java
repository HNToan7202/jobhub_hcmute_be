package vn.iotstar.jobhub_hcmute_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reasonId;

    private String reasonName;

    private int code;

    @ManyToOne
    @JoinColumn(name = "blackListId")
    private BlackList blackList;
}
