package camp.woowa.jspcafe.servlets;

import camp.woowa.jspcafe.exception.CustomException;
import camp.woowa.jspcafe.models.User;
import camp.woowa.jspcafe.services.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "usersServlet", urlPatterns = {"/users/*"})
public class UsersServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        ServletContext sc = getServletContext();
        userService = (UserService) sc.getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "".equalsIgnoreCase(pathInfo)) {
            List<User> users = userService.findAll();
            req.setAttribute("users", users); // user 리스트 정보를 가져옴
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/user/list.jsp");

            try {
                dispatcher.forward(req, resp);
            } catch (ServletException | IOException e) {
                log(e.getMessage());
            }
        } else if (pathInfo.endsWith("/form")) { // POST /users/{id}/form 필터링
            String[] split = pathInfo.split("/");
            long id = 0;
            try { // id 가 long인지 확인
                id = Long.parseLong(split[1]);
            } catch (NumberFormatException e) {
                log(e.getMessage());
            }
            User user = userService.findById(id);
            req.setAttribute("user", user);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/user/updateForm.jsp");

            try {
                dispatcher.forward(req, resp);
            } catch (ServletException | IOException e) {
                log(e.getMessage());
            }
        } else {
            try {
                User user = userService.findById(Long.parseLong(pathInfo.substring(1)));
                req.setAttribute("user", user);
            } catch (NumberFormatException e) {
                log(e.getMessage());
            }

            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/user/profile.jsp");

            try {
                dispatcher.forward(req, resp);
            } catch (ServletException | IOException e) {
                log(e.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equalsIgnoreCase(pathInfo) || "".equalsIgnoreCase(pathInfo)) { // POST /users 필터링
            userService.createUser(req.getParameter("userId"),
                    req.getParameter("password"),
                    req.getParameter("name"),
                    req.getParameter("email"));
            try {
                res.sendRedirect("/users");
            } catch (IOException e) {
                log(e.getMessage());
            }
        } else if (pathInfo.endsWith("/form")) { // POST /users/{id}/form 필터링
            String[] split = pathInfo.split("/");
            long id = 0;
            try { // id 가 long인지 확인
                id = Long.parseLong(split[1]);
            } catch (NumberFormatException e) {
                log(e.getMessage());
            }

            userService.update(id,
                    req.getParameter("password"),
                    req.getParameter("userId"),
                    req.getParameter("name"),
                    req.getParameter("email"));

            try {
                res.sendRedirect("/users/" + id + "/form");
            } catch (IOException e) {
                log(e.getMessage());
            }
        }
    }
}
