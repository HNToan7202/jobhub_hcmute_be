package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.Rating;
import vn.iotstar.jobhub_hcmute_be.constant.StatusTransaction;
import vn.iotstar.jobhub_hcmute_be.constant.Transaction;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Transactions;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.TransactionBase;
import vn.iotstar.jobhub_hcmute_be.model.TransactionModel;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.TransactionsRepository;
import vn.iotstar.jobhub_hcmute_be.service.TransactionsService;

import java.security.Timestamp;
import java.time.DateTimeException;
import java.time.Month;
import java.util.*;

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
                    long time = transactionOld.get().getCreateAt().getTime();
                    boolean isWithinTimeLimit = checkTransaction(time);
                    if (isWithinTimeLimit) {
                        // Giao dịch chưa hết hạn đang chờ thanh toán thì không cho tạo giao dịch mới
                        result.setErrorCode(ErrorCodeEnum.USER_IS_TRANSACTION);
                        return result;
                    } else {
                        // Hủy giao dịch cũ chưa thanh toán
                        transactionOld.get().setStatus(StatusTransaction.CANCEL.toString());
                        transactionsRepository.save(transactionOld.get());
                    }
                }
            }
            Transactions transactions = new Transactions();
            transactions.setId(UUID.randomUUID().toString().split("-")[0]);
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
                // Giao dịch đã được thanh toán hoặc đã hết hạn
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

    @Override
    public ActionResult getAllTransaction(String employerId, Pageable pageable) {
        ActionResult actionResult = new ActionResult();
        try {
            Page<Transactions> transactions;
            Map<String, Object> map = new HashMap<String, Object>();
            if (employerId == null) {
                transactions = transactionsRepository.findAll(pageable);
            } else {
                Optional<Employer> optionalEmployer = employerRepository.findById(employerId);
                if (optionalEmployer.isEmpty()) {
                    actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                    return actionResult;
                }
                transactions = transactionsRepository.findAllByEmployer_UserIdAndStatus(employerId, StatusTransaction.SUCCESS.toString(), pageable);
                map.put("totalMoney", optionalEmployer.get().getTransactionMoney());
                map.put("countTransaction", transactions.getTotalElements());
            }
            List<TransactionModel> transactionModels = transactions.stream().map(TransactionModel::transform).toList();
            map.put("transactions", transactionModels);
            map.put("pageNumber", transactions.getPageable().getPageNumber());
            map.put("pageSize", transactions.getSize());
            map.put("totalPages", transactions.getTotalPages());
            map.put("totalElements", transactions.getTotalElements());
            actionResult.setData(map);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }

    @Override
    public ActionResult getCountTransactions() {

        ActionResult actionResult = new ActionResult();
        try {

            long totalCount = transactionsRepository.countByStatus(StatusTransaction.SUCCESS.toString());

            long totalMoney = transactionsRepository.calculateTotalAmountByStatus(StatusTransaction.SUCCESS.toString());

            TransactionBase basicPackage = new TransactionBase(
                    Transaction.BASIC_PACKAGE.getCode(),
                    transactionsRepository.countByCodeAndStatus(Transaction.BASIC_PACKAGE.getCode(), StatusTransaction.SUCCESS.toString()),
                    transactionsRepository.calculateTotalAmountByCodeAndStatus(Transaction.BASIC_PACKAGE.getCode(), StatusTransaction.SUCCESS.toString())
            );

            TransactionBase standardPackage = new TransactionBase(
                    Transaction.STANDARD_PACKAGE.getCode(),
                    transactionsRepository.countByCodeAndStatus(Transaction.STANDARD_PACKAGE.getCode(), StatusTransaction.SUCCESS.toString()),
                    transactionsRepository.calculateTotalAmountByCodeAndStatus(Transaction.STANDARD_PACKAGE.getCode(), StatusTransaction.SUCCESS.toString())
            );

            TransactionBase premiumPackage = new TransactionBase(
                    Transaction.PREMIUM_PACKAGE.getCode(),
                    transactionsRepository.countByCodeAndStatus(Transaction.PREMIUM_PACKAGE.getCode(), StatusTransaction.SUCCESS.toString()),
                    transactionsRepository.calculateTotalAmountByCodeAndStatus(Transaction.PREMIUM_PACKAGE.getCode(), StatusTransaction.SUCCESS.toString())
            );

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("totalCount", totalCount);
            map.put("totalMoney", totalMoney);
            map.put("basicPackage", basicPackage);
            map.put("standardPackage", standardPackage);
            map.put("premiumPackage", premiumPackage);

            actionResult.setData(map);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {

            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }


    private String getMonthName(int month) {
        try {
            Month monthEnum = Month.of(month);
            return monthEnum.toString();
        } catch (DateTimeException e) {
            return "Invalid Month";
        }
    }
}
