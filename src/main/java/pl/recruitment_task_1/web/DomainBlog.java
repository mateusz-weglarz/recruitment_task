package pl.recruitment_task_1.web;

import pl.recruitment_task_1.dao.BlogDao;
import pl.recruitment_task_1.dao.UserDao;
import pl.recruitment_task_1.model.Blog;
import pl.recruitment_task_1.model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/domain/blog.php")
public class DomainBlog extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        UserDao userDao = new UserDao();
        BlogDao blogDao = new BlogDao();
        HttpSession session = request.getSession();

        if (action == null) {
            List<Blog> blogList = blogDao.findAll();
            for (Blog blog : blogList) {
                response.getWriter().append(String.valueOf(blog)).append("\n");
            }
        } else if (action.equals("login")) {
            String userName = request.getParameter("user");
            String password = request.getParameter("password");
            boolean logged = userDao.authorization(userName, password);
            if (logged) {
                session.setAttribute("loggedIn", true);
                // getting data of logged-in user
                List<User> userList = userDao.findAll();
                for (User user : userList) {
                    if (user.getUserName().equals(userName)) {
                        session.setAttribute("user", user);
                    }
                }
            }
            response.getWriter().append("User logged successfully.");
        } else if (action.equals("new")) {
            boolean logged = (boolean) session.getAttribute("loggedIn");
            if (logged) {
                User user = (User) session.getAttribute("user");
                String text = request.getParameter("text");
                Blog newBlog = new Blog();
                newBlog.setText(text);
                newBlog.setUserId(user.getUserId());
                blogDao.create(newBlog);
                response.getWriter().append("New blog entry created successfully.");
            }
        } else if (action.equals("new_user")) {
            User user = new User();
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String permission = request.getParameter("permission");
            String readonly = request.getParameter("readonly");
            if (username != null && password != null && permission != null && readonly != null) {
                user.setUserName(username);
                user.setPassword(password);
                user.setPermission(permission);
                user.setReadOnly(readonly);
                userDao.create(user);
                response.getWriter().append("New user created successfully.");
            }
        } else if (action.equals("delete")) {
            boolean logged = (boolean) session.getAttribute("loggedIn");
            if (logged) {
                Long id = Long.parseLong(request.getParameter("id"));
                blogDao.delete(id);
                response.getWriter().append("Blog entry deleted successfully.");
            }

        } else {
            response.getWriter().append("Bad action selected.");
        }
    }
}
