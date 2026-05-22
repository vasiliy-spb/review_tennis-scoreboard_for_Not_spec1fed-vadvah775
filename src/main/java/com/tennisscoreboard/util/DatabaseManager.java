package com.tennisscoreboard.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {

    // TODO: В текущей реализации отсутствует метод для закрытия `SessionFactory` при остановке приложения.
        // `SessionFactory` владеет важными ресурсами, включая пул соединений с базой данных.
        // Если эти ресурсы не освободить корректно, это может привести к утечкам памяти и проблемам с подключением к БД,
        // особенно в окружениях, которые не управляют жизненным циклом приложения автоматически.
        //
        // Стоит добавить статический метод `shutdown()` (покажу в примере к следующему пункту),
        // который будет закрывать `SessionFactory` и вызывать его при завершении работы приложения (например, в `contextDestroyed` для `ServletContextListener`).
        // Такой подход гарантирует, что все ресурсы, удерживаемые Hibernate, будут освобождены при остановке приложения
        // и предотвращает потенциальные утечки и ошибки, связанные с "висячими" соединениями.

    // Всю конфигурацию можно вынести в файл настроек (`src/main/resources/hibernate.cfg.xml`).

    private static final HikariDataSource dataSource;
    @Getter
    private static final SessionFactory sessionFactory;

    static {

        // Этот блок try-catch избыточен. Драйвер зарегистрируется автоматически через механизм Service Provider Interface (SPI).
        try {
            // Стоит удалять комментарии (вроде того, что указан в следующей строке) из кода перед тем, как выполнять коммит
            // 1. Принудительная загрузка драйвера H2
            Class.forName("org.h2.Driver");

            // Использование `System.out` для логирования — плохая практика в серверных приложениях.
                // Логи должны выводиться через специализированный фреймворк.
            System.out.println("H2 Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("H2 JDBC driver not found: " + e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:tennis_scoreboard;DB_CLOSE_DELAY=-1");

        // TODO: Секретны не должны попадать в github.
            // Помещение любых (реальных) секретных учётных данных в код и их публикация на github —
            // это критическая уязвимость безопасности.
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
        // Можно добавить configuration.setProperty("hibernate.highlight_sql", "true"); для подсветки синтаксиса


        configuration.getProperties().put("hibernate.connection.datasource", dataSource);

        configuration.addAnnotatedClass(com.tennisscoreboard.model.Player.class);
        configuration.addAnnotatedClass(com.tennisscoreboard.model.Match.class);

        return configuration.buildSessionFactory();
    }
}
