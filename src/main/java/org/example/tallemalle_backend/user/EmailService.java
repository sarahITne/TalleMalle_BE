package org.example.tallemalle_backend.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.api-url}")
    private String apiUrl;

    // 이메일 인증 메일 전송 메소드
    public void sendWelcomeMail(String uuid, String email) {
        // 전송될 메일 객체 생성
        MimeMessage message = mailSender.createMimeMessage();
        try {
            // MimeMessage를 더 쉽게 다루기 위한 Helper 객체 생성
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 수신자 이메일 설정 (받는 사람)
            helper.setTo(email);

            // 메일 제목 및 본문 설정
            String subject = "[탈래말래] 회원 가입 인증 메일";

            String verifyLink = apiUrl + "/user/verify?uuid=" + uuid;

            // 사용자가 클릭하면 /user/verify?uuid=xxxx로 요청이 감 (인증 링크)
            String htmlContents =
                    "<div style='background:#f6f8fb; padding:40px; font-family:Arial, sans-serif;'>"
                            + "  <div style='max-width:600px; margin:0 auto; background:white; padding:40px; border-radius:10px;'>"

                            + "    <h2 style='color:#4f46e5;'>탈래말래 이메일 인증</h2>"

                            + "    <p>안녕하세요. 탈래말래에 가입해주셔서 감사합니다.</p>"

                            + "    <p>아래 버튼을 눌러 이메일 인증을 완료해주세요.</p>"

                            + "    <div style='margin:30px 0;'>"
                            + "      <a href='" + verifyLink + "' "
                            + "      style='background:#4f46e5;"
                            + "      color:white;"
                            + "      padding:12px 25px;"
                            + "      text-decoration:none;"
                            + "      border-radius:6px;"
                            + "      font-weight:bold;'>"
                            + "      이메일 인증하기"
                            + "      </a>"
                            + "    </div>"

                            + "    <p style='color:#555;'>"
                            + "    버튼이 작동하지 않는 경우 아래 링크를 브라우저에 복사해주세요."
                            + "    </p>"

                            + "    <p style='word-break:break-all; color:#4f46e5;'>"
                            + "    " + verifyLink
                            + "    </p>"

                            + "    <hr style='margin:30px 0;'>"

                            + "    <p style='font-size:13px; color:#888;'>"
                            + "    본 이메일은 탈래말래 회원가입을 위한 인증 메일입니다.<br>"
                            + "    이메일 인증 후 로그인 및 서비스 이용이 가능합니다."
                            + "    </p>"

                            + "  </div>"
                            + "</div>";

            helper.setSubject(subject);
            helper.setText(htmlContents, true);

            // 실제 메일 전송
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
