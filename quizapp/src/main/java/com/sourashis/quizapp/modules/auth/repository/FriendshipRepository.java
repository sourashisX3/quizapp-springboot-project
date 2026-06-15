package com.sourashis.quizapp.modules.auth.repository;

import com.sourashis.quizapp.modules.auth.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByRequesterIdOrAddresseeId(Long userId1, Long userId2);

    Optional<Friendship> findByRequesterIdAndAddresseeId(Long requesterId, Long addresseeId);

    List<Friendship> findByAddresseeIdAndStatus(Long userId, String status);

    List<Friendship> findByRequesterIdAndStatus(Long userId, String status);

    long countByRequesterIdAndStatusOrAddresseeIdAndStatus(Long requesterId, String status1, Long addresseeId, String status2);
}
