package com.transjob.setenv.redis;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @Slf4j
    @Configuration
    public class Redis2Config {

        @Value("${spring.data.redis2.host}")
        private String host;

        @Value("${spring.data.redis2.port}")
        private int port;

        @Value("${spring.data.redis2.password}")
        private String password;

        @Value("${spring.data.redis2.cluster.use}")
        private boolean isCluster;
        public boolean isCluster() {
            return this.isCluster;
        }
        @Value("${spring.data.redis2.cluster.nodes}")
        private List<String> clusterNodes;

        @Bean(name = "paymentRedisFactory")
        public LettuceConnectionFactory paymentLettuceConnectionFactory() {
            if (isCluster) {
                RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();

                if (clusterNodes != null && !clusterNodes.isEmpty() && clusterNodes.size() > 1) {
                    clusterNodes.forEach(node -> {
                        String ip = node.split(":")[0];
                        int port = Integer.parseInt(node.split(":")[1]);
                        clusterConfiguration.clusterNode(ip, port);
                    });
                }
                ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder().build();
                ClusterClientOptions clientOptions = ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build();
                LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder().clientOptions(clientOptions).build();

                LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
                lettuceConnectionFactory.afterPropertiesSet();
                return lettuceConnectionFactory;
            } else {
                RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
                redisStandaloneConfiguration.setHostName(host);
                redisStandaloneConfiguration.setPort(port);
                if (password != null) {
                    redisStandaloneConfiguration.setPassword(password);
                }
                return new LettuceConnectionFactory(redisStandaloneConfiguration);
            }
        }

        // 첫번째 @Primary가 있는 값들은 자동으로 빈주입이 되지만 Second이상의 레디스를 사용할 경우에는 @Qualifier을 통한 특정 빈 주입이 필수
        // 또한 여러개의 redis사용시 @Primary가 없으면 역으로 설정값을 못찾아 갈 수 있으니 확인할 것
        // ?,? 는 상관없는데 두번째 이상의 빈은 기본값 설정이 제대로 설정되어 있지 않아서
        // DefaultSerializer 설정을 정상적으로 안할경우 실제 넣은 값 앞에 이상한 값이 들어가는 경우가 발생한다
        @Bean(name = "paymentRedisTemplate")
        public RedisTemplate<?, ?> redisTemplate(@Qualifier("paymentRedisFactory") LettuceConnectionFactory redisConnectionFactory) {
            RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            redisTemplate.setDefaultSerializer(new StringRedisSerializer());
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }

        @Bean(name = "paymentRedisListener")
        public RedisMessageListenerContainer redisMessageListenerContainer(@Qualifier("paymentRedisFactory") RedisConnectionFactory redisConnectionFactory) {
            RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
            redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
            return redisMessageListenerContainer;
        }

    }


}
