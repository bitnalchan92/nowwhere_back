# 🚀 Nowwhere Backend 배포 체크리스트

이 가이드를 따라 순서대로 진행하면 **main 브랜치에 푸시할 때마다 자동으로 배포**됩니다.

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
  - SSH (22): 내 IP
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

# 환경변수 설정
sudo nano /etc/environment
```

**아래 내용 추가 (본인의 실제 키로 변경):**
```bash
KAKAO_REST_API_KEY="실제_카카오_API_키"
DATA_GO_API_KEY="실제_공공데이터_API_키"
ALLOWED_ORIGINS="http://localhost:3000"
```

저장: `Ctrl + X` → `Y` → `Enter`

```bash
# 환경변수 적용
source /etc/environment

# 로그 디렉토리 생성
mkdir -p /home/ubuntu/logs

# 확인
echo $KAKAO_REST_API_KEY
```

✅ API 키가 출력되면 성공!

---

### 3단계: GitHub Secrets 설정 (5분)

1. **GitHub 저장소 페이지 접속**
   - https://github.com/bitnalchan92/nowwhere_back

2. **Settings → Secrets and variables → Actions**

3. **New repository secret 클릭하여 2개 추가:**

**Secret 1: EC2_HOST**
- Name: `EC2_HOST`
- Value: `<EC2-Public-IP>` (예: 43.201.123.45)

**Secret 2: EC2_SSH_KEY**
- Name: `EC2_SSH_KEY`
- Value: (로컬 터미널에서 실행)
  ```bash
  cat ~/Downloads/nowwhere-key.pem
  ```
  출력된 전체 내용 복사 (`-----BEGIN` ~ `-----END` 전부)

---

### 4단계: GitHub Actions 워크플로우 푸시 (1분)

```bash
# 로컬 컴퓨터에서 실행
cd /Users/chankim/github/nowwhere_back

git add .github/workflows/deploy.yml
git add DEPLOYMENT.md
git add SETUP_GUIDE.md
git commit -m "Add GitHub Actions CI/CD workflow for automatic deployment"
git push origin main
```

---

### 5단계: 배포 확인 (2분)

1. **GitHub Actions 페이지에서 워크플로우 확인**
   - https://github.com/bitnalchan92/nowwhere_back/actions
   - 최근 실행된 워크플로우 클릭
   - 모든 단계가 ✅ 녹색이면 성공!

2. **API 테스트**
   ```bash
   # 로컬 터미널에서 실행
   curl "http://<EC2-Public-IP>:8080/api/location/addressInfo?latitude=37.5665&longitude=126.9780"
   ```

   응답 예시:
   ```json
   {
     "address": "서울특별시 중구 세종대로 110",
     "roadAddress": "서울특별시 중구 세종대로 110"
   }
   ```

✅ JSON 응답이 나오면 배포 성공!

---

## 🎉 완료!

이제 **main 브랜치에 푸시할 때마다 자동으로 배포**됩니다.

### 다음 작업: Frontend Vercel 배포

1. Frontend `.env` 파일 업데이트:
   ```bash
   NEXT_PUBLIC_SERVER_HOST=http://<EC2-Public-IP>:8080
   ```

2. EC2 환경변수 업데이트 (CORS):
   ```bash
   ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>
   sudo nano /etc/environment
   ```

   `ALLOWED_ORIGINS`에 Vercel 도메인 추가:
   ```bash
   ALLOWED_ORIGINS="https://your-app.vercel.app,http://localhost:3000"
   ```

3. Vercel 배포 진행

---

## 🔧 트러블슈팅

### GitHub Actions 실패 시

```bash
# EC2에서 로그 확인
ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>
tail -f /home/ubuntu/logs/application.log
```

### 8080 포트 접근 안 될 때

```bash
# EC2 보안 그룹 확인
# AWS Console → EC2 → 보안 그룹 → 인바운드 규칙 확인
# 8080 포트가 0.0.0.0/0으로 열려있는지 확인
```

### 환경변수가 적용 안 될 때

```bash
# EC2 재부팅
sudo reboot

# 다시 접속 후 확인
ssh -i nowwhere-key.pem ubuntu@<EC2-Public-IP>
echo $KAKAO_REST_API_KEY
```
