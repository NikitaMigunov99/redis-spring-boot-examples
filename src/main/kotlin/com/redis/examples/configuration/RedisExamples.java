package com.redis.examples.configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisExamples {

    private StringRedisTemplate stringRedisTemplate;

//    public void incrementOrReset() {
//        stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
//            byte[] keyBytes = "counter".getBytes(); // Ключ нашего счётчика
//
//            // Наблюдаем за изменением ключа
//            connection.watch(keyBytes);
//
//            // Читаем текущее значение
//            byte[] valueBytes = connection.get(keyBytes);
//            long currentValue = valueBytes != null ? Long.parseLong(new String(valueBytes)) : 0L;
//
//            if (currentValue >= 100) {
//                // Устанавливаем новый счётчик в 0
//                connection.multi();
//                connection.set(keyBytes, "0".getBytes());
//            } else {
//                // Просто увеличиваем счётчик
//                connection.multi();
//                connection.incr(keyBytes);
//            }
//
//            // Выполнение транзакции
//            Object result = connection.exec();
//            if (result instanceof List<?> list && !list.isEmpty()) {
//                // Возвращаем последнее выполненное значение (результат INCR)
//                return ((List<?>) result).stream()
//                        .filter(Long.class::isInstance)
//                        .mapToLong(Long.class::cast)
//                        .findFirst().orElse(currentValue + 1);
//            } else {
//                throw new RuntimeException("Ошибка выполнения транзакции");
//            }
//        });
//    }
//
//    public long incrementOrResetWithLock(Duration lockTimeout) {
//        // Уникальная метка для блокировки
//        String lockKey = "lock_counter";
//        String uniqueLockIdentifier = UUID.randomUUID().toString();
//
//        try {
//            // Установка блокировки
//            Boolean acquiredLock = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, uniqueLockIdentifier,
//                    lockTimeout.toMillis(), TimeUnit.MILLISECONDS);
//
//            if (acquiredLock) {
//                // Забрали блокировку, теперь работаем с счётчиком
//                byte[] keyBytes = "counter".getBytes();
//                byte[] valueBytes = stringRedisTemplate.getConnectionFactory().getConnection().get(keyBytes);
//                long currentValue = valueBytes != null ? Long.parseLong(new String(valueBytes)) : 0L;
//
//                if (currentValue >= 100) {
//                    stringRedisTemplate.opsForValue().set("counter", "0"); // Сброс
//                } else {
//                    stringRedisTemplate.opsForValue().increment("counter", 1); // Инкремент
//                }
//            } else {
//                // Если блокировка занята другим процессом, ждём какое-то время и пробуем ещё раз
//                throw new IllegalStateException("Unable to acquire lock for counter update");
//            }
//        } finally {
//            // Обязательно освобождаем блокировку
//            unlock(lockKey, uniqueLockIdentifier);
//        }
//
//        return stringRedisTemplate.opsForValue().get("counter");
//    }
//
//    private void unlock(String lockKey, String identifier) {
//        // Удаляем блокировку, если именно мы её захватили
//        String existingLockValue = stringRedisTemplate.opsForValue().get(lockKey);
//        if (identifier.equals(existingLockValue)) {
//            stringRedisTemplate.delete(lockKey);
//        }
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        // Создаем клиента Redis
//        RedisClient redisClient = RedisClient.create("redis://localhost");
//
//        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
//            RedisCommands<String, String> syncCommands = connection.sync();
//
//            while (true) { // бесконечный цикл для демонстрации работы
//                boolean success = false;
//
//                do {
//                    // Наблюдение за ключом 'counter'
//                    syncCommands.watch("counter");
//
//                    // Получаем текущее значение
//                    Long currentValue = syncCommands.get("counter") != null ? Long.parseLong(syncCommands.get("counter")) : 0L;
//
//                    if (currentValue >= 100) {
//                        // Начинаем транзакцию и устанавливаем новое значение в 0
//                        syncCommands.multi();
//                        syncCommands.set("counter", "0");
//                    } else {
//                        // Увеличиваем значение на 1
//                        syncCommands.multi();
//                        syncCommands.incr("counter");
//                    }
//
//                    // Запускаем выполнение транзакции
//                    TransactionResult execResult = syncCommands.exec();
//
//                    if (!execResult.isEmpty() && !execResult.contains(null)) {
//                        System.out.println("Транзакция выполнена успешно.");
//                        success = true;
//                    } else {
//                        System.err.println("Транзакция была прервана, повторение...");
//                    }
//                } while (!success);
//
//                Thread.sleep(100); // Пауза между итерациями
//            }
//        } finally {
//            redisClient.shutdown();
//        }
//    }
}
