package com.funfo.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class LettuceConfig {
    private static final Logger log = LoggerFactory.getLogger(LettuceConfig.class);

    @Value("${spring.redis.cluster.nodes:}")
    private String redisClusterNodes;

    @Value("${spring.redis.cluster.max-redirects:3}")
    private Integer maxRedirects;

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();

        log.info("===== 从 Properties 读取的 Redis 节点：{} =====", redisClusterNodes);
        if (redisClusterNodes == null || redisClusterNodes.trim().isEmpty()) {
            throw new RuntimeException("Redis 集群节点配置为空！");
        }

        // 仅保留节点解析逻辑，无任何 SSL 相关代码
        List<RedisNode> redisNodes = Arrays.stream(redisClusterNodes.split(","))
                .map(String::trim)
                .filter(node -> !node.isEmpty())
                .map(node -> {
                    String[] parts = node.split(":");
                    return new RedisNode(parts[0], Integer.parseInt(parts[1]));
                })
                .collect(Collectors.toList());

        clusterConfig.setClusterNodes(redisNodes);
        clusterConfig.setMaxRedirects(maxRedirects);
        return clusterConfig;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisClusterConfiguration);
        factory.setValidateConnection(false); // 保持关闭（避免连接失败阻塞服务）
        // 移除所有 SSL 相关配置（比如 sslProvider/sslParameters 等）
        return factory;
    }
}