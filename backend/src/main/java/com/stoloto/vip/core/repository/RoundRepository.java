package com.stoloto.vip.core.repository;

import com.stoloto.vip.core.entity.Round;
import com.stoloto.vip.core.enums.RoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByRoomId(Long roomId);
    List<Round> findByRoomIdAndStatus(Long roomId, RoundStatus status);
    Optional<Round> findFirstByRoomIdOrderByRoundNumberDesc(Long roomId);
}
