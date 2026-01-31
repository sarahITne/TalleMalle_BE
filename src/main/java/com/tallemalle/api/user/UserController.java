package com.tallemalle.api.user;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.user.model.UserDto;
import com.tallemalle.api.utils.JsonParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.sql.*;

public class UserController implements Controller {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getRequestURI().contains("signup") && req.getMethod().equals("POST")) {
            // JSON으로 받은 데이터를 DTO(객체)로 변환
            UserDto.SignupReq dto = JsonParser.from(req, UserDto.SignupReq.class);
            UserDto.SignupRes returnDto = userService.signup(dto);

        } else if (req.getRequestURI().contains("login") && req.getMethod().equals("POST")) {
            UserDto.LoginReq dto = JsonParser.from(req, UserDto.LoginReq.class);
            UserDto.LoginRes returnDto = userService.login(dto);

            // 서비스로부터 정상적으로 Dto를 반환 받으면 (= 로그인 성공 하면)
            if (returnDto != null) {
                String key = "djkflajdklajf9032uj9042fjfoijsadkj0924jtfiorejoij90f429eodji";
                SecretKey encodedKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));

                String jwt = Jwts.builder()
                        .claim("userId", returnDto.getUserId())
                        .claim("email", returnDto.getEmail())
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + 30000))
                        .signWith(encodedKey)
                        .compact();

                resp.setHeader("Set-Cookie", "ATOKEN="+jwt + "; Path=/");

                System.out.println(jwt);
                return BaseResponse.success(returnDto);
            } else {
                return BaseResponse.fail("로그인 실패!");
            }
        }
        return null;
    }
}
