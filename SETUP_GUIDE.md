# 🚀 Nowwhere Backend 배포 체크리스트

이 가이드를 따라 순서대로 진행하면 **main 브랜치에 푸시할 때마다 EC2에서 자동으로 배포**됩니다.

**배포 방식**: EC2 내부에서 5분마다 GitHub를 체크하고 자동으로 Pull & 재배포 (안전하고 간단!)

---

## ✅ 단계별 체크리스트

### 1단계: EC2 인스턴스 생성 (20분)

- [ ] AWS Console 접속: https://console.aws.amazon.com/
- [ ] 리전 선택: **서울 (ap-northeast-2)**
- [ ] EC2 → "인스턴스 시작" 클릭
- [ ] 설정:
  - 이름: `nowwhere-backend`
  - AMI: `Ubuntu Server 22.04 LTS`
  - 인스턴스 유형: `t3.micro`
  - 키 페어: 새로 생성 → `nowwhere-key.pem` 다운로드 ⚠️ **안전한 곳에 보관**
- [ ] 보안 그룹 설정:
  - SSH (22): **내 IP** (본인 IP만 허용 - 안전!)
  - HTTP (80): 0.0.0.0/0
  - 사용자 지정 TCP (8080): 0.0.0.0/0
- [ ] 인스턴스 시작
- [ ] **EC2 Public IP 주소 메모** (예: 43.201.xxx.xxx)

---

### 2단계: EC2 접속 및 환경 구성 (15분)

```bash
# 터미널에서 실행
cd ~/Downloads  # .pem 파일이 있는 디렉토리로 이동
chmod 400 nowwhere-key.pem
ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>
```

**EC2 서버 안에서 실행:**

```bash
# 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# Java 17 설치
sudo apt install openjdk-17-jdk -y
java -version

# Git 설치
sudo apt install git -y

# 설정 디렉토리 생성
mkdir -p /home/ubuntu/config

# Spring Boot 설정 파일 생성
nano /home/ubuntu/config/application-prod.properties
```

**아래 내용 추가 (본인의 실제 키로 변경):**
```properties
# API Keys
api.key.kakao=실제_카카오_API_키
api.key.datago=실제_공공데이터_API_키

# CORS
cors.allowed-origins=http://localhost:3000

# Logging
logging.level.root=INFO
logging.level.com.nowwhere.nowwhere_back=INFO
logging.file.name=/home/ubuntu/logs/nowwhere-back.log
logging.file.max-size=10MB
logging.file.max-history=30
```

⚠️ **참고**: `cors.allowed-origins`는 초기에는 `http://localhost:3000`으로 설정합니다.
Frontend Vercel 배포 후, Vercel URL을 추가해야 합니다:
```properties
cors.allowed-origins=https://your-app.vercel.app,http://localhost:3000
```

저장: `Ctrl + X` → `Y` → `Enter`

```bash
# 설정 파일 권한 설정 (보안)
chmod 600 /home/ubuntu/config/application-prod.properties

# 설정 파일 확인
cat /home/ubuntu/config/application-prod.properties
```

✅ API 키가 보이면 성공!

```bash

# 로그 디렉토리 생성
mkdir -p /home/ubuntu/logs

# 작업 디렉토리 생성
mkdir -p /home/ubuntu/app
cd /home/ubuntu/app

# Git Clone
git clone https://github.com/bitnalchan92/nowwhere_back.git
cd nowwhere_back

# 실행 권한 부여
chmod +x gradlew
chmod +x deploy.sh
chmod +x auto-deploy.sh
```

---

### 3단계: 초기 배포 (10분)

**EC2에서 실행:**

```bash
cd /home/ubuntu/app/nowwhere_back

# 첫 배포 실행
./deploy.sh
```

배포 스크립트가 자동으로:
1. Git pull
2. Gradle 빌드
3. 기존 프로세스 종료
4. 새 애플리케이션 시작

**로그 확인:**
```bash
# 애플리케이션 로그 확인
tail -f /home/ubuntu/logs/application.log

# Ctrl + C로 종료
```

✅ 로그에 "Started NowwhereBackApplication" 메시지가 보이면 성공!

---

### 4단계: API 테스트 (2분)

**로컬 터미널에서 실행:**

```bash
# EC2 Public IP로 테스트 (본인 IP로 변경)
curl "http://<EC2-Public-IP>:8080/api/location/addressInfo?latitude=37.5665&longitude=126.9780"
```

**응답 예시:**
```json
{
  "address": "서울특별시 중구 세종대로 110",
  "roadAddress": "서울특별시 중구 세종대로 110"
}
```

