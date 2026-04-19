package com.stoloto.vip.core.repository;

import com.stoloto.vip.core.entity.BotConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotConfigRepository extends JpaRepository<BotConfig, Long> {
    List<BotConfig> findByActiveTrue();
}
