package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import vn.iotstar.jobhub_hcmute_be.entity.Friend;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PageModel {
    private int currentPage;
    private int totalItems;
    private int totalPage;
    private Object data;

    public static PageModel transform(Page page) {
        return PageModel.builder()
                .currentPage(page.getNumber())
                .totalItems((int) page.getTotalElements())
                .totalPage(page.getTotalPages())
                .data(page.getContent())
                .build();
    }
    public static PageModel transform(Page page,Object data ) {
        return PageModel.builder()
                .currentPage(page.getNumber())
                .totalItems((int) page.getTotalElements())
                .totalPage(page.getTotalPages())
                .data(data)
                .build();
    }
    public static PageModel transform(int currentPage, int totalItems, int totalPage, Object data) {
        return PageModel.builder()
                .currentPage(currentPage)
                .totalItems(totalItems)
                .totalPage(totalPage)
                .data(data)
                .build();
    }

}
