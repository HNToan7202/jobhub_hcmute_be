package vn.iotstar.jobhub_hcmute_be.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionBase {
    private String code;
    private long amount;
    private long totalMoney;
}
