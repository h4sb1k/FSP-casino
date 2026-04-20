package com.stoloto.vip.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Архив раскрытых RNG семян для Provably Fair верификации
 */
@Entity
@Table(name = "rng_seeds_archive", indexes = {
    @Index(name = "idx_rng_seeds_round", columnList = "round_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RngSeedArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_rng_seeds_round"))
    private Round round;

    /**
     * Серверный seed (раскрытый после раунда)
     */
    @Column(name = "server_seed", nullable = false, length = 64)
    private String serverSeed;

    /**
     * Хэш серверного seed (был опубликован до начала раунда)
     */
    @Column(name = "server_seed_hash", nullable = false, length = 64)
    private String serverSeedHash;

    /**
     * Клиентский seed
     */
    @Column(name = "client_seed", length = 64)
    private String clientSeed;

    /**
     * Nonce счетчик
     */
    @Column(nullable = false)
    private Long nonce;

    /**
     * Хэш результата для быстрой верификации
     */
    @Column(name = "result_hash", length = 64)
    private String resultHash;

    @CreationTimestamp
    @Column(name = "revealed_at", nullable = false, updatable = false)
    private Instant revealedAt;

    /**
     * Проверка соответствия хэша seed
     */
    public boolean verifySeedHash(String computedHash) {
        return serverSeedHash != null && serverSeedHash.equals(computedHash);
    }
}
