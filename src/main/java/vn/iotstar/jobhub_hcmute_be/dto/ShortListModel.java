package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.ShortList}
 */
@Data
public class ShortListModel implements Serializable {
    String id;
    UserModel user;
    JobModel job;
    Date createdAt;
}