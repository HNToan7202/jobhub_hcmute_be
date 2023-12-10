package vn.iotstar.jobhub_hcmute_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Transactions;

import java.io.Serializable;

@Data
@Builder
public class TransactionModel implements Serializable {

    private String Id;

    private long createAt;

    private long time;

    private String status;

    private long amount;

    private String name;

    private String code;

    private String bank;

    private String employerId;

    private String employerName;

    private String employerEmail;

    private String employerPhone;

    private String employerAddress;

    public static TransactionModel transform(Transactions transactions) {
        return TransactionModel.builder()
                .Id(transactions.getId())
                .createAt(transactions.getCreateAt().getTime())
                .time(transactions.getTime())
                .status(transactions.getStatus())
                .amount(transactions.getAmount())
                .name(transactions.getName())
                .code(transactions.getCode())
                .bank(transactions.getBank())
                .employerId(transactions.getEmployer().getUserId())
                .employerName(transactions.getEmployer().getCompanyName())
                .employerEmail(transactions.getEmployer().getEmail())
                .employerAddress(transactions.getEmployer().getAddress().get(0))
                .employerPhone(transactions.getEmployer().getPhone())
                .build();
    }

}
