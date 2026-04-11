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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String uuid = req.getParameter("uuid");
            if (uuid != null && !uuid.isBlank()) {
                MatchStorage.remove(uuid);
            }
            resp.sendRedirect(req.getContextPath() + "/ongoing-matches");
        } catch (Exception e){
            log("Error deleting ongoing match", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