✅ JSON 응답이 나오면 배포 성공!

---

### 5단계: 자동 배포 설정 (5분)

**main 브랜치에 푸시할 때마다 자동 배포**되도록 Cron Job을 설정합니다.

**EC2에서 실행:**

```bash
# Crontab 편집
crontab -e

# 처음 실행 시 에디터 선택 (nano 추천 - 1번 선택)
```

**아래 내용 추가:**
```bash
# Nowwhere Backend 자동 배포 (5분마다 GitHub 체크)
*/5 * * * * /home/ubuntu/app/nowwhere_back/auto-deploy.sh
```

저장: `Ctrl + X` → `Y` → `Enter`

**Cron Job 확인:**
```bash
# 등록된 cron job 확인
crontab -l

# 자동 배포 로그 확인 (5분 후)
tail -f /home/ubuntu/logs/auto-deploy.log
```

---

### 6단계: 자동 배포 테스트 (5분)

**로컬 컴퓨터에서 실행:**

```bash
cd /Users/chankim/github/nowwhere_back

# 테스트용 커밋 생성
echo "# Auto Deploy Test" >> README.md
git add README.md
git commit -m "test: verify auto deployment"
git push origin main
```

**5분 후 EC2에서 확인:**

```bash
ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>

# 자동 배포 로그 확인
tail -20 /home/ubuntu/logs/auto-deploy.log
```

✅ "새로운 커밋 감지! 배포를 시작합니다" 메시지가 보이면 성공!

---

## 🎉 완료!

이제 **main 브랜치에 푸시하면 5분 이내에 자동으로 배포**됩니다!

### 작동 방식

1. 코드를 `main` 브랜치에 push
2. EC2의 Cron Job이 5분마다 GitHub 체크
3. 변경사항 발견 시 자동으로:
   - Git pull
   - Gradle 빌드
   - 기존 앱 종료
   - 새 버전 시작

---

## 🔧 관리 명령어

### 로그 확인

```bash
# 애플리케이션 로그
tail -f /home/ubuntu/logs/application.log

# 자동 배포 로그
tail -f /home/ubuntu/logs/auto-deploy.log

# 최근 배포 내역 확인
tail -50 /home/ubuntu/logs/auto-deploy.log
```

### 수동 배포

```bash
cd /home/ubuntu/app/nowwhere_back
./deploy.sh
```

### 애플리케이션 상태 확인

```bash
# 실행 중인 프로세스 확인
pgrep -f "nowwhere_back.*jar"

# 또는
ps aux | grep nowwhere_back
```

### 애플리케이션 재시작

```bash
# 프로세스 종료
pkill -f "nowwhere_back.*jar"

# 수동 배포
cd /home/ubuntu/app/nowwhere_back
./deploy.sh
```

---

## 🔧 트러블슈팅

### 자동 배포가 안 되는 경우

```bash
# 1. Cron job 확인
crontab -l

# 2. 스크립트 실행 권한 확인
ls -la /home/ubuntu/app/nowwhere_back/auto-deploy.sh

# 3. 수동으로 스크립트 실행해보기
cd /home/ubuntu/app/nowwhere_back
./auto-deploy.sh

# 4. Cron 로그 확인
grep CRON /var/log/syslog | tail -20
```

### 환경변수가 적용 안 될 때

```bash
# EC2 재부팅
sudo reboot

# 다시 접속 후 확인
ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>
echo $KAKAO_REST_API_KEY
```

### 8080 포트 접근 안 될 때

```bash
# 프로세스 확인
pgrep -f "nowwhere_back.*jar"

# 로그 확인
tail -f /home/ubuntu/logs/application.log

# EC2 보안 그룹 확인 (AWS Console)
# 8080 포트가 0.0.0.0/0으로 열려있는지 확인
```

---

## 다음 단계: Frontend Vercel 배포

1. Frontend `.env` 파일 업데이트:
   ```bash
   NEXT_PUBLIC_SERVER_HOST=http://<EC2-Public-IP>:8080
   ```

2. EC2 설정 파일 업데이트 (CORS):
   ```bash
   ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>
   nano /home/ubuntu/config/application-prod.properties
   ```

   `cors.allowed-origins`에 Vercel 도메인 추가:
   ```properties
   cors.allowed-origins=https://your-app.vercel.app,http://localhost:3000
   ```

   저장 후 애플리케이션 재시작:
   ```bash
   # 애플리케이션 재시작
   pkill -f "nowwhere_back.*jar"
   cd /home/ubuntu/app/nowwhere_back
   ./deploy.sh
   ```

3. Vercel 배포 진행
