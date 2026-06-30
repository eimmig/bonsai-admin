package com.utfpr.edu.br.pw45s.shared.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("local")
public class LocalDevConfig {

    @Bean
    public ApplicationRunner cleanAndMigrate(DataSource dataSource) {
        return args -> {
            var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .cleanDisabled(false)
                .load();
            flyway.clean();
            flyway.migrate();
        };
    }
}
