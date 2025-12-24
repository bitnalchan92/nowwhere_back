# NowWhere Backend 🚌

> 실시간 위치 기반 버스 정보 제공 API 서버

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📋 프로젝트 개요

NowWhere는 사용자의 현재 위치를 기반으로 주변 버스 정류장과 실시간 버스 도착 정보를 제공하는 웹 서비스의 백엔드 API 서버입니다.

**프로젝트 목표:**
1. 사용자의 위치 정보(위도/경도)를 받아 주소로 변환
2. 현재 위치 기준 근처 버스 정류장 목록 제공
3. 선택한 버스 정류장의 실시간 버스 도착 정보 제공

## 🚀 주요 기능

### 1. 위치 정보 관리
- **좌표 → 주소 변환**: Kakao Maps API를 활용한 역지오코딩
- **세션 관리**: 사용자 위치 정보를 세션에 저장하여 재사용

### 2. 버스 정보 제공
- **근처 버스 정류장 조회**: 서울시 공공 API를 활용하여 반경 내 버스 정류장 검색
- **실시간 버스 도착 정보**: 각 정류장별 버스 도착 예정 시간 및 상태 제공

## 🛠 기술 스택

### Core
- **Java 17**: 최신 LTS 버전
- **Spring Boot 3.5.0**: 프레임워크
- **Gradle**: 빌드 도구

### Libraries
- **Spring WebFlux**: 비동기 HTTP 클라이언트로 외부 API 호출
- **Jackson XML**: XML 형식의 API 응답 파싱
- **Lombok**: 보일러플레이트 코드 최소화

### Infrastructure
- **AWS EC2**: 서버 호스팅
- **Nginx**: 리버스 프록시 및 SSL 터미네이션
- **Let's Encrypt**: 무료 SSL 인증서
- **Cron**: 자동 배포 스케줄러

## 📁 프로젝트 구조

```
src/main/java/com/nowwhere/nowwhere_back/
│
├── config/              # 설정 관련 클래스
│   ├── CorsConfig.java        # CORS 설정
│   └── WebClientConfig.java   # WebClient 빈 설정
│
├── controller/          # API 엔드포인트
│   ├── LocationController.java  # 위치/주소 관련 API
│   └── BusController.java       # 버스 정보 관련 API
│
├── domain/              # 도메인 계층
│   └── dto/             # 데이터 전송 객체
│       ├── location/    # 주소 관련 DTO
│       └── bus/         # 버스 관련 DTO
│
└── service/             # 비즈니스 로직
    ├── LocationService.java  # 위치 서비스
    └── BusService.java       # 버스 정보 서비스
```

## 🔌 API 엔드포인트

### 위치 정보 API

#### 좌표로 주소 조회
```http
GET /api/location/addressInfo?latitude={lat}&longitude={lon}
```

**Response:**
```xml
<AddressDto>
    <addressName>서울 중구 태평로1가 31</addressName>
    <roadAddressName>세종대로</roadAddressName>
    <zoneNo>04524</zoneNo>
</AddressDto>
```

### 버스 정보 API

#### 근처 버스 정류장 조회
```http
GET /api/bus/nearBusStops?latitude={lat}&longitude={lon}
```

**Response:**
```json
[
  {
    "arsId": "12345",
    "stationName": "광화문역",
    "distance": 150.5
  }
]
```

#### 버스 도착 정보 조회
```http
GET /api/bus/arrivalInfo?arsId={arsId}
```

**Response:**
```json
[
  {
    "busNumber": "272",
    "remainTime": "3분 후",
    "remainStation": "2정류장 전"
  }
]
```

## ⚙️ 환경 설정

### 필수 환경 변수

`/home/ubuntu/config/application-prod.properties`:

```properties
# Kakao Maps API
kakao.api.key=YOUR_KAKAO_API_KEY

# Seoul Open API
seoul.api.key=YOUR_SEOUL_API_KEY

# CORS 설정
cors.allowed-origins=https://yourdomain.com,http://localhost:3000
```

