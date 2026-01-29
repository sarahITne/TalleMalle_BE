package com.tallemalle.api.common;

import com.tallemalle.api.utils.JsonParser;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/")
@MultipartConfig
public class DispatcherServlet extends HttpServlet {
    private AppConfig appConfig;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.appConfig = new AppConfig();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        Controller controller = appConfig.getController(uri);

        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        BaseResponse res = controller.process(req, resp);
        resp.getWriter().write(JsonParser.from(res));
    }
}
