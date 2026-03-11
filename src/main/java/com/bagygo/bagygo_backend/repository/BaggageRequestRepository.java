package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaggageRequestRepository extends JpaRepository<BaggageRequest, Long> {

    List<BaggageRequest> findBySenderOrderByCreatedAtDesc(User user);

    List<BaggageRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    List<BaggageRequest> findByStatusInOrderByCreatedAtDesc(List<RequestStatus> statuses);

    @Query("SELECT br FROM BaggageRequest br WHERE br.sender = :user AND br.status IN :statuses ORDER BY br.createdAt DESC")
    List<BaggageRequest> findByUserAndStatusIn(User user, List<RequestStatus> statuses);

    long countBySenderAndStatus(User user, RequestStatus status);

    @Query("SELECT COUNT(br) FROM BaggageRequest br WHERE br.sender = :user AND br.status NOT IN (:s1, :s2)")
    long countByUserAndStatusNotIn(User user, RequestStatus s1, RequestStatus s2);
}