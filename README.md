# SISTTICKET — 공연 예매 플랫폼

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.1.1" />
  <img src="https://img.shields.io/badge/Spring%20Security-Authentication%20%26%20Authorization-6DB33F?logo=springsecurity&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/JPA-Hibernate-blue" alt="Spring Data JPA" />
  <img src="https://img.shields.io/badge/Database-MySQL-4479A1?logo=mysql&logoColor=white" alt="MySQL" />
</p>

**SISTTICKET**은 공연 탐색부터 회차별 좌석 선택, 좌석 선점, 주문·결제 상태 관리까지 이어지는 공연 예매 웹 애플리케이션입니다. 회원용 화면과 관리자 화면을 분리하고, 좌석 선점 만료와 주문 상태 전이를 서버에서 처리하도록 구성했습니다.

- **Backend:** [hohun21/SistTicket](https://github.com/hohun21/SistTicket)
- **Frontend:** [hohun21/sistticket-frontend](https://github.com/hohun21/sistticket-frontend)

> 결제는 코드상 모의 성공·실패 시나리오를 처리하며, 외부 PG사의 실결제 연동은 확인되지 않았습니다. 매출 관리 메뉴 등 아직 구현되지 않은 화면은 별도로 표시했습니다.

---

## 목차

- [프로젝트 개요](#프로젝트-개요)
- [핵심 기능](#핵심-기능)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [주요 구현 포인트](#주요-구현-포인트)
- [데이터 모델](#데이터-모델)
- [프로젝트 구조](#프로젝트-구조)
- [로컬 실행 방법](#로컬-실행-방법)
- [빌드 및 배포 준비](#빌드-및-배포-준비)
- [시연 화면](#시연-화면)
- [현재 범위 및 개선 과제](#현재-범위-및-개선-과제)

## 프로젝트 개요

| 항목 | 내용 |
|---|---|
| 프로젝트 | SISTTICKET — 공연 예매 플랫폼 |
| Backend | Java 21, Spring Boot, Spring MVC, Spring Data JPA |
| Frontend | React 기반 별도 저장소 |
| 인증·인가 | Spring Security, 세션 기반 로그인, BCrypt 비밀번호 인코딩 |
| 데이터베이스 | MySQL JDBC Driver 기반 관계형 데이터베이스 |
| 주요 도메인 | 공연, 회차, 좌석, 회원, 주문, 결제, 리뷰, 관리자 |

## 핵심 기능

### 사용자 기능

- **공연 탐색:** 공연 목록 및 상세 정보, 장르·공연장·회차 정보 조회
- **좌석 선택 및 선점:** 공연 회차별 좌석 조회, 좌석 선점·해제, 선점 만료 시간 관리
- **예매 및 주문:** 선점한 좌석과 토큰을 검증한 뒤 주문 생성
- **결제 상태 처리:** 대기·성공·실패·취소 상태 관리와 실패 결제 재시도 흐름
- **회원 관리:** 회원가입, 로그인, 이메일 중복 확인, 아이디 찾기 및 비밀번호 재설정 흐름
- **마이페이지 및 리뷰:** 회원별 정보·예매 관련 기능과 공연 리뷰 관리 API

### 관리자 기능

- **대시보드:** 운영 현황 요약 데이터 조회
- **회원 관리:** 회원 목록 조회 및 상태 변경 요청 처리
- **예매 관리:** 예매 목록·상세 조회
- **취소 관리:** 취소 요청 목록·상세 조회 및 취소 승인 처리
- **공연 설정:** 공연·회차 등 운영 데이터를 관리하는 API 및 기존 관리 화면

> 프런트엔드의 매출 관리 메뉴는 현재 플레이스홀더 화면으로 확인됩니다. 구현 완료 기능처럼 표현하지 않습니다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot 4.1.1, Spring MVC, Spring Data JPA, Spring Security, Bean Validation |
| View / Template | Thymeleaf 및 Thymeleaf Spring Security 확장 |
| ORM / Query | Hibernate, JPA Repository, JPQL |
| Database | MySQL JDBC Driver, H2 테스트 의존성 |
| Build | Gradle Wrapper |
| Boilerplate | Lombok |
| Frontend 연동 | React 프런트엔드의 API 요청 처리 |

## 시스템 아키텍처

~~~mermaid
flowchart LR
    U[사용자 브라우저] --> FE[React + Vite 프런트엔드]
    FE -->|HTTP / JSON · 세션 쿠키| SEC[Spring Security]
    SEC --> CTRL[Spring MVC REST Controller]
    CTRL --> SVC[Service 계층]
    SVC --> REPO[Spring Data JPA Repository]
    REPO --> DB[(MySQL)]
    SCH[주문 만료 스케줄러] --> SVC
~~~

- 컨트롤러는 요청·응답을 처리하고, 서비스 계층에서 예매·주문·결제 관련 규칙을 검증합니다.
- JPA Repository를 통해 엔티티를 조회·수정하며, 상태 변경이 필요한 작업은 트랜잭션 안에서 처리합니다.
- 인증 정보는 Spring Security를 통해 확인하고, **/api/admin/** 경로에는 관리자 권한을 적용합니다.
- 주문 만료 스케줄러는 만료 후보 주문을 주기적으로 확인해 후속 정리 작업을 실행합니다.

## 주요 구현 포인트

### 1. 좌석 선점과 중복 예매 방지

좌석은 기본 좌석 정보와 특정 공연 회차에 연결된 좌석 정보(**performance_seats**)로 나뉩니다. 선점 요청에서는 좌석 ID의 유효성·중복 여부·최대 선택 수를 검증하고, Repository의 비관적 쓰기 잠금을 이용해 대상 좌석을 조회합니다.

- 주문 한 건에 포함할 수 있는 좌석은 **최대 5개**로 제한합니다.
- 선점 좌석에는 토큰, 회원, 만료 시각을 기록해 주문 생성 시 본인 선점 여부를 검증합니다.
- (**performance_id, seat_id**) 유니크 제약으로 같은 회차의 동일 기본 좌석이 중복 생성되지 않도록 설계했습니다.
- 선점/주문 처리에는 **@Transactional**을 적용해 상태 변경이 부분적으로 반영되는 위험을 줄입니다.

### 2. 만료 주문 자동 정리

**OrderExpirationScheduler**는 **60초 간격**으로 만료 후보 주문을 조회합니다. 각 주문은 서비스 계층의 만료 처리 로직으로 전달되며, 한 건에서 예외가 발생하더라도 나머지 주문 처리를 계속하도록 구성되어 있습니다.

### 3. 주문과 결제 상태 전이

주문과 결제 데이터를 분리해 관리합니다. 결제 완료 처리에서는 주문·결제 데이터를 잠금 조회하고, 로그인 회원의 소유 주문인지 확인한 뒤 결제 상태 및 예매 좌석 상태를 갱신합니다.

| 도메인 | 코드에서 확인되는 상태/처리 예시 |
|---|---|
| 좌석 | **AVAILABLE**, **HELD** 등 선점 상태 관리 |
| 예매 상세 | **CONFIRMED**, **EXPIRED**, **CANCELLED**, **CANCELREQUEST** |
| 결제 | **PENDING**, **SUCCESS**, **FAILED**, **CANCELLED** |

결제 API에는 모의 성공·실패 처리와 실패 결제 재시도 엔드포인트가 있습니다. 실제 PG사 결제 승인·웹훅 연동이 구현되었다고 간주하지 않습니다.

### 4. 인증과 권한 분리

- 이메일과 비밀번호를 사용하는 Spring Security 로그인 흐름
- BCrypt 기반 비밀번호 인코딩
- **ROLE_ADMIN**을 기준으로 한 관리자 API 권한 확인
- 프런트엔드 관리자 경로와 백엔드 관리자 API 양쪽에서 접근을 제한하는 구조

## 데이터 모델

아래 다이어그램은 주요 JPA 엔티티의 관계를 요약한 것입니다. 실제 스키마의 전체 컬럼·인덱스·제약 조건을 대신하는 ERD는 아닙니다.

~~~mermaid
erDiagram
    MEMBERS ||--o{ ORDERS : places
    PERFORMANCES ||--o{ ORDERS : booked_for
    CONCERTS ||--o{ PERFORMANCES : has
    GENRE ||--o{ CONCERTS : classifies
    VENUES o|--o{ CONCERTS : hosts
    CONCERTS ||--o{ CONCERT_IMGS : displays
    PERFORMANCES ||--o{ PERFORMANCE_SEATS : defines
    SEATS_GRADE ||--o{ SEATS : classifies
    SEATS ||--o{ PERFORMANCE_SEATS : mapped_to
    ORDERS ||--o{ ORDERS_DETAIL : contains
    PERFORMANCE_SEATS ||--o{ ORDERS_DETAIL : selected_as
    ORDERS ||--o{ PAYMENTS : payment_records
    MEMBERS ||--o{ REVIEWS : writes
    CONCERTS ||--o{ REVIEWS : receives
~~~

### 주요 테이블 역할

| 테이블 | 역할 |
|---|---|
| **members**, **members_roles** | 회원 기본 정보와 권한 |
| **concerts**, **performances** | 공연 메타데이터와 회차·예매 일정 |
| **genre**, **venues**, **concert_imgs** | 장르, 공연장, 공연 이미지 |
| **seats_grade**, **seats**, **performance_seats** | 좌석 등급·기본 좌석·회차별 좌석 상태 및 선점 정보 |
| **orders**, **orders_detail** | 주문 헤더와 주문에 포함된 좌석·예매 상태 |
| **payments** | 주문별 결제 수단, 결제 키, 금액 및 결제 상태 |
| **reviews** | 회원이 공연에 작성한 리뷰 |

## 프로젝트 구조

~~~text
src/main/java/org/doit/sistticket/
├── admin/       # 관리자 API 및 운영 기능
├── actor/       # 출연진 관련 도메인
├── category/    # 공연·회차·장르·역할 정보
├── mainpage/    # 메인 화면 데이터
├── member/      # 회원·권한·인증 관련 기능
├── mypage/      # 마이페이지
├── orders/      # 주문·예매 상세·만료 처리
├── payments/    # 결제 상태 처리
├── review/      # 리뷰
├── seat/        # 좌석 조회·선점·상태 변경
├── venue/       # 공연장
└── config/      # Spring Security 등 설정

db/
└── seat_payment_test.sql  # 좌석·주문·결제 테스트용 MySQL fixture
~~~

## 로컬 실행 방법

### 1. 사전 준비

- JDK 21
- MySQL 로컬 인스턴스
- Git
- 프런트엔드 실행 시 Node.js와 npm

### 2. 저장소 내려받기

~~~bash
git clone https://github.com/hohun21/SistTicket.git
cd SistTicket
~~~

### 3. 데이터베이스 및 설정 준비

현재 저장소에는 전체 개발 환경을 위한 **application.properties** 파일이 포함되어 있지 않습니다. 로컬에서 사용할 설정 파일을 **src/main/resources/application.properties**에 만들고, 실제 DB 계정 및 포트에 맞게 수정하세요. 아래는 **로컬 개발용 예시**입니다.

~~~properties
server.port=80
spring.datasource.url=jdbc:mysql://localhost:3306/sistticket?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
~~~

- 위 예시는 현재 프런트엔드의 Vite 프록시 대상인 **http://localhost:80**에 맞춘 구성입니다. 백엔드를 **8080**으로 실행한다면 프런트엔드 **vite.config.js**의 프록시 대상도 **http://localhost:8080**으로 변경하세요.
- **ddl-auto=update**는 로컬 개발 편의를 위한 예시이며, 운영 환경에서는 마이그레이션 정책과 기존 DB 스키마에 맞춰 설정해야 합니다.
- DB 사용자명과 비밀번호를 README나 Git에 직접 저장하지 마세요.
- **db/seat_payment_test.sql**은 좌석·주문·결제 테스트에 필요한 일부 테이블을 만드는 **테스트용 fixture**입니다. 전체 서비스 초기화 SQL이 아니며, 공유/운영 DB에서 실행하지 마세요.

### 4. 백엔드 실행

Windows:

~~~powershell
.\gradlew.bat bootRun
~~~

macOS / Linux:

~~~bash
./gradlew bootRun
~~~

테스트 실행:

~~~bash
./gradlew test
~~~

### 5. 프런트엔드 실행

별도 터미널에서 프런트엔드 저장소를 실행합니다.

~~~bash
git clone https://github.com/hohun21/sistticket-frontend.git
cd sistticket-frontend
npm install
npm run dev
~~~

개발 서버는 **http://localhost:5173**을 사용합니다. 프런트엔드의 **/api** 요청은 **vite.config.js**에 설정된 백엔드 주소로 전달됩니다.

## 빌드 및 배포 준비

### Backend

~~~bash
./gradlew clean test bootJar
~~~

빌드 결과 JAR는 **build/libs/**에 생성됩니다. 실행 명령은 실제 생성된 JAR 파일명에 맞춰 사용하세요.

~~~bash
java -jar build/libs/생성된-jar-파일명.jar
~~~

### Frontend

~~~bash
npm run build
~~~

정적 배포 파일은 **dist/**에 생성됩니다. 정적 파일 호스팅 또는 웹 서버를 통해 제공하고, API 요청이 백엔드 주소로 전달되도록 운영 환경의 프록시/CORS/인증 쿠키 설정을 맞춰야 합니다.

> 현재 저장소에서 Dockerfile 또는 GitHub Actions 배포 워크플로를 확인하지 못했습니다. 위 내용은 빌드 산출물을 만드는 방법이며, 자동 배포 파이프라인이 이미 구성되어 있다는 의미는 아닙니다.

## 시연 화면

아래 영역에는 **실제 실행 화면 캡처**를 추가하면 좋습니다. 캡처를 **docs/screenshots/**에 저장한 뒤 이미지 경로를 활성화하세요.

<!--
![메인 페이지](docs/screenshots/main-page.png)
![공연 상세](docs/screenshots/concert-detail.png)
![좌석 선택 및 선점](docs/screenshots/seat-selection.png)
![주문 및 모의 결제](docs/screenshots/payment-flow.png)
![관리자 대시보드](docs/screenshots/admin-dashboard.png)
-->

추천 시연 흐름: **공연 선택 → 회차 선택 → 좌석 선점 → 주문 생성 → 모의 결제 처리 → 관리자 예매 확인**

## 현재 범위 및 개선 과제

- 실제 PG사를 통한 결제 승인·취소 연동 및 서명/웹훅 검증
- 전체 개발·운영 환경에 맞춘 DB 초기화 및 마이그레이션 문서화
- 매출 관리 화면의 실제 집계 API 및 UI 연결
- 테스트 데이터와 시연 시나리오를 재현 가능한 형태로 정리
- 운영 배포를 위한 환경 변수 관리, 로그·모니터링, 자동 배포 워크플로 구성

---

**Frontend repository:** [hohun21/sistticket-frontend](https://github.com/hohun21/sistticket-frontend)
