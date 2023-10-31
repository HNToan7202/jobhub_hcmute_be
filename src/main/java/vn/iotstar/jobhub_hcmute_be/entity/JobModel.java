//package vn.iotstar.jobhub_hcmute_be.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.springframework.data.elasticsearch.annotations.Document;
//import vn.iotstar.jobhub_hcmute_be.constant.JobType;
//
//import java.util.Date;
//import java.util.List;
//
//@Document(indexName = "job")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class JobModel {
//
//    @Id
//    String id;
//
//    private String jobId;
//
//    private String name;
//
//    private String jobType;
//
//    //thơi gian làm việc trong 1 ngày
//    private String time;
//
//    //link dẫn qua trang web của công ty
//    private String link;
//
//    private String logo;
//
//    private Integer experience;
//
//    private String benefit;
//
//    private String salaryRange;
//
//    private String requirement;
//
//    private Integer quantity;
//
//    private String location;
//
//    private String description;
//
//    private Boolean isActive;
//
//    private Date createdAt;
//
//    private Date updatedAt;
//
//    private Date deadline;
//
//    private String position;
//
//    private List<String> skills;
//}
