package servlet;

import service.MatchesService;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "MatchesServlet", urlPatterns = {"/matches"})
public class MatchesServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        String destination = "/WEB-INF/matches.jsp";
        request.setAttribute("latestMatches", MatchesService.getLatestMatches());
        request.setAttribute("upcomingMatches", MatchesService.getUpcomingMatches());
        RequestDispatcher rd = getServletContext().getRequestDispatcher(destination);
        rd.forward(request, response);
    }
}
