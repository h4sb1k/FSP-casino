package com.stoloto.vip.core.repository;

import com.stoloto.vip.core.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByRoundId(Long roundId);
    List<Bet> findByUserId(Long userId);
    List<Bet> findByRoundIdAndUserId(Long roundId, Long userId);
}
