package com.tennisscoreboard.servlet;

import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.util.MatchStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(value = "/ongoing-matches")
public class OngoingMatchesServlet extends HttpServlet {

    // Лучше явно получать зависимости в методе init(), а не обращаться к их статическим методам в коде.
        // Это является более идиоматичным подходом для сервлетов.
        // Метод init() специально предназначен для однократной инициализации ресурсов,
        // которые сервлет будет использовать на протяжении своего жизненного цикла.
        // Размещение этого кода в init() делает ваш код более предсказуемым и понятным для других разработчиков,
        // знакомых со спецификацией сервлетов.

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    // TODO: Сервлет работает с доменной моделью `OngoingMatch` и передаёт её во View.
        // Это нарушает границы между слоями приложения и Принцип разделения ответственности
        // (см. файл "Принцип разделения ответственности (Separation of Concerns).md" в этом же пакете).
        // Сервлет не должен работать с доменными моделями.
        // Вместо этого он должен "общаться" с другими слоями через DTO.

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // TODO: Сервлет не должен работать с доменными моделями.
        // TODO: MatchStorage стоит внедрять как зависимость, а не обращаться напрямую к статическому методу здесь.
        List<OngoingMatch> ongoingMatches = MatchStorage.getAll();
        req.setAttribute("ongoingMatches", ongoingMatches);
        req.getRequestDispatcher("/WEB-INF/views/ongoing-matches.jsp").forward(req, resp);
    }
}
