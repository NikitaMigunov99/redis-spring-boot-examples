package com.redis.examples.service;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClusterConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;

public class MyRedis {

//    private RedisTemplate<String, String> redisTemplate;
//
//    public List<Object> getMemoryStats() {
//        SessionCallback<List<Object>> callback = connection -> {
//            byte[][] cmd = { "MEMORY".getBytes(), "STATS".getBytes() };
//            return connection.execute(cmd); // Посылаем команду
//        };
//
//        return redisTemplate.execute(callback); // Исполняем нашу команду
//    }
//
//    public Object executeCustomCommand(String command, String... args) {
//        return redisTemplate.execute((RedisCallback<Object>) connection -> {
//            // Build the command
//            byte[][] commandArgs = new byte[args.length + 1][];
//            commandArgs[0] = command.getBytes();
//            for (int i = 0; i < args.length; i++) {
//                commandArgs[i + 1] = args[i].getBytes();
//            }
//
//            // Execute it using low-level connection
//            Object result = connection.execute(commandArgs);
//            return result;
//        });
//    }

    private LettuceConnectionFactory connectionFactory;

//    public String runFooCommand() {
//        var connection = (LettuceClusterConnection) connectionFactory.getClusterConnection();
//        var nativeConn = connection.getNativeConnection();
//
//        Object raw = nativeConn.dispatch(
//                MyRedisCommand.FOO,
//                new io.lettuce.core.output.StatusOutput<>(io.lettuce.core.codec.ByteArrayCodec.INSTANCE)
//        );
//
//        return raw != null ? raw.toString() : null;
//    }

//    public void setFieldExpire(RedisTemplate<String, String> redisTemplate,
//                               String key, String field, long ttlSeconds) {
//
//        LettuceConnectionFactory factory =
//                (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
//
//        assert factory != null;
//        factory.getConnection().close(); // make sure it's initialized
//
//        factory.getClusterConnection().execute(
//                io.lettuce.core.protocol.CommandType.valueOf("COMMAND"), // dummy to get codec, not used
//                new io.lettuce.core.output.StatusOutput<>(factory.getClient().getOptions().getCodec()),
//                new io.lettuce.core.protocol.CommandArgs<>(factory.getClient().getOptions().getCodec())
//        );
//
//        // simpler approach: execute a raw command string via dispatch
//        var connection = factory.getClusterConnection();
//        try {
//            //RedisCommands<String, String> sync = connection.sync();
//
//            // Lettuce doesn't yet know HEXPIRE, so we send it manually:
//            String result = connection.execute(
//                    new io.lettuce.core.protocol.ProtocolKeyword() {
//                        @Override
//                        public byte[] getBytes() {
//                            return "HEXPIRE".getBytes();
//                        }
//
//                        @Override
//                        public String name() {
//                            return "HEXPIRE";
//                        }
//                    },
//                    new io.lettuce.core.output.StatusOutput<>(connection.getCodec()),
//                    new io.lettuce.core.protocol.CommandArgs<>(connection.getCodec())
//                            .addKey(key)
//                            .add(field)
//                            .add(ttlSeconds)
//            );
//
//            System.out.println("HEXPIRE result: " + result);
//
//        } finally {
//            connection.close();
//        }
//    }

//    public static void main(String[] args) {
//        RedisClient client = RedisClient.create("redis://localhost:6379");
//        StatefulRedisConnection<String, String> connection = client.connect();
//        RedisCommands<String, String> commands = connection.sync();
//
//
//        RedisCodec<String, String> codec = StringCodec.UTF8;
//        // Run the custom command
//        Object result = commands.dispatch(
//                MyRedisCommand.CUSTOM_COMMAND,
//                new io.lettuce.core.output.StatusOutput<>(codec),
//                new io.lettuce.core.protocol.CommandArgs<>(codec)
//                        .add(Co)
//                        .add("arg1")
//                        .add("arg2")
//        );
//
//        System.out.println("Custom command result: " + result);
//
//        connection.close();
//        client.shutdown();
//    }

}
