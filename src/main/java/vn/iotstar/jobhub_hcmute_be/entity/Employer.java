package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employer extends User{
    @OneToMany(mappedBy = "employer")
    @JsonBackReference
    @ToString.Exclude
    private List<Job> jobs;

    @OneToMany(mappedBy = "employer")
    @JsonBackReference
    @ToString.Exclude
    private List<Event> events;

    @ElementCollection
    @CollectionTable(name = "recruiter_linkContacts", joinColumns = @JoinColumn(name = "recruiter_id"))
    @MapKeyColumn(name = "linkContact_key")
    @Column(name = "linkContact_value")
    private Map<String, String> linkContacts = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private EmployState employState;
}