### API 키 발급
- **Kakao Maps API**: [Kakao Developers](https://developers.kakao.com/)
- **서울시 공공 API**: [서울 열린데이터 광장](https://data.seoul.go.kr/)

## 🚀 배포

### 로컬 실행

```bash
# 1. 저장소 클론
git clone https://github.com/bitnalchan92/nowwhere_back.git
cd nowwhere_back

# 2. 환경 변수 설정
cp .env.example .env.local
# .env.local 파일 편집하여 API 키 입력

# 3. 빌드 및 실행
./gradlew clean build
./gradlew bootRun
```

서버가 http://localhost:8080 에서 실행됩니다.

### EC2 프로덕션 배포

#### 초기 설정

```bash
# 1. EC2 접속
ssh ubuntu@your-ec2-ip

# 2. 저장소 클론
cd ~/app
git clone https://github.com/bitnalchan92/nowwhere_back.git

# 3. 설정 파일 생성
mkdir -p /home/ubuntu/config
nano /home/ubuntu/config/application-prod.properties
# API 키 및 설정 입력

# 4. 실행 권한 부여
cd nowwhere_back
chmod +x deploy.sh auto-deploy.sh

# 5. 배포
./deploy.sh
```

#### 자동 배포 설정

```bash
# Cron 작업 등록 (1분마다 Git 변경사항 확인 후 자동 배포)
crontab -e

# 다음 라인 추가:
* * * * * /home/ubuntu/app/nowwhere_back/auto-deploy.sh
```

#### SSL/HTTPS 설정 (Nginx + Let's Encrypt)

```bash
# 1. Nginx 설치
sudo apt install nginx certbot python3-certbot-nginx -y

# 2. SSL 인증서 발급
sudo certbot --nginx -d yourdomain.com

# 3. 자동 갱신 설정
sudo certbot renew --dry-run
```

## 📊 모니터링

### 로그 확인

```bash
# 애플리케이션 로그
tail -f /home/ubuntu/logs/application.log

# 자동 배포 로그
tail -f /home/ubuntu/logs/auto-deploy.log

# Nginx 로그
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### 프로세스 상태 확인

```bash
# 실행 중인 Java 프로세스 확인
ps aux | grep nowwhere_back

# 포트 사용 확인
sudo netstat -tuln | grep 8080
```

## 🔄 개발 로드맵

### 현재 완료된 기능 ✅
- [x] 위경도 → 주소 변환
- [x] 근처 버스 정류장 조회
- [x] 실시간 버스 도착 정보
- [x] CORS 설정
- [x] EC2 자동 배포 파이프라인
- [x] HTTPS/SSL 적용

### 향후 개발 예정 🚧

#### Phase 1: 사용자 경험 개선
- [ ] **즐겨찾기 기능**: 자주 이용하는 버스 정류장 저장
- [ ] **알림 기능**: 선택한 버스의 도착 임박 시 알림
- [ ] **최근 검색 기록**: 최근 조회한 정류장 히스토리 저장
- [ ] **버스 경로 표시**: 버스 노선도 및 경로 정보 제공

#### Phase 2: 데이터 확장
- [ ] **전국 버스 정보 지원**: 서울 외 타 지역 버스 정보 추가
- [ ] **지하철 정보 통합**: 지하철 도착 정보 및 환승 정보
- [ ] **교통 혼잡도**: 실시간 도로 교통 상황 정보
- [ ] **날씨 정보**: 현재 위치 기반 날씨 정보 통합

#### Phase 3: 성능 및 안정성
- [ ] **캐싱 전략**: Redis를 활용한 API 응답 캐싱
- [ ] **데이터베이스 도입**: PostgreSQL/MongoDB로 사용자 데이터 영구 저장
- [ ] **로깅 및 모니터링**: ELK Stack 또는 CloudWatch 연동
- [ ] **에러 핸들링 고도화**: 상세한 에러 메시지 및 재시도 로직
- [ ] **API 응답 시간 최적화**: 병렬 처리 및 쿼리 최적화

#### Phase 4: 보안 및 인증
- [ ] **사용자 인증**: JWT 기반 인증 시스템
- [ ] **API Rate Limiting**: 과도한 요청 방지
- [ ] **보안 헤더**: CSRF, XSS 방지 설정 강화

#### Phase 5: 분석 및 인사이트
- [ ] **사용자 통계**: 인기 정류장, 시간대별 이용 패턴 분석
- [ ] **개인화 추천**: 사용 패턴 기반 버스/정류장 추천
- [ ] **관리자 대시보드**: 시스템 통계 및 모니터링 UI

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

This project is licensed under the MIT License.

## 📧 문의

프로젝트 관련 문의사항은 Issues를 통해 남겨주세요.

---

**Live API**: https://wheremybbus.co.kr
**Frontend Repository**: https://github.com/bitnalchan92/nowwhere_front
