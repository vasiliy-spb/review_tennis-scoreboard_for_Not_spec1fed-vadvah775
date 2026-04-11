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
    private final MatchScoreService matchScoreService = new MatchScoreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String uuid = req.getParameter("uuid");
            if (uuid == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing uuid parameter");
                return;
            }

            OngoingMatch match = MatchStorage.get(uuid);
            if (match == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Match not found");
                return;
            }

            req.setAttribute("match", match);
            req.getRequestDispatcher("/WEB-INF/views/match-score.jsp").forward(req, resp);
        } catch (DatabaseException e) {
            log("DB error", e);
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
        } catch (RuntimeException e) {
            log("Runtime error", e);
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
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid winnerId");
            return;
        }

        OngoingMatch match = MatchStorage.get(uuid);
        if (match == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Match not found");
            return;
        }

        if (match.isFinished()) {
            MatchStorage.remove(uuid);
            resp.sendRedirect(req.getContextPath() + "/matches");
            return;
        }

        try {
            matchScoreService.addPoint(match, winnerId);
        } catch (DatabaseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            log("Invalid argument", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        if (match.isFinished()) {
            MatchStorage.remove(uuid);
            // redirect to matches
            resp.sendRedirect(req.getContextPath() + "/matches");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
    }
}
