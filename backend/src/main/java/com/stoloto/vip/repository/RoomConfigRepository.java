package com.stoloto.vip.repository;

import com.stoloto.vip.domain.entity.RoomConfig;
import com.stoloto.vip.domain.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для конфигураций комнат
 */
@Repository
public interface RoomConfigRepository extends JpaRepository<RoomConfig, Long> {

    Optional<RoomConfig> findByType(RoomType type);

    @Query("SELECT rc FROM RoomConfig rc WHERE rc.isActive = true ORDER BY rc.minBet ASC")
    List<RoomConfig> findAllActive();

    @Query("SELECT rc FROM RoomConfig rc WHERE rc.type = :type AND rc.isActive = true")
    Optional<RoomConfig> findActiveByType(@Param("type") RoomType type);
}
