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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<OngoingMatch> ongoingMatches = MatchStorage.getAll();
        req.setAttribute("ongoingMatches", ongoingMatches);
        req.getRequestDispatcher("/WEB-INF/views/ongoing-matches.jsp").forward(req, resp);
    }
}
