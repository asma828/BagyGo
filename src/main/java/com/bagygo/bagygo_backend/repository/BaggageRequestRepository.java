package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BaggageRequestRepository extends JpaRepository<BaggageRequest, Long> {

    List<BaggageRequest> findBySenderOrderByCreatedAtDesc(User user);

    List<BaggageRequest> findByTrip(com.bagygo.bagygo_backend.entity.Trip trip);

    List<BaggageRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    long countByStatus(RequestStatus status);

    List<BaggageRequest> findByStatusInOrderByCreatedAtDesc(List<RequestStatus> statuses);

    @Query("SELECT br FROM BaggageRequest br WHERE br.sender = :user AND br.status IN :statuses ORDER BY br.createdAt DESC")
    List<BaggageRequest> findByUserAndStatusIn(@Param("user") User user,
            @Param("statuses") List<RequestStatus> statuses);

    long countBySenderAndStatus(User user, RequestStatus status);

    @Query("SELECT COUNT(br) FROM BaggageRequest br WHERE br.sender = :user AND br.status NOT IN (:s1, :s2)")
    long countByUserAndStatusNotIn(@Param("user") User user, @Param("s1") RequestStatus s1,
            @Param("s2") RequestStatus s2);

    long countBySenderAndStatusAndIsPaidFalse(User user, RequestStatus status);

    long countBySenderAndStatusIn(User user, List<RequestStatus> statuses);

    // Filter requests for transporter's trips
    long countByTrip_Transporter(User transporter);

    List<BaggageRequest> findByTrip_TransporterOrderByCreatedAtDesc(User transporter);

    @Query("SELECT COUNT(br) FROM BaggageRequest br WHERE br.trip.transporter = :transporter AND br.status IN :statuses")
    long countByTransporterAndStatusIn(@Param("transporter") User transporter,
            @Param("statuses") List<RequestStatus> statuses);
}