package com.stoloto.vip.rng;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * Сервис для генерации аудируемых случайных чисел (Provably Fair)
 * Использует комбинацию Server Seed + Client Seed + Nonce
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RngService {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Генерация серверного seed (секретного до конца раунда)
     */
    public String generateServerSeed() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return bytesToHex(randomBytes);
    }

    /**
     * Генерация клиентского seed (может быть предоставлен клиентом или сгенерирован)
     */
    public String generateClientSeed() {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return bytesToHex(randomBytes);
    }

    /**
     * Комбинирование seed'ов для получения финального хеша
     */
    public String combineSeeds(String serverSeed, String clientSeed, Long roundId) {
        String combined = serverSeed + ":" + clientSeed + ":" + roundId;
        return sha256(combined);
    }

    /**
     * Получение случайного числа от 0.0 до 1.0 на основе seed
     */
    public double getRandomValue(String seed) {
        try {
            byte[] hashBytes = MessageDigest.getInstance("SHA-256").digest(seed.getBytes(StandardCharsets.UTF_8));
            
            // Берем первые 8 байт и преобразуем в long
            long hashLong = 0;
            for (int i = 0; i < 8; i++) {
                hashLong = (hashLong << 8) | (hashBytes[i] & 0xFF);
            }
            
            // Преобразуем в число от 0 до 1
            // Используем только положительные значения
            long positiveHash = hashLong & 0x7FFFFFFFFFFFFFFFL;
            return (double) positiveHash / (double) 0x7FFFFFFFFFFFFFFFL;
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("RNG failure", e);
        }
    }

    /**
     * Генерация хеша для аудита (публичное представление server seed до раскрытия)
     */
    public String generateHashedServerSeed(String serverSeed) {
        return sha256(serverSeed);
    }

    /**
     * Верификация честности раунда
     * Может быть вызвана пользователем после раскрытия server seed
     */
    public boolean verifyRound(String serverSeed, String clientSeed, Long roundId, 
                               String expectedHash, double expectedResult) {
        // Проверка хеша server seed
        String actualHash = generateHashedServerSeed(serverSeed);
        if (!actualHash.equals(expectedHash)) {
            log.warn("Server seed hash mismatch");
            return false;
        }
        
        // Проверка результата
        String combined = combineSeeds(serverSeed, clientSeed, roundId);
        double actualResult = getRandomValue(combined);
        
        // Сравниваем с небольшой погрешностью
        return Math.abs(actualResult - expectedResult) < 0.0000001;
    }

    /**
     * Вспомогательный метод для SHA-256 хеширования
     */
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("Hashing failure", e);
        }
    }

    /**
     * Преобразование байтов в hex строку
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Генерация случайного целого числа в диапазоне [min, max]
     */
    public int getRandomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than max");
        }
        return secureRandom.nextInt((max - min) + 1) + min;
    }

    /**
     * Генерация случайного элемента из списка
     */
    public <T> T getRandomElement(T[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        return array[secureRandom.nextInt(array.length)];
    }
}
