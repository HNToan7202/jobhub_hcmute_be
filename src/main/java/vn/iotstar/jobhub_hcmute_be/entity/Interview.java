package vn.iotstar.jobhub_hcmute_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interview")
public class Interview implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String interviewId;

	private Date time;
	
	private String interviewLink;

	@OneToOne
	@JoinColumn(name = "jobApply_id")
	@JsonBackReference
	@ToString.Exclude
	private JobApply jobApply;

	@ManyToMany
	@JoinTable(
			name = "interview_interviewer",
			joinColumns = @JoinColumn(name = "interview_id"),
			inverseJoinColumns = @JoinColumn(name = "interviewer_id")
	)
	@JsonBackReference
	@ToString.Exclude
	private List<Interviewer> interviewer;

	private Float totalScore;


	@OneToMany(mappedBy = "interview")
	@JsonBackReference
	@ToString.Exclude
	private List<QuestionCandidate> questionCandidates;

}
