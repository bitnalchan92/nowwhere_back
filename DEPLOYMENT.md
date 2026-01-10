# Nowwhere Backend 배포 가이드 (AWS EC2)

## 📋 사전 준비

### 1. AWS 계정 생성
- AWS 프리티어 계정 생성
- 신용카드 등록 필요

### 2. API 키 준비
- Kakao REST API Key
- 공공데이터포털 API Key
- 서울실시간지하철인증키 API Key

---

## 🤖 자동 배포 설정

이 프로젝트는 **main 브랜치에 push할 때마다 EC2에서 자동으로 배포**됩니다.

### 배포 방식: EC2 Cron Job (안전하고 간단!)

GitHub Actions 대신 **EC2 내부에서 주기적으로 GitHub를 체크**하는 방식을 사용합니다.

**장점**:
- ✅ EC2 보안 그룹에서 SSH를 "내 IP"로만 제한 가능 (더 안전)
- ✅ GitHub Secrets 설정 불필요
- ✅ 설정이 간단하고 안정적

### 작동 방식

1. **main 브랜치에 코드 push**
2. **EC2의 Cron Job이 5분마다** GitHub 체크
3. 변경사항 발견 시 자동으로:
   - Git pull
   - Gradle 빌드
   - 기존 애플리케이션 종료
   - 새 버전 시작

### 설정 방법

자세한 설정 방법은 `SETUP_GUIDE.md`를 참고하세요.

**핵심 단계**:
1. EC2에서 Git clone
2. `auto-deploy.sh` 스크립트 실행 권한 부여
3. Crontab에 다음 추가:
   ```bash
   */5 * * * * /home/ubuntu/app/nowwhere_back/auto-deploy.sh
   ```

---

## 🚀 AWS EC2 배포 단계

### Step 1: EC2 인스턴스 생성

1. **AWS Console 접속**
   - https://console.aws.amazon.com/
   - 리전 선택: **서울 (ap-northeast-2)**

2. **EC2 대시보드**
   - 서비스 → EC2 → "인스턴스 시작" 클릭

3. **인스턴스 설정**
   ```
   이름: nowwhere-backend
   AMI: Ubuntu Server 22.04 LTS (64비트)
   인스턴스 유형: t3.micro (프리티어)
   키 페어: 새로 생성 (nowwhere-key.pem 다운로드)
   ```

4. **보안 그룹 설정**
   ```
   규칙 추가:
   - SSH (22): 내 IP
   - HTTP (80): 0.0.0.0/0
   - 사용자 지정 TCP (8080): 0.0.0.0/0
   ```

5. **스토리지 설정**
   ```
   크기: 8GB (프리티어 무료)
   ```

6. **인스턴스 시작**

---

### Step 2: EC2 접속 및 환경 설정

```bash
# 1. SSH 접속
chmod 400 nowwhere-key.pem
ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>

# 2. 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# 3. Java 17 설치
sudo apt install openjdk-17-jdk -y
java -version

# 4. Git 설치
sudo apt install git -y

# 5. 작업 디렉토리 생성
mkdir -p /home/ubuntu/app
cd /home/ubuntu/app

# 6. Git Clone
git clone https://github.com/bitnalchan92/nowwhere_back.git
cd nowwhere_back
```

---

### Step 3: 설정 파일 생성

```bash
# 설정 디렉토리 생성
mkdir -p /home/ubuntu/config

# Spring Boot 설정 파일 생성
nano /home/ubuntu/config/application-prod.properties

# 다음 내용 추가:
api.key.kakao=your_kakao_api_key
api.key.datago=your_datago_api_key
api.key.seoul-subway=your_seoul_subway_api_key
cors.allowed-origins=http://localhost:3000,your_frontend_domain,your_backend_domain
logging.level.root=INFO
logging.level.com.nowwhere.nowwhere_back=INFO
logging.file.name=/home/ubuntu/logs/nowwhere-back.log
logging.file.max-size=10MB
logging.file.max-history=30

# ⚠️ 중요: Vercel 배포 후에는 Vercel URL을 추가해야 합니다:
# cors.allowed-origins=https://your-app.vercel.app,http://localhost:3000

# 파일 권한 설정 (보안)
chmod 600 /home/ubuntu/config/application-prod.properties

# 설정 파일 확인
cat /home/ubuntu/config/application-prod.properties
```

---

### Step 4: 애플리케이션 빌드 및 실행

```bash
# 1. 빌드
./gradlew clean build -x test

# 2. 배포 스크립트 실행 권한 부여
chmod +x deploy.sh

# 3. 배포
./deploy.sh

# 4. 로그 확인
tail -f /home/ubuntu/logs/application.log
```

---

### Step 5: 서비스 등록 (자동 재시작)

```bash
# systemd 서비스 파일 생성
sudo nano /etc/systemd/system/nowwhere-backend.service
```

```ini
[Unit]
Description=Nowwhere Backend Service
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/app/nowwhere_back
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="KAKAO_REST_API_KEY=your_key"
Environment="DATA_GO_API_KEY=your_key"
Environment="ALLOWED_ORIGINS=https://your-app.vercel.app"
ExecStart=/usr/bin/java -jar /home/ubuntu/app/nowwhere_back/build/libs/nowwhere_back-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# 서비스 시작
sudo systemctl daemon-reload
sudo systemctl enable nowwhere-backend
sudo systemctl start nowwhere-backend

# 상태 확인
sudo systemctl status nowwhere-backend
```

---

## 🔍 배포 확인

```bash
# 서버 상태 확인
curl http://localhost:8080/api/location/addressInfo?latitude=37.5665&longitude=126.9780

# 외부 접속 확인
curl http://<EC2-Public-IP>:8080/api/location/addressInfo?latitude=37.5665&longitude=126.9780
```

---

## 📝 재배포 방법

```bash
cd /home/ubuntu/app/nowwhere_back
./deploy.sh
```

---

## 🔧 트러블슈팅

### 포트 8080이 열리지 않는 경우
```bash
# 방화벽 확인
sudo ufw status

# 8080 포트 허용
sudo ufw allow 8080
```

### 메모리 부족
```bash
# 스왑 메모리 추가
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### 로그 확인
```bash
# 애플리케이션 로그
tail -f /home/ubuntu/logs/application.log

# 시스템 로그
sudo journalctl -u nowwhere-backend -f
```

---

## 💰 비용 예상

- **프리티어 (1년)**: 무료
- **프리티어 이후**: 월 약 $10~15
  - t3.micro: ~$8
  - 트래픽: ~$2
  - EBS 스토리지: ~$1

---

## 🎯 다음 단계

1. Elastic IP 할당 (고정 IP)
2. 도메인 연결 (Route 53)
3. HTTPS 설정 (Let's Encrypt)
4. 로드 밸런서 설정 (선택)
