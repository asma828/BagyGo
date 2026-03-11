package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.Rating;
import com.bagygo.bagygo_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByToUserOrderByCreatedAtDesc(User toUser);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.toUser = :user")
    Double findAverageRatingForUser(User user);
}