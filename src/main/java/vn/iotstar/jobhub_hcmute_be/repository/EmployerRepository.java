package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Student;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface EmployerRepository extends JpaRepository<Employer, String> {

    Optional<Employer> findByPhoneAndIsActiveIsTrue(String phone);

    Optional<Employer> findByUserIdAndIsActiveIsTrue(String id);

   Page<Employer> findByTransactionMoneyGreaterThanEqualOrderByTransactionMoneyDesc(Long transactionMoney, Pageable pageable);

   Page<Employer> findAllByIsActiveIsTrueOrderByTransactionMoneyDesc(Pageable pageable);

}