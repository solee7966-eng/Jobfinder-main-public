# 🔍 JobFinder — 채용공고 플랫폼

> 기업과 구직자를 연결하는 통합 채용 플랫폼  
> 기업 담당자의 실제 업무 흐름을 중심으로 설계된 B2B 중심 채용 서비스

[![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Oracle](https://img.shields.io/badge/Oracle-DB-F80000?style=flat-square&logo=oracle)](https://www.oracle.com)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI/CD-D24939?style=flat-square&logo=jenkins)](https://www.jenkins.io)
[![AWS](https://img.shields.io/badge/AWS-EC2-FF9900?style=flat-square&logo=amazonaws)](https://aws.amazon.com)

**개발 기간** | 2026.02.24 ~ 2026.03.31 &nbsp;·&nbsp; **팀 구성** | 4인 팀 프로젝트 &nbsp;·&nbsp; **담당 역할** | 기업 기능 전체, 포인트 결제 시스템, 공고 자동 동기화

🔗 **[배포 사이트 바로가기](http://52.78.3.125/user-service/index)** &nbsp;|&nbsp; 📄 **[API 명세 (Swagger)](http://52.78.3.125/user-service/swagger-ui/index.html#/)** &nbsp;|&nbsp; 🎨 **[Figma 화면설계](https://www.figma.com/make/tgAE5O7dx4tSwYLV9V70V2/JobFinder_%EA%B8%B0%EC%97%85%EB%8C%80%EC%8B%9C%EB%B3%B4%EB%93%9C_%EC%B5%9C%EC%A2%85%EB%B3%B8?t=wGpxEN9KR83A2Pvn-1)**

---

## 📌 프로젝트 개요

JobFinder는 기업 담당자가 **공고 등록 → 지원자 수신 → 제안서 발송 → 합격/불합격 처리**라는 채용 흐름을 하나의 인터페이스에서 끊김 없이 처리할 수 있도록 설계된 채용 플랫폼입니다.

초기 모놀리식 구조로 시작해 **MSA(Microservice Architecture)** 로 전환하였으며, Jenkins + Docker 기반 CI/CD 파이프라인을 구축하여 운영/테스트 환경을 분리했습니다.

---

## 🗂️ 서비스 구조 (MSA)

이 레포지토리는 **메인 서비스(User Service, 포트 8001)** 입니다.  
구직자·기업 회원 기능과 기업 전용 핵심 기능(공고·지원자·제안서·결제)을 담당합니다.

| 서비스 | 포트 | 레포지토리 | 설명 |
|--------|------|-----------|------|
| **Main Service** | 8001 | ⬅️ 현재 레포 | 회원, 공고, 지원자, 제안서, 결제 |
| **Board Service** | 8002 | [Jobfinder-board-public](https://github.com/solee7966-eng/Jobfinder-board-public) | 커뮤니티 게시판 |
| **API Gateway** | 8000 | [Jobfinder-gateway-public](https://github.com/solee7966-eng/Jobfinder-gateway-public) | 단일 진입점, 서비스 라우팅 |
| **Discovery** | 8761 | [Jobfinder-discovery-public](https://github.com/solee7966-eng/Jobfinder-discovery-public) | Eureka 서비스 레지스트리 |

```
Client (Browser)
    │
    ▼
API Gateway (8000)          ← 단일 진입점, Discovery 연동 라우팅
    │
    ├──▶ Main Service (8001) ← 회원/공고/지원/결제 (현재 레포)
    │         │
    ├──▶ Board Service (8002) ← 커뮤니티
    │
    └──▶ Oracle DB / External API (PortOne, SOLAPI)

Discovery Service (8761)    ← Eureka 서비스 레지스트리
```

---

## ✨ 주요 기능 (내 담당 파트)

### 1. 기업 기능 전체
- **공고 관리**: 채용공고 등록·수정·삭제, 배너 등록
- **지원자 관리**: 지원서 열람, 상태 변경(미열람 → 열람 → 서류합격/불합격 → 최종합격/불합격)
- **인재 검색**: 조건 기반 구직자 탐색 및 제안서 발송
- **제안서 관리**: 제안서 템플릿 작성, 수신자별 발송·열람·응답 현황 추적

### 2. 포인트 결제 시스템 (PortOne 연동)
- PortOne(아임포트) PG사 연동으로 포인트 충전
- **PENDING → VERIFYING → PAID** 상태 흐름으로 외부 결제와 내부 적립 간 데이터 정합성 보장
- 서버에서 PG사 API 직접 재검증 → 프론트 조작·중복 결제 원천 차단
- 결제 완료·포인트 적립·거래내역 저장을 단일 트랜잭션으로 처리

### 3. Spring Scheduler 기반 공고 상태 자동 동기화
- 1분 주기로 전체 공고 상태 자동 갱신 (`@Scheduled`)
- 삭제 → 마감 → 대기 → 진행중 순서로 우선순위 기반 상태 전이
- 마감된 공고가 구직자 화면에 노출되는 문제 원천 방지

---

## 🛠️ 기술 스택

| 분류 | 기술 |
|------|------|
| **Backend** | Java, Spring Boot, Spring Security, Spring Scheduler, MyBatis |
| **Frontend** | HTML5, CSS3, JavaScript |
| **Database** | Oracle |
| **결제** | PortOne (아임포트) V1 |
| **인프라** | AWS EC2, Docker, Docker Compose |
| **CI/CD** | Jenkins, Docker Hub |
| **API 문서** | Swagger |
| **코드 품질** | SonarQube |
| **화면 설계** | Figma |

---

## 🏗️ 설계 결정 & 기술적 고민

### 사용자 구조 분리
구직자와 기업은 보유 데이터와 역할이 근본적으로 달라, 단일 테이블보다 도메인 단위로 분리하는 방향을 선택했습니다. 기업 계정은 공고·지원자·제안서 중심의 독립 도메인으로 설계했습니다.

### 상태값 기반 지원자 관리
단순 상태 UPDATE가 아닌, 변경 시 **DB에서 현재 상태를 재조회**하여 Race Condition을 방지했습니다. 화면에서 전달된 이전 상태와 DB 실제 상태가 다르면 변경을 차단하고, 변경 이력은 별도 테이블로 저장해 감사 추적이 가능하도록 구성했습니다.

### 결제 멱등성 처리
PG사와 내부 DB는 별개 시스템이기 때문에 외부 결제 성공 후 내부 예외 발생 시 포인트 미적립 문제가 생길 수 있습니다. `VERIFYING` 단계 도입과 주문번호 기반 재검증 API로 이를 해결했습니다.

### MSA 전환
초기 모놀리식에서 MSA로 전환하며 서비스 독립 배포 구조를 확보했지만, 현재 규모에서는 다소 과한 선택일 수 있다는 트레이드오프도 인지했습니다. **아키텍처 선택은 현재 규모와 팀 역량을 함께 고려해야 한다**는 점을 배웠습니다.

---

## 🔥 트러블슈팅

### 1. 외부 결제 성공 후 포인트 미적립 문제
**상황**: PG사 결제는 완료됐지만 서버 예외로 포인트가 적립되지 않는 데이터 불일치 발생  
**원인**: 외부 결제 성공 신호 수신 후 서버 예외 발생 시 롤백이 PG사에 미적용되는 구조적 한계  
**해결**: `VERIFYING` 상태 단계 추가 + 주문번호 기반 PG사 재검증 API 도입. 조건부 업데이트로 중복 처리 차단  
**결과**: 결제-적립 데이터 불일치 해소, 서버 오류 상황에서도 복구 가능한 구조 확보

### 2. Docker 내부 네트워크 설정 문제
**상황**: EC2 환경에서 컨테이너는 정상 실행되었지만 서비스 간 통신 실패, Eureka 등록 불가  
**원인**: 각 컨테이너가 서로 다른 네트워크에 배치되어 DNS 기반 통신 불가  
**해결**: `docker-compose`로 공통 네트워크 구성 후 모든 서비스를 동일 네트워크에 재배치  
**결과**: Eureka 등록 및 서비스 간 API 통신 정상화

### 3. CI/CD 파이프라인 환경 분리
**상황**: 단일 Jenkins 파이프라인으로 개발 중인 코드가 운영 배포에 영향을 줄 수 있는 구조  
**해결**: `main` 브랜치(운영) / 개인 브랜치(테스트) 파이프라인 분리 + Jenkinsfile 별도 관리  
**결과**: 개발-운영 환경 독립적 운영, 안정적 배포 흐름 확보

---

## 📈 코드 품질 개선 (SonarQube)

SonarQube 정적 분석을 통한 리팩토링 진행

| 항목 | 개선 전 | 개선 후 |
|------|--------|--------|
| Code Smell | 324 | 212 (**약 35% 감소**) |
| 예외 처리 | `System.out` 기반 | `Logger` + 구체적 예외 타입 |
| DTO 구조 | `public` 필드 직접 접근 | `private` + 접근자, 타입 안정성 확보 |
| 중복 코드 | 반복 문자열 리터럴 | 상수 분리 및 공통 메서드 추출 |

---

## 🗄️ DB 설계

- 핵심 도메인(회원·기업·공고·이력서·지원 흐름) 중심 ERD 설계
- 기술스택·자격증 등 다대다 관계는 중간 테이블로 정규화
- 결제·포인트·배너 테이블을 도메인 단위로 분리해 확장성 확보
- 공고 → 지원 → 제안 → 결과로 이어지는 채용 프로세스를 데이터 구조에 자연스럽게 반영

---

## 🚀 로컬 실행 방법

```bash
# 1. 레포지토리 클론
git clone https://github.com/solee7966-eng/Jobfinder-main-public.git
cd Jobfinder-main-public

# 2. 환경변수 설정 (.env 또는 application.yml)
# DB 접속 정보, PortOne API 키, SOLAPI 키 등 설정 필요

# 3. Discovery Service 먼저 실행 (포트 8761)
# → https://github.com/solee7966-eng/Jobfinder-discovery-public

# 4. Gateway Service 실행 (포트 8000)
# → https://github.com/solee7966-eng/Jobfinder-gateway-public

# 5. Main Service 빌드 및 실행
./gradlew build
java -jar build/libs/*.jar

# Docker로 실행하는 경우
docker-compose up --build
```

> ⚠️ 전체 서비스 동작을 위해 Discovery → Gateway → Main(Board) 순서로 실행해야 합니다.

---

## 📁 관련 레포지토리

| 레포 | 역할 |
|------|------|
| [Jobfinder-main-public](https://github.com/solee7966-eng/Jobfinder-main-public) | ⬅️ 현재 (메인 서비스) |
| [Jobfinder-board-public](https://github.com/solee7966-eng/Jobfinder-board-public) | 커뮤니티 게시판 서비스 |
| [Jobfinder-gateway-public](https://github.com/solee7966-eng/Jobfinder-gateway-public) | API Gateway |
| [Jobfinder-discovery-public](https://github.com/solee7966-eng/Jobfinder-discovery-public) | Eureka Discovery 서비스 |
