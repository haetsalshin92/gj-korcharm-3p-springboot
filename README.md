# AI Bot 프로젝트

## 프로젝트 개요
이 프로젝트는 GitHub 웹훅을 통해 Pull Request 이벤트를 받아 AI 기반 코드 리뷰를 자동으로 수행하는 Spring Boot 애플리케이션입니다.

## 기술 스택
- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- Spring Data MongoDB
- H2 Database
- Lombok
- Docker
- GitHub Webhook 연동
- Google Gemini AI

## 시스템 요구사항
- JDK 17 이상
- Gradle
- Docker (선택사항)
- GitHub 계정 및 저장소 접근 권한

## 주요 기능
- GitHub Webhook 이벤트 처리
- Gemini AI를 활용한 Pull Request 자동 코드 리뷰
- REST API 엔드포인트 제공

## API 엔드포인트
### 웹훅 엔드포인트
- URL: POST /webhook
- 기능: GitHub Pull Request 이벤트 처리
- 헤더 요구사항: X-GitHub-Event
- 응답: 리뷰 처리 상태 메시지

## 빌드 및 실행 방법

### Gradle을 사용한 빌드
bash ./gradlew build

### Docker를 사용한 실행
1. Docker 이미지 빌드
   bash docker build -t aibot .
2. Docker 컨테이너 실행
   bash docker run -p 8080:8080 aibot

## 환경 설정
- 서버 포트: 8080
- 타임존: Asia/Seoul

## 의존성
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Data MongoDB
- H2 Database
- Lombok
- JUnit (테스트용)

## 웹훅 설정 방법
1. GitHub 저장소 설정에서 Webhooks 메뉴로 이동
2. 새 웹훅 추가
3. Payload URL 설정: `http://your-domain/webhook`
4. Content type을 `application/json`으로 설정
5. Pull Request 이벤트만 선택하여 웹훅 활성화

## 주의사항
- 보안을 위해 실제 운영 환경에서는 적절한 인증 및 보안 설정이 필요합니다.
- GitHub 토큰과 AI API 키는 환경 변수로 관리하는 것을 권장합니다.

## 라이선스
이 프로젝트는 오픈 소스로 제공됩니다.
