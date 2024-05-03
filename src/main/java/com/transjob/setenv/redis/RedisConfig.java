package com.transjob.setenv.redis;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
@Configuration
@Slf4j
public class RedisConfig {
    @Value("${spring.data.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Value("${spring.data.redis.cluster.use}")
    private boolean isCluster;

    public boolean isCluster() {
        return this.isCluster;
    }

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    public String getPassword() {
        return password;
    }
    @Value("${spring.data.redis.password}")
    private String password;
    public int getDataTimeOut() {
        return dataTimeOut;
    }

    @Value("${spring.data.redis.dataTimeOut}")
    private int dataTimeOut;

    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactory(){
        if(isCluster) {
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();

            if (clusterNodes.size() != 0) {
                clusterNodes.forEach(node -> {
                    String ip = node.substring(0, node.indexOf(":"));
                    int port = Integer.parseInt(node.substring(node.indexOf(":") + 1));
                    log.info("{}:{}", ip, port);
                    clusterConfiguration.clusterNode(ip, port);
                });
            }
            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder().build();
            ClusterClientOptions clientOptions = ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build();
            LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder().clientOptions(clientOptions).build();

            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
            connectionFactory.afterPropertiesSet();
            return connectionFactory;

        }
        else {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(host);
            redisStandaloneConfiguration.setPort(port);
            if (password != null) {
                redisStandaloneConfiguration.setPassword(password);
            }
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
            return lettuceConnectionFactory;

        }
    }

    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @Primary
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }
}
