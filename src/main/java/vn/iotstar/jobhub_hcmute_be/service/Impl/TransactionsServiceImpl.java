package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.Rating;
import vn.iotstar.jobhub_hcmute_be.constant.StatusTransaction;
import vn.iotstar.jobhub_hcmute_be.constant.Transaction;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Transactions;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.TransactionsRepository;
import vn.iotstar.jobhub_hcmute_be.service.TransactionsService;

import java.util.Optional;

@Service
public class TransactionsServiceImpl implements TransactionsService {
    @Autowired
    TransactionsRepository transactionsRepository;
    @Autowired
    EmployerRepository employerRepository;

    private boolean checkTransaction(long time) {
        long currentTime = System.currentTimeMillis();
        // Xác định giới hạn thời gian (15 phút)
        long timeLimit = 15 * 60 * 1000; // 15 phút tính bằng mili giây
        boolean isWithinTimeLimit = (currentTime - time) <= timeLimit;
        return isWithinTimeLimit;

    }

    @Override
    public ActionResult BeginTransaction(String userId, long amount) {
        System.out.println("38" + amount);
        ActionResult result = new ActionResult();
        try {
            if (Transaction.getCode(amount) == null) {
                System.out.println("39" + Transaction.getCode(amount));
                result.setErrorCode(ErrorCodeEnum.DETECTION_OF_VIOLATIONS);
                return result;
            }
            Optional<Employer> employer = employerRepository.findById(userId);
            if (employer.isEmpty()) {
                result.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return result;
            }
            if (employer.get().getIsTransaction()) {
                Optional<Transactions> transactionOld = transactionsRepository.findByEmployerAndStatus(employer.get(), StatusTransaction.PENDING.toString());
                if (transactionOld.isEmpty()) {
                    employer.get().setIsTransaction(false);
                    employerRepository.save(employer.get());
                } else {
                    long time = transactionOld.get().getCreateAt();
                    boolean isWithinTimeLimit = checkTransaction(time);
                    if (isWithinTimeLimit) {
                        result.setErrorCode(ErrorCodeEnum.USER_IS_TRANSACTION);
                        return result;
                    } else {
                        transactionOld.get().setStatus(StatusTransaction.CANCEL.toString());
                        transactionsRepository.save(transactionOld.get());
                    }
                }
            }
            Transactions transactions = new Transactions();
            transactions.setEmployer(employer.get());
            transactions.setAmount(amount);
            transactions.setCode(Transaction.getCode(amount));
            transactions.setName(Transaction.getName(Transaction.getCode(amount)));
            transactions.setStatus(StatusTransaction.PENDING.toString());
            transactionsRepository.save(transactions);

            employer.get().setIsTransaction(true);
            employerRepository.save(employer.get());

            result.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.out.println("74" + e.getMessage());
            result.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @Override
    public ActionResult AfterTransaction(String userId, long time, String bank) {
        ActionResult result = new ActionResult();
        try {
            Optional<Employer> employer = employerRepository.findById(userId);
            if (employer.isEmpty()) {
                result.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return result;
            }
            if (!employer.get().getIsTransaction()) {
                result.setErrorCode(ErrorCodeEnum.TRANSACTION_SAVED);
                return result;
            }
            Optional<Transactions> transactionOld = transactionsRepository.findByEmployerAndStatus(employer.get(), StatusTransaction.PENDING.toString());
            if (transactionOld.isEmpty()) {
                result.setErrorCode(ErrorCodeEnum.TRANSACTION_SAVED);
                return result;
            }
            transactionOld.get().setStatus(StatusTransaction.SUCCESS.toString());
            transactionOld.get().setTime(time);
            transactionOld.get().setBank(bank);

            employer.get().setIsTransaction(false);
            employer.get().setTransactionMoney(employer.get().getTransactionMoney() + transactionOld.get().getAmount());
            String codeRanting = Rating.getCode(employer.get().getTransactionMoney());
            employer.get().setSponsor(codeRanting);

            employerRepository.save(employer.get());
            transactionsRepository.save(transactionOld.get());
            result.setErrorCode(ErrorCodeEnum.OK);
            result.setData(transactionOld.get());

        } catch (Exception e) {
            result.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
