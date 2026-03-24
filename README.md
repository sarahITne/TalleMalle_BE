<div align="center">

<img width="280" alt="TalleMalle Logo" src="https://github.com/user-attachments/assets/3ae14639-49a6-415c-8407-bf0cb62fd85c" />

# 🚕 TalleMalle Backend

</div>

<br>

> ### 🔗 Project Links
> 🌐 **Web Service** : [TalleMalle 공식 서비스 접속하기](https://www.tallemalle.kro.kr)  
> 📘 **API Docs** : [Swagger API 명세서](http://www.tallemalle.kro.kr:8080/swagger-ui/index.html#/call-controller/settlement)  
> 📏 **Convention** : [팀 코딩 컨벤션 및 규칙 (Notion)](https://www.notion.so/2dfa4b6b459480e693d3f1e81cf9134a?source=copy_link)

<br>

## 👥 Team TalleMalle

<table align="center" width="100%">
  <tr>
    <td align="center" width="20%">
      <a href="https://github.com/shinukang">
        <img src="https://github.com/shinukang.png" width="90" style="border-radius: 50%;"><br/>
        <strong>강신우</strong>
      </a>
    </td>
    <td align="center" width="20%">
      <a href="https://github.com/saralove20">
        <img src="https://github.com/saralove20.png" width="90" style="border-radius: 50%;"><br/>
        <strong>김사라</strong>
      </a>
    </td>
    <td align="center" width="20%">
      <a href="https://github.com/pbgodsoo">
        <img src="https://github.com/pbgodsoo.png" width="90" style="border-radius: 50%;"><br/>
        <strong>박범수</strong>
      </a>
    </td>
    <td align="center" width="20%">
      <a href="https://github.com/hijaehyuk">
        <img src="https://github.com/hijaehyuk.png" width="90" style="border-radius: 50%;"><br/>
        <strong>이재혁</strong>
      </a>
    </td>
    <td align="center" width="20%">
      <a href="https://github.com/DongHyunj">
        <img src="https://github.com/DongHyunj.png" width="90" style="border-radius: 50%;"><br/>
        <strong>정동현</strong>
      </a>
    </td>
  </tr>
</table>

---

## 🛠 Tech Stack

### 🔹 Backend
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/SpringSecurity-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/JPA_Hibernate-59666C?style=flat-square)
![JWT](https://img.shields.io/badge/JWT_Auth-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-4285F4?style=flat-square&logo=oauth&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket_STOMP-00B894?style=flat-square)

### 🔹 DBMS & Storage
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS_S3-FF9900?style=flat-square&logo=amazons3&logoColor=white)

### 🔹 Infra & Deployment
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat-square&logo=amazonaws&logoColor=white)
![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=flat-square&logo=ubuntu&logoColor=white)

### 🔹 Collaboration
![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white)

---

| 기술 스택 | 선정 이유 및 활용 방안 |
| :--- | :--- |
| **Java 17 / Spring Boot 3.5.10** | Java 17 LTS와 Spring Boot 3.x 기반으로 안정적인 런타임을 확보하고, 최신 스프링 생태계 기능(Security, JPA, Actuator 등)을 일관되게 사용 |
| **Spring Security + JWT + OAuth2 Client** | 서버 세션 의존도를 낮춘 인증 구조를 위해 JWT(ATOKEN) 기반 인증을 적용하고, 소셜 로그인은 OAuth2 Client로 연동 |
| **Spring Data JPA (Hibernate)** | 도메인 중심 개발 및 트랜잭션 관리를 단순화하고, Fetch Join/락 쿼리 등으로 성능·정합성 제어 |
| **MariaDB (mariadb-java-client 3.4.1)** | 관계형 데이터 일관성과 트랜잭션 처리가 중요한 모집/참여/호출 도메인에 적합하여 채택 |
| **WebSocket + STOMP** | 모집 상태 변경, 호출/운행 이벤트, 알림 등을 클라이언트에 실시간 반영하기 위해 사용 |
| **Web Push (web-push 5.1.1)** | 브라우저 비활성 상태에서도 중요한 매칭/운행 알림을 전달하기 위해 도입 |
| **AWS S3 (spring-cloud-aws-starter-s3 3.4.2)** | 프로필/첨부 파일을 애플리케이션 서버와 분리 저장해 배포·확장 시 안정적으로 파일 제공 |

---

## 📚 Documents & Wiki

> **프로젝트의 상세한 내용은 아래 Wiki 링크에서 확인하실 수 있습니다.**

* 🎯 [**프로젝트 개요 (Project Overview)**](https://github.com/beyond-sw-camp/be24-3rd-saraITne-TalleMalle/wiki/1.-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B0%9C%EC%9A%94-(Project-Overview))
* 🧩 [**기술 선정 이유 (Tech Stack & Rationale)**](https://github.com/beyond-sw-camp/be24-3rd-saraITne-TalleMalle/wiki/2.-%EA%B8%B0%EC%88%A0-%EC%84%A0%EC%A0%95-%EC%9D%B4%EC%9C%A0-(Tech-Stack-&-Rationale))
* 🏗 [**시스템 아키텍처 (Software Architecture)**](https://github.com/beyond-sw-camp/be24-3rd-saraITne-TalleMalle/wiki/3.-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98-(System-Architecture)-%EB%B0%8F-SW-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98-(Software-Architecture))
* ✨ [**코딩 컨벤션 (Coding Convention)**](https://github.com/beyond-sw-camp/be24-3rd-saraITne-TalleMalle/wiki/4.-%EC%BD%94%EB%94%A9-%EC%BB%A8%EB%B2%A4%EC%85%98)
* 🚀 [**성능 개선 (Performance Improvement)**](https://github.com/beyond-sw-camp/be24-3rd-saraITne-TalleMalle/wiki/6.-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0-(Performance-Improvement)-%F0%9F%9A%80)

<details>
<summary><b>🖼️ ERD (Entity Relationship Diagram) 보기</b></summary>
<div markdown="1">
<br>

![ERD](https://github.com/user-attachments/assets/e165850c-09ce-4e15-9e6a-f05455d937f9)
</div>
</details>

---

## 🔄 Service Flow

### 🙋‍♂️ 탑승객 (Passenger)
1. **가입 및 본인 확인** : 이메일 가입 및 휴대폰 본인인증으로 신원을 확인합니다.
2. **로그인** : 이메일 또는 소셜(카카오 등) 계정으로 로그인합니다.
3. **동승 모집** : "같이 갈 사람 모집" 글을 올리거나, 기존에 올라온 모집에 **참여 신청**합니다.
4. **정원 마감** : 인원이 다 차면 모집 상태가 마감되며 기사 연결 단계로 넘어갑니다.

### 🚖 시스템 및 기사 (System & Driver)
5. **기사 배정 요청 (콜)**
    - 정원이 찼고 출발 예정 시각 전이라면, 서버가 **약 1분 주기**로 확인하여 "배정 요청" 상태로 전환합니다.
    - 새 요청은 기사 앱 화면에 실시간 알림(웹 소켓 통신)으로 전달됩니다.
6. **기사 앱 운행**
    - 기사 전용 계정으로 로그인 후, 배정 요청을 **수락**하고 `운행 시작` ➔ `운행 종료` 프로세스를 진행합니다.

### 💳 정산 및 알림 (Payment & Notification)
7. **요금 정산 및 결제**
    - 운행이 끝나면 기사 앱에 N분의 1로 나뉜 예상 요금이 표시됩니다.
    - 기사가 "탑승객 결제하기"를 누르면, 탑승객이 등록한 카드(**토스 결제**)로 각각 청구됩니다.
8. **실시간 알림 전송**
    - 배정, 운행 시작, 결제 완료 등 주요 진행 상황은 앱 내 알림에 저장되며, 브라우저 **푸시(Push) 알림**으로도 발송됩니다.

---

<div align="center">
  <br>
  <b>🚕 Backend powered by TalleMalle</b>
</div>
