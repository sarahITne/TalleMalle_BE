#!/bin/bash

JAR_NAME="TalleMalle_Backend-0.0.1-SNAPSHOT.jar"
SERVER_USER="test"
SERVER_IP="192.100.1.10"
SERVER_PATH="/home/test"

echo "> 1. 프로젝트 빌드를 시작합니다..."
./gradlew clean build

echo "> 2. 빌드된 jar 파일을 서버로 전송합니다..."
scp build/libs/$JAR_NAME $SERVER_USER@$SERVER_IP:$SERVER_PATH/

echo "> 3. 서버에서 배포 스크립트를 실행합니다..."
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && ./deploy.sh"

echo "> 로컬 배포 자동화 완료! 수고하셨습니다."

# 실행 방법 : 터미널에 ./local_deploy.sh 입력