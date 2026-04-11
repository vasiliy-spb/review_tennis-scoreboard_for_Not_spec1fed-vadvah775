package com.tennisscoreboard.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {
    private static final HikariDataSource dataSource;
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            // 1. Принудительная загрузка драйвера H2
            Class.forName("org.h2.Driver");
            System.out.println("H2 Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("H2 JDBC driver not found: " + e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:tennis_scoreboard;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setDriverClassName("org.h2.Driver");

        dataSource = new HikariDataSource(config);

        sessionFactory = createSessionFactory();
    }

    private static SessionFactory createSessionFactory(){
        Configuration configuration = new Configuration();

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");



        configuration.getProperties().put("hibernate.connection.datasource", dataSource);

        configuration.addAnnotatedClass(com.tennisscoreboard.model.Player.class);
        configuration.addAnnotatedClass(com.tennisscoreboard.model.Match.class);

        return configuration.buildSessionFactory();
    }
}
