package com.tallemalle.api.user;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.user.model.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class UserRepository {
    DataSource ds;

    public UserRepository(DataSource ds) {
        this.ds = ds;
    }

    // 회원가입
    public UserDto.SignupRes save(UserDto.SignupReq dto) {
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
                    return returnDto;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // 로그인
    public UserDto.LoginRes findByEmailAndPasssword(UserDto.LoginReq dto) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";

        try {
            Class.forName("org.mariadb.jdbc.Driver");

            try (Connection conn = ds.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, dto.getEmail());
                pstmt.setString(2, dto.getPassword());

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    UserDto.LoginRes returnDto = new UserDto.LoginRes(
                            rs.getLong("user_id"),
                            rs.getString("email")
                    );
                    return returnDto;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
