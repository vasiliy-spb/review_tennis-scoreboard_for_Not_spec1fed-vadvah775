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
    private final MatchesService matchesService = new MatchesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pageParam = req.getParameter("page");
            String filterName = req.getParameter("filter_by_player_name");

            int currentPage = 1;
            if (pageParam != null && !pageParam.isBlank()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException ignored) {
                }
            }

            if (filterName != null && filterName.isBlank()) {
                filterName = null;
            }

            List<Match> matches = matchesService.getMatchesPage(currentPage, filterName);
            long totalMatches = matchesService.getTotalMatchesCount(filterName);
            int totalPages = matchesService.getTotalPages(filterName);

            req.setAttribute("matches", matches);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("filterName", filterName == null ? "" : filterName);
            req.setAttribute("totalMatches", totalMatches);

            req.getRequestDispatcher("/WEB-INF/views/matches.jsp").forward(req, resp);
        } catch (DatabaseException e) {
            log("View matches database exception", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            log("Unexpected error in matches", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
