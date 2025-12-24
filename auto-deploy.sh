#!/bin/bash

# Nowwhere Backend 자동 배포 스크립트 (EC2 내부용)
# Git에서 변경사항을 감지하고 자동으로 빌드 & 배포합니다.

LOG_FILE="/home/ubuntu/logs/auto-deploy.log"
APP_DIR="/home/ubuntu/app/nowwhere_back"
JAR_FILE="$APP_DIR/build/libs/nowwhere_back-0.0.1-SNAPSHOT.jar"
CONFIG_FILE="/home/ubuntu/config/application-prod.properties"

# 로그 함수
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# 설정 파일 확인
if [ ! -f "$CONFIG_FILE" ]; then
    log "❌ 설정 파일을 찾을 수 없습니다 :: $CONFIG_FILE"
    exit 1
fi

cd "$APP_DIR" || exit 1

# Git fetch로 원격 변경사항 확인
git fetch origin main

# 로컬과 원격 비교
LOCAL=$(git rev-parse HEAD)
REMOTE=$(git rev-parse origin/main)

if [ "$LOCAL" = "$REMOTE" ]; then
    # 변경사항 없음 - 로그 남기지 않고 종료
    exit 0
fi

# 변경사항 발견!
log "========================================="
log "새로운 커밋 감지! 배포를 시작합니다."
log "Local:  $LOCAL"
log "Remote: $REMOTE"
log "========================================="

# 1. Git Pull
log "[1/5] 최신 코드 가져오기..."
git pull origin main >> "$LOG_FILE" 2>&1

# 2. Gradle Build
log "[2/5] 애플리케이션 빌드..."
./gradlew clean build -x test >> "$LOG_FILE" 2>&1

if [ ! -f "$JAR_FILE" ]; then
    log "❌ 빌드 실패! JAR 파일을 찾을 수 없습니다."
    exit 1
fi

# 3. 기존 프로세스 종료
log "[3/5] 기존 프로세스 종료..."
PID=$(pgrep -f "nowwhere_back.*jar")
if [ -n "$PID" ]; then
    log "기존 프로세스 종료: PID $PID"
    kill -15 $PID
    sleep 5

    # 강제 종료 확인
    if pgrep -f "nowwhere_back.*jar" > /dev/null; then
        log "⚠️  프로세스가 종료되지 않았습니다. 강제 종료합니다."
        pkill -9 -f "nowwhere_back.*jar"
        sleep 2
    fi
fi

# 4. 로그 디렉토리 확인
log "[4/5] 로그 디렉토리 확인..."
mkdir -p /home/ubuntu/logs

# 5. 애플리케이션 시작
log "[5/5] 애플리케이션 시작..."
nohup java \
    -Dspring.profiles.active=prod \
    -Dspring.config.additional-location=/home/ubuntu/config/ \
    -jar "$JAR_FILE" \
    > /home/ubuntu/logs/application.log 2>&1 &

# 프로세스 확인
sleep 10
NEW_PID=$(pgrep -f "nowwhere_back.*jar")
if [ -n "$NEW_PID" ]; then
    log "========================================="
    log "✅ 배포 완료! PID: $NEW_PID"
    log "로그 확인: tail -f /home/ubuntu/logs/application.log"
    log "========================================="
else
    log "========================================="
    log "❌ 배포 실패! 로그를 확인하세요."
    log "========================================="
    exit 1
fi
