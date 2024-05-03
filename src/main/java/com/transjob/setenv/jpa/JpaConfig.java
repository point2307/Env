package com.transjob.setenv.jpa;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
//        basePackages = {"com.arreo.payments.customer.user.repository"}  실제 레포지터리들이 위치한 폴더를 지정해 주어야 함
)
public class JpaConfig {
    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
    // 엔티티 팩토리와 트렌젝션 메니저를 생성해야 하고, 데이터 소스에 hikaripool을 생성하여서 사용함

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
        return builder.dataSource(dataSource).packages("com.arreo.payments.customer.user.model.entity").persistenceUnit("primary").properties(properties).build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    PlatformTransactionManager transactionManager(@Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
