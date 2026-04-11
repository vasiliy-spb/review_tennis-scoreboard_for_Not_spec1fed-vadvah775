package com.tennisscoreboard.servlet;

import com.tennisscoreboard.exception.DatabaseException;
import com.tennisscoreboard.service.NewMatchService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(value = "/new-match")
public class NewMatchServlet extends HttpServlet {
    private final NewMatchService newMatchService = new NewMatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/views/new-match.jsp");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String playerOneName = req.getParameter("playerOne");
        String playerTwoName = req.getParameter("playerTwo");

        if (playerOneName == null || playerOneName.trim().isEmpty() ||
                playerTwoName == null || playerTwoName.trim().isEmpty()) {
            req.setAttribute("error", "Both player names are required");
            req.getRequestDispatcher("/WEB-INF/views/new-match.jsp").forward(req, resp);
            return;
        }
        if (playerOneName.trim().equals(playerTwoName.trim())) {
            req.setAttribute("error", "Player names must be different");
            req.getRequestDispatcher("/WEB-INF/views/new-match.jsp").forward(req, resp);
            return;
        }

        if (newMatchService.checkPlayersBusy(playerOneName, playerTwoName)){
            req.setAttribute("error", "This player is already busy");
            req.getRequestDispatcher("/WEB-INF/views/new-match.jsp").forward(req, resp);
            return;
        }

        try {
            String uuid = newMatchService.createNewMatch(playerOneName, playerTwoName);
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
        } catch (DatabaseException e) {
            log("Data base", e);
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
        } catch (IllegalArgumentException e) {
            log("Invalid argument", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }
}
