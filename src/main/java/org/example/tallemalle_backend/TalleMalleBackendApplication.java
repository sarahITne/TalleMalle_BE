package org.example.tallemalle_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TalleMalleBackendApplication {

    public static void main(String[] args) {
        // [환경 변수 로드 로직]
        // 실행 시 VM 옵션에서 -DENV_FILE=.env.prod 처럼 지정할 수 있음
        // 지정하지 않으면 기본값으로 .env를 사용
        String envFileName = System.getProperty("ENV_FILE", ".env.prod");

        Dotenv dotenv = Dotenv.configure()
                .filename(envFileName)
                .ignoreIfMissing()
                .load();

        // 읽어온 변수들을 시스템 속성으로 등록하여 Spring 환경 변수(${...})에서 사용할 수 있게 함
        dotenv.entries().forEach(entry -> {
//            if (System.getProperty(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
//            }
        });

        SpringApplication.run(TalleMalleBackendApplication.class, args);
    }

}
