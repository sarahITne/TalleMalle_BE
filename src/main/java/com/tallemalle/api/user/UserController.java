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
    DataSource ds;


    public UserController(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getRequestURI().contains("signup") && req.getMethod().equals("POST")) {
            // JSON으로 받은 데이터를 DTO(객체)로 변환
            UserDto.SignupReq dto = JsonParser.from(req, UserDto.SignupReq.class);

            // DB에 CRUD
            String sql = "INSERT INTO users (email, password) VALUES (?, ?)";

            try {
                Class.forName("org.mariadb.jdbc.Driver");

                // DB 연결 객체를 다 사용하고 나면 반납할 수 있도록 수정
                try (Connection conn = ds.getConnection()) {
                    PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, dto.getEmail());
                    pstmt.setString(2, dto.getPassword());

                    pstmt.executeUpdate();
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        UserDto.SignupRes returnDto = new UserDto.SignupRes(
                                rs.getLong(1),
                                dto.getEmail()
                        );
                        return BaseResponse.success(returnDto);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (req.getRequestURI().contains("login") && req.getMethod().equals("POST")) {
            UserDto.LoginReq dto = JsonParser.from(req, UserDto.LoginReq.class);

            String sql = "SELECT * FROM users WHERE email=? AND password=?";

            try {
                Class.forName("org.mariadb.jdbc.Driver");

                try (Connection conn = ds.getConnection()) {
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, dto.getEmail());
                    pstmt.setString(2, dto.getPassword());

                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        // 토큰 생성해서 응답에 토큰을 넣어줘야 한다.
                        String key = "djkflajdklajf9032uj9042fjfoijsadkj0924jtfiorejoij90f429eodji";
                        SecretKey encodedKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));

                        // compact는 builder()라는 스태틱 메서드가 반환값으로 반환해준 것에 들어있는 메서드, 그러니까 builder는 아마도 클래스를 반환해주겠지요
                        // Jwts.builder().compact();

                        String jwt = Jwts.builder()
                                .claim("userId",rs.getInt("user_id"))
                                .claim("email",rs.getString("email"))
                                .issuedAt(new Date())
                                .expiration(new Date(System.currentTimeMillis() + 30000))
                                .signWith(encodedKey)
                                .compact();

                        resp.setHeader("Set-Cookie", "ATOKEN="+jwt + "; Path=/");

                        System.out.println(jwt);
                        return BaseResponse.success("로그인 성공");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
