package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import java.util.Optional;

@Hidden
@Repository
public interface EmployerRepository extends JpaRepository<Employer, String> {

    Optional<Employer> findByPhoneAndIsActiveIsTrue(String phone);

    Optional<Employer> findByUserIdAndIsActiveIsTrue(String id);

   Page<Employer> findByTransactionMoneyGreaterThanEqualOrderByTransactionMoneyDesc(Long transactionMoney, Pageable pageable);

   Page<Employer> findAllByIsActiveIsTrueOrderByTransactionMoneyDesc(Pageable pageable);

    @Query("SELECT e FROM Employer e WHERE " +
            "(e.companyName LIKE %:companyName%) " +
            "AND (COALESCE(:teamSize, '') = '' OR e.teamSize = :teamSize) " +
            "AND (e.isActive = true) " +
            "ORDER BY e.transactionMoney DESC")
    Page<Employer> findEmployers(Pageable pageableString, String companyName, String teamSize);

}