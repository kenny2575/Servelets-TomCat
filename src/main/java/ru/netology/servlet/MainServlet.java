package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private PostController controller;

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
        controller = (PostController) context.getBean("postController");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            // primitive routing
            if (method.equals(GET) && path.equals("/api/posts")) {
                controller.all(resp);
                return;
            }

            if (method.equals(POST) && path.equals("/api/posts")) {
                controller.save(req.getReader(), resp);
                return;
            }

            if (path.matches("/api/posts/\\d+")) {
                long parseLong = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                final var id = parseLong;
                if (method.equals(GET)) {
                    controller.getById(id, resp);
                    return;
                }
                if (method.equals(DELETE)){
                    controller.removeById(id, resp);
                    return;
                }
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

