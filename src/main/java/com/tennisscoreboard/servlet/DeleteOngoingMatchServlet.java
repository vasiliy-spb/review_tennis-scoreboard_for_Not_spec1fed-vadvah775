package com.tennisscoreboard.servlet;

import com.tennisscoreboard.util.MatchStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(value = "/delete-ongoing-match")
public class DeleteOngoingMatchServlet extends HttpServlet {

    // Лучше явно получать зависимости в методе init(), а не обращаться к их статическим методам в коде.
        // Это является более идиоматичным подходом для сервлетов.
        // Метод init() специально предназначен для однократной инициализации ресурсов,
        // которые сервлет будет использовать на протяжении своего жизненного цикла.
        // Размещение этого кода в init() делает ваш код более предсказуемым и понятным для других разработчиков,
        // знакомых со спецификацией сервлетов.

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    // TODO: Сервлет отправляет сообщение из исключения (`e.getMessage()`) напрямую пользователю.
        // Сообщения об ошибках из исключений могут содержать технические детали, которые не предназначены
        // для конечного пользователя и могут представлять угрозу безопасности. Например, сообщение может быть
        // `"No entity found for query 'SELECT ...'"` или `"Validation failed for field 'internalFieldName'"`,
        // что раскрывает структуру БД или внутренние имена полей.
        //
        // Лучше никогда не отправлять необработанное сообщение из исключения на клиент.
        // Вместо этого можно использовать заранее определённые, безопасные сообщения или коды ошибок.
        // Само исключение при этом нужно логировать для разработчиков.
        //
        // Это повысит безопасность приложения и улучшит пользовательский опыт при возникновении ошибок.

    // Логику обработки исключений можно реализовать в фильтре.
        // Так она будет централизована для всего приложения и её части не будут повторяться в разных местах.

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String uuid = req.getParameter("uuid");
            if (uuid != null && !uuid.isBlank()) {

                // TODO: MatchStorage стоит внедрять как зависимость, а не обращаться напрямую к статическому методу здесь.
                MatchStorage.remove(uuid);
            }
            resp.sendRedirect(req.getContextPath() + "/ongoing-matches");

        // Логику обработки исключений можно реализовать в фильтре.
        } catch (Exception e){
            log("Error deleting ongoing match", e);

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
