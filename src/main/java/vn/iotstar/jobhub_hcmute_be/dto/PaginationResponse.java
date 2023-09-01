package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationResponse {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private List<?> content;
}
