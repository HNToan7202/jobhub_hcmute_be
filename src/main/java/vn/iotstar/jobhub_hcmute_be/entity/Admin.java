package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Getter
@Setter
public class Admin extends User{
    @Nationalized
    String fullName;

    @OneToMany(mappedBy = "admin")
    @JsonBackReference
    @ToString.Exclude
    private List<Event> events;


}
