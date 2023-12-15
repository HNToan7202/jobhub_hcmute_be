package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;
import vn.iotstar.jobhub_hcmute_be.constant.Rating;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employer extends User implements Serializable {

    @OneToMany(mappedBy = "employer")
    @JsonBackReference
    @ToString.Exclude
    private List<Job> jobs;

    @ElementCollection
    @CollectionTable(name = "recruiter_linkContacts", joinColumns = @JoinColumn(name = "recruiter_id"))
    @MapKeyColumn(name = "linkContact_key")
    @Column(name = "linkContact_value")
    private Map<String, String> linkContacts = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private EmployState employState;

    @Nationalized
    private String companyName;

    @ElementCollection
    private List<String> backGround;

    @ElementCollection
    @Nationalized
    private List<String> address;

    private String logo;

    private String website;

    private String foundedYear;

    private String teamSize;

    @OneToMany(mappedBy = "employer")
    @JsonBackReference
    @ToString.Exclude
    private List<Transactions> transactions;

    private Boolean isTransaction = false;

    private Long transactionMoney = 0L;

    private String sponsor = Rating.NORMAL.getCode().toString();


}
