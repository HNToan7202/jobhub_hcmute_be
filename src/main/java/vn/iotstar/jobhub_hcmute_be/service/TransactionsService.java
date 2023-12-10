package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Pageable;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface TransactionsService {
    ActionResult BeginTransaction(String userId, long amount);

    ActionResult AfterTransaction(String userId, long time, String bank);

    ActionResult getAllTransaction(String employerId, Pageable pageable);

    ActionResult getCountTransactions();

}
