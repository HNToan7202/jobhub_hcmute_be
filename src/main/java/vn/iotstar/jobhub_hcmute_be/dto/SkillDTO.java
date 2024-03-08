package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class SkillDTO {
    String id;
    String name;

    Boolean isEdit = false;
    Boolean isDelete = false;
}
