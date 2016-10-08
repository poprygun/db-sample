package com.synchrony.poc.config;

import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.PooledServiceConnectorConfig;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DbConfig {
    @Configuration
    @Profile("cloud")
    @PropertySource("classpath:cloud-db.properties")
    static class CloudConfiguration {

        @Bean
        public CloudFactory cloudFactory() {
            return new CloudFactory();
        }

        @Bean
        @Qualifier("myDataSourceOne")
        public DataSource dataSourceOne(CloudFactory cloudFactory
                , @Value("${ds1.min.pool.size}") int minPoolSize
                , @Value("${ds1.max.pool.size}") int maxPoolSize
                , @Value("${ds1.max.wait.time}") int maxWaitTime) {
            Cloud cloud = cloudFactory.getCloud();
            return cloud.getServiceConnector("one-db",
                    DataSource.class, getServiceConnectorConfig(minPoolSize, maxPoolSize, maxWaitTime));
        }

        @Bean
        @Qualifier("myDataSourceTwo")
        public DataSource dataSourceTwo(CloudFactory cloudFactory
                , @Value("${ds2.min.pool.size}") int minPoolSize
                , @Value("${ds2.max.pool.size}") int maxPoolSize
                , @Value("${ds2.max.wait.time}") int maxWaitTime) {
            Cloud cloud = cloudFactory.getCloud();
            return cloud.getServiceConnector("two-db",
                    DataSource.class, getServiceConnectorConfig(minPoolSize, maxPoolSize, maxWaitTime));
        }

        private ServiceConnectorConfig getServiceConnectorConfig(int minPoolSize
                , int maxPoolSize
                , int maxWaitTime) {
            System.out.println("Configuring cloud datasource with min " + minPoolSize + " max " + maxPoolSize + " max wait time" + maxWaitTime);
            PooledServiceConnectorConfig.PoolConfig poolConfig = new PooledServiceConnectorConfig.PoolConfig(minPoolSize, maxPoolSize, maxWaitTime);
            DataSourceConfig.ConnectionConfig connConfig = new DataSourceConfig.ConnectionConfig("useUnicode=yes;characterEncoding=UTF-8");
            return new DataSourceConfig(poolConfig, connConfig);
        }

    }

    @Configuration
    @Profile("default")
    @PropertySource("classpath:local-db.properties")
    static class LocalConfiguration {

        @Bean
        @Qualifier("myDataSourceTwo")
        public DataSource dataSourceTwo(@Value("${ds1.user}") String user
                , @Value("${ds1.pwd}") String pwd
                , @Value("${ds1.url}") String url) {
            return dsFor(user, pwd, url);
        }

        @Bean
        @Qualifier("myDataSourceOne")
        public DataSource dataSourceOne(@Value("${ds2.user}") String user
                , @Value("${ds2.pwd}") String pwd
                , @Value("${ds2.url}") String url) {
            return dsFor(user, pwd, url);
        }

        private DataSource dsFor(String user, String pwd, String url) {
            try {
                System.out.println("Loading data source for " + url + " pwd " + pwd + " url " + url);
                OracleDataSource dataSource = new OracleDataSource();
                dataSource.setUser(user);
                dataSource.setPassword(pwd);
                dataSource.setURL(url);
                dataSource.setImplicitCachingEnabled(true);
                dataSource.setFastConnectionFailoverEnabled(true);
                return dataSource;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new BeanInitializationException("Error loading datasource for " + url, e);
            }
        }

    }
}
