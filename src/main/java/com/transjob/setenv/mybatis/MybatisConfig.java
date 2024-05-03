package com.transjob.setenv.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@PropertySource("classpath:application.yml")
@Configuration
@MapperScan(value = "com.transjob.setenv.mybatis",  sqlSessionTemplateRef = "ScheduleSessionTemplate")
public class MybatisConfig {
    // mapper scan을 위해서는 mapper인터페이스가 필요

    // sessionTemplate와 sqlsessionFactory를 만들어 주는 설정등이 들어있음

    // 기본이 되는 bean은 name없이 @Primary만 설정해주면 자동 설정이 된다
    @Bean(name = "SchedulerdataSource")
    @ConfigurationProperties(prefix = "secondary.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    // 두번 째 이후로는 Qualifier을 통해서 bean을 특정해 주어야 함
    @Bean(name = "SchedulerSqlSessionFactory")
    public SqlSessionFactory SqlSessionFactory2(@Qualifier("SchedulerdataSource") DataSource DataSource,
                                                ApplicationContext applicationContext) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(DataSource);
        // 밑의 항목은 인터페이스 자체 어노테이션 매퍼가 아닌
        // xml형태의 mapper을 사용할 경우 필요한 설정, xml의 위치를 지정해주면 됨
        //sqlSessionFactoryBean.setMapperLocations(applicationContext.getResource(mapperLocations));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "ScheduleSessionTemplate")
    public SqlSessionTemplate sessionTemplate2(@Qualifier("SchedulerSqlSessionFactory")
                                               SqlSessionFactory firstSqlSessionFactory) {
        return new SqlSessionTemplate(firstSqlSessionFactory);
    }

}
