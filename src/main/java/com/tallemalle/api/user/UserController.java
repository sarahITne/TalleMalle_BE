package com.tallemalle.api.user;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.user.model.UserDto;
import com.tallemalle.api.utils.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
            System.out.println("login code");
        }
        return null;
    }
}
