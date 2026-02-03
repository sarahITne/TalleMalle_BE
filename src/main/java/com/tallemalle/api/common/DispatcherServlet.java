package com.tallemalle.api.common;

import com.tallemalle.api.auth.context.LoginUser;
import com.tallemalle.api.utils.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
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

        // 로그인 필요한 api
        boolean isProtected = req.getRequestURI().contains("/notice/read")
                            || req.getRequestURI().contains("/notification/list")
                            || req.getRequestURI().contains("/notification/summary")
                            || req.getRequestURI().contains("/notification/readonly")
                            || req.getRequestURI().contains("/notification/readall");

        // 1. /notice/read 인 경우 로그인 체크 (공지사항 상세 조회)
        if (isProtected) {

            String token = null;

            // 2. ATOKEN 쿠키 찾기
            if (req.getCookies() != null) {
                for (Cookie cookie : req.getCookies()) {
                    if ("ATOKEN".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            // 3. 토큰 없으면 → 로그인 필요
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(
                        JsonParser.from(BaseResponse.fail("로그인 하세요"))
                );
                return;
            }

            // 4. 토큰 검증
            try {
                String key = "djkflajdklajf9032uj9042fjfoijsadkj0924jtfiorejoij90f429eodji";
                SecretKey encodedKey = Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(key)
                );

                Claims claims = Jwts.parser()
                        .verifyWith(encodedKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                // 필요 시 사용자 정보 전달
                System.out.println(claims.get("email", String.class));

                // 사용자 정보 LoginUser라는 컨텍스트에 저장
                LoginUser loginUser = LoginUser.from(claims);
                req.setAttribute("LoginUser", loginUser);
//                req.setAttribute("email", claims.get("email", String.class));

            } catch (Exception e) {
                // 토큰 만료 / 위조
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(
                        JsonParser.from(BaseResponse.fail("로그인 하세요"))
                );
                return;
            }
        }

        // 로그인 필요 없는 api -> 바로 실행
        BaseResponse res = controller.process(req, resp);
        resp.getWriter().write(JsonParser.from(res));
    }
}
