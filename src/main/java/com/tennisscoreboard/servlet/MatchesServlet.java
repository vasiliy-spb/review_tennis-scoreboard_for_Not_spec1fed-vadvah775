package com.tennisscoreboard.servlet;

import com.tennisscoreboard.exception.DatabaseException;
import com.tennisscoreboard.model.Match;
import com.tennisscoreboard.service.MatchesService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(value = "/matches")
public class MatchesServlet extends HttpServlet {

    // Лучше получать зависимости в методе init().
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

    // TODO: Сервлет работает с JPA Entity.
        // Это нарушает границы между слоями приложения и Принцип разделения ответственности
        // (см. файл "Принцип разделения ответственности (Separation of Concerns).md" в этом же пакете).
        // Сервлет не должен работать с сущностями БД.
        // Вместо этого он должен "общаться" с другими слоями через DTO.

    // TODO: Сервлет передаёт в слой представления JPA сущности (`List<Match> matches`).
        // Передача Entity объектов в JSP не является хорошей практикой.
        // Это может привести к проблемам производительности (например, ленивая загрузка)
        // и безопасности (например, случайная передача чувствительных данных).
        // Кроме того, это связывает слой представления с моделью данных.
        // Лучше использовать DTO (Data Transfer Object) для передачи данных в представление.
        // DTO позволяют контролировать, какие именно данные передаются.

    private final MatchesService matchesService = new MatchesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pageParam = req.getParameter("page");
            String filterName = req.getParameter("filter_by_player_name");

            // Логику парсинга можно вынести во вспомогательный метод — так код станет более читаемым.
            int currentPage = 1;
            if (pageParam != null && !pageParam.isBlank()) {
                try {
                    currentPage = Integer.parseInt(pageParam);

                    // Тело блока if всегда стоит оборачивать в {}
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException ignored) {
                }
            }

            // Этот блок не имеет практического смысла условие WHERE (:playerName is NULL OR ...)
                // вернёт список матчей как для playerName == null, так и для playerName == " ".
            if (filterName != null && filterName.isBlank()) {
                filterName = null;
            }

            // TODO: Сервлет не должен работать с JPA Entity.
            List<Match> matches = matchesService.getMatchesPage(currentPage, filterName);
            long totalMatches = matchesService.getTotalMatchesCount(filterName);
            int totalPages = matchesService.getTotalPages(filterName);

            // TODO: Сервлет не должен передавать JPA Entity во View.
            req.setAttribute("matches", matches);

            // Вместо того чтобы передавать данные о странице с матчами по частям, можно создать для этого специальный DTO.
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("filterName", filterName == null ? "" : filterName);
            req.setAttribute("totalMatches", totalMatches);

            req.getRequestDispatcher("/WEB-INF/views/matches.jsp").forward(req, resp);

        // Логику обработки исключений можно реализовать в фильтре.
        } catch (DatabaseException e) {
            log("View matches database exception", e);

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            log("Unexpected error in matches", e);

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
