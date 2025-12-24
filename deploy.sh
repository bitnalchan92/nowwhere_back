#!/bin/bash

# Nowwhere Backend 배포 스크립트

echo "========================================="
echo "Nowwhere Backend 배포 시작"
echo "========================================="

# 0. 설정 파일 확인
CONFIG_FILE="/home/ubuntu/config/application-prod.properties"
if [ ! -f "$CONFIG_FILE" ]; then
    echo "❌ 설정 파일을 찾을 수 없습니다: $CONFIG_FILE"
    echo "SETUP_GUIDE.md를 참고하여 설정 파일을 생성하세요."
    exit 1
fi
echo "[ 0 / 5 ] 설정 파일 확인 완료"

# 1. Git Pull
echo "[ 1 / 5 ] 최신 코드 가져오기..."
git pull origin main

# 2. Gradle Build
echo "[ 2 / 5 ] 애플리케이션 빌드..."
./gradlew clean build -x test

# 3. 기존 프로세스 종료
echo "[ 3 / 5 ] 기존 프로세스 종료..."
PID=$(pgrep -f "nowwhere_back.*jar")
if [ -n "$PID" ]; then
    echo "기존 프로세스 종료: PID $PID"
    kill -15 $PID
    sleep 5
fi

# 4. 로그 디렉토리 생성
echo "[ 4 / 5 ] 로그 디렉토리 생성..."
mkdir -p /home/ubuntu/logs

# 5. 애플리케이션 시작
echo "[ 5 / 5 ] 애플리케이션 시작..."
nohup java \
    -Dspring.profiles.active=prod \
    -Dspring.config.additional-location=/home/ubuntu/config/ \
    -jar build/libs/nowwhere_back-0.0.1-SNAPSHOT.jar \
    > /home/ubuntu/logs/application.log 2>&1 &

# 프로세스 확인
sleep 5
NEW_PID=$(pgrep -f "nowwhere_back.*jar")
if [ -n "$NEW_PID" ]; then
    echo "========================================="
    echo "배포 완료! PID: $NEW_PID"
    echo "로그 확인: tail -f /home/ubuntu/logs/application.log"
    echo "========================================="
else
    echo "========================================="
    echo "배포 실패! 로그를 확인하세요."
    echo "========================================="
    exit 1
fi
