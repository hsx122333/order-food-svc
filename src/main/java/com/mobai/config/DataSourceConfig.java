package com.mobai.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
public class DataSourceConfig {

    @Bean(name="dataSource")
    public DataSource datasource(Environment env) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("hikari.datasource.url"));
        ds.setUsername(env.getProperty("hikari.datasource.username"));
        ds.setPassword(env.getProperty("hikari.datasource.password"));
        ds.setDriverClassName(env.getProperty("hikariCp.datasource.dataSourceClassName"));
        ds.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("hikariCp.datasource.maximumPoolSize"))));
        ds.setConnectionTimeout(Integer.parseInt(Objects.requireNonNull(env.getProperty("hikariCp.datasource.connectionTimeout"))));
        return ds;
    }
}