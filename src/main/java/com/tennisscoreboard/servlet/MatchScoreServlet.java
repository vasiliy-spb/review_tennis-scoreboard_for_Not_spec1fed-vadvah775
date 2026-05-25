package com.tennisscoreboard.servlet;

import com.tennisscoreboard.exception.DatabaseException;
import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.service.MatchScoreService;
import com.tennisscoreboard.util.MatchStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(value = "/match-score")
public class MatchScoreServlet extends HttpServlet {

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

    // TODO: Сервлет работает с доменной моделью `OngoingMatch` и передаёт её во View.
        // Это нарушает границы между слоями приложения и Принцип разделения ответственности
        // (см. файл "Принцип разделения ответственности (Separation of Concerns).md" в этом же пакете).
        // Сервлет не должен работать с доменными моделями.
        // Вместо этого он должен "общаться" с другими слоями через DTO.

    // TODO: Сервлет берёт на себя лишнюю ответственность — оркестрирует взаимодействие между несколькими сервисами (MatchStorage и MatchScoreService),
        // хотя его задача — только принимать HTTP-запросы и делегировать их обработку. Это нарушает принцип единственной ответственности (SRP)
        // и делает код сервлета более сложным и трудным для тестирования.
        // Сервлет должен быть "тонким контроллером", делегирующим всю бизнес-логику одному фасадному сервису.
        // (см. файл "fat-controller.md" в этом же пакете)

    private final MatchScoreService matchScoreService = new MatchScoreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String uuid = req.getParameter("uuid");

            // В doPost есть проверка на uuid.isBlank(), а здесь нет
            if (uuid == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing uuid parameter");
                return;
            }

            // TODO: Сервлет не должен работать с доменными моделями.
            // TODO: MatchStorage стоит внедрять как зависимость, а не обращаться напрямую к статическому методу здесь.
            OngoingMatch match = MatchStorage.get(uuid);
            if (match == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Match not found");
                return;
            }

            req.setAttribute("match", match);
            req.getRequestDispatcher("/WEB-INF/views/match-score.jsp").forward(req, resp);

        // Логику обработки исключений можно реализовать в фильтре.
        } catch (DatabaseException e) {
            log("DB error", e);

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
        } catch (RuntimeException e) {
            log("Runtime error", e);

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            log("Unexpected error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = req.getParameter("uuid");
        String winnerIdStr = req.getParameter("winnerId");

        if (uuid == null || uuid.isBlank() || winnerIdStr == null || winnerIdStr.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return;
        }

        Long winnerId;
        try {
            winnerId = Long.parseLong(winnerIdStr);

        // Логику обработки исключений можно реализовать в фильтре.
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid winnerId");
            return;
        }

        // TODO: Сервлет не должен работать с доменными моделями.
        // TODO: MatchStorage стоит внедрять как зависимость, а не обращаться напрямую к статическому методу здесь.
        OngoingMatch match = MatchStorage.get(uuid);
        if (match == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Match not found");
            return;
        }

        // Этот if является недостижимым в логике приложения — его стоит удалить.
        if (match.isFinished()) {

            // TODO: MatchStorage стоит внедрять как зависимость, а не обращаться напрямую к статическому методу здесь.
            MatchStorage.remove(uuid);
            resp.sendRedirect(req.getContextPath() + "/matches");
            return;
        }

        try {

            // TODO: Race condition при обработке выигранного очка.
                // Если пользователь очень быстро нажмёт кнопку выигрыша очка, браузер отправит два POST-запроса почти одновременно.
                // Tomcat обработает эти два запроса в двух разных потоках, но так как оба потока будут работать с одним и тем же общим объектом `OngoingMatch`,
                // будет возникать ситуация, когда счёт изменится только один раз.
                // Чтобы это исправить, нужно гарантировать, что только один поток может изменять состояние конкретного матча в один момент времени.
            matchScoreService.addPoint(match, winnerId);

        // Логику обработки исключений можно реализовать в фильтре.
        } catch (DatabaseException e) {

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            log("Invalid argument", e);

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        } catch (RuntimeException e) {

            // TODO: Не стоит отправлять сообщение из исключения (e.getMessage()) напрямую во View
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        // Сервлеет не должен заниматься этой бизнес-логикой — она должна быть в сервисном слое.
        if (match.isFinished()) {

            // TODO: MatchStorage стоит внедрять как зависимость, а не обращаться напрямую к статическому методу здесь.
            MatchStorage.remove(uuid);
            // redirect to matches
            resp.sendRedirect(req.getContextPath() + "/matches");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
    }
}
