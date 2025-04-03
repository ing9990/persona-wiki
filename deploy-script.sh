#!/bin/bash

# -------------------------------------
# 환경설정
# -------------------------------------
APP_NAME="persona-wiki"
JAR_PATH="/root/persona-wiki/web-0.0.1-SNAPSHOT.jar"
NGINX_CONF="/etc/nginx/sites-available/default"
LOG_DIR="/root/persona-wiki/deploy-logs/"

# t3.small(2GB RAM)에 최적화된 JVM 메모리 설정
# 최대 메모리는 사용 가능한 RAM의 약 40%로 설정
JVM_OPTS="-Xms256m -Xmx768m -XX:MetaspaceSize=96m -XX:MaxMetaspaceSize=192m -XX:+UseG1GC -XX:+DisableExplicitGC -Djava.security.egd=file:/dev/./urandom"

# 로그 파일 생성
DEPLOY_LOG="$LOG_DIR/deploy-$(date +%Y%m%d-%H%M%S).log"i
touch $DEPLOY_LOG

# 로그 기록 함수
log() {
  local message="[$(date +%Y-%m-%d\ %H:%M:%S)] $1"
  echo "$message" | tee -a $DEPLOY_LOG
}

# -------------------------------------
# 시스템 상태 확인
# -------------------------------------
log "📊 배포 전 시스템 상태 확인"
log "메모리 상태:"
free -m | tee -a $DEPLOY_LOG
log "디스크 상태:"
df -h / | tee -a $DEPLOY_LOG
log "실행 중인 Java 프로세스:"
ps -ef | grep java | grep -v grep | tee -a $DEPLOY_LOG

# -------------------------------------
# Nginx 설정에서 현재 포트 확인
# -------------------------------------
log "🔍 현재 사용 중인 Nginx 포트 파악 중..."

# 현재 Nginx 설정에서 proxy_pass에 설정된 포트 추출
CURRENT_PORT=$(grep -oP 'proxy_pass http://localhost:\K[0-9]+' "$NGINX_CONF")

if [ -z "$CURRENT_PORT" ]; then
  log "❌ 현재 Nginx 설정에서 포트를 찾을 수 없습니다. 배포 중단."
  exit 1
fi

# 현재 사용 중인 포트가 8080이면, 다음 배포는 8081로 (반대도 마찬가지)
if [ "$CURRENT_PORT" == "8080" ]; then
  IDLE_PORT=8081
else
  IDLE_PORT=8080
fi

log "✅ 현재 서비스 포트: $CURRENT_PORT"
log "🔄 새 버전 배포 포트: $IDLE_PORT"

# -------------------------------------
# 메모리 확보 및 시스템 최적화
# -------------------------------------
log "🧹 시스템 캐시 정리 중..."
sync
echo 1 > /proc/sys/vm/drop_caches
log "✅ 시스템 캐시 정리 완료"

# -------------------------------------
# 기존 포트에서 실행 중인 프로세스 종료
# -------------------------------------
log "🛑 포트 $IDLE_PORT 에서 실행 중인 애플리케이션 종료 중..."
PID=$(lsof -t -i:$IDLE_PORT)
if [ -n "$PID" ]; then
  log "⏳ 애플리케이션 정상 종료 시도 중 (PID=$PID)..."
  kill -15 $PID

  # 최대 10초 대기하여 정상 종료 확인
  for i in {1..10}; do
    if ! ps -p $PID > /dev/null; then
      log "✅ 애플리케이션 정상 종료 완료"
      break
    fi
    sleep 1
  done

  # 10초 후에도 종료되지 않으면 강제 종료
  if ps -p $PID > /dev/null; then
    log "⚠️ 정상 종료 실패, 강제 종료 진행..."
    kill -9 $PID
    sleep 2
  fi
else
  log "ℹ️ 해당 포트에서 실행 중인 프로세스 없음"
fi

# 종료 후 시스템 상태 확인
log "종료 후 메모리 상태:"
free -m | tee -a $DEPLOY_LOG

# -------------------------------------
# 새 버전 애플리케이션 실행
# -------------------------------------
log "🚀 새 버전 애플리케이션 실행 중 (포트 $IDLE_PORT)..."
nohup java $JVM_OPTS -jar "$JAR_PATH" \
  --spring.profiles.active=prod \
  --server.port="$IDLE_PORT" \
  --logging.file.name="$LOG_DIR/application-$IDLE_PORT.log" \
  > "$LOG_DIR/nohup-$IDLE_PORT.log" 2>&1 &

NEW_PID=$!
log "📝 새 애플리케이션 PID: $NEW_PID"

# JVM이 초기화될 시간을 주기 위해 잠시 대기
log "⏳ 애플리케이션 초기화 대기 중..."
sleep 10

# 프로세스가 여전히 실행 중인지 확인
if ! ps -p $NEW_PID > /dev/null; then
  log "❌ 애플리케이션이 시작 후 비정상 종료됨. 로그 확인:"
  tail -n 50 "$LOG_DIR/nohup-$IDLE_PORT.log" | tee -a $DEPLOY_LOG
  exit 1
fi

# 메모리 사용량 확인
log "애플리케이션 시작 후 메모리 상태:"
free -m | tee -a $DEPLOY_LOG

# -------------------------------------
# 헬스 체크 (최대 60초까지 시도, 체크 간격 5초)
# -------------------------------------
log "⏳ 헬스 체크 대기 중 (최대 60초)..."
for i in {1..12}
do
  log "🔍 헬스 체크 시도 #$i..."
  RESPONSE=$(curl -s http://localhost:$IDLE_PORT/actuator/health)

  if [[ "$RESPONSE" == *'"status":"UP"'* ]]; then
    log "✅ 헬스 체크 통과 (포트 $IDLE_PORT)"
    break
  fi

  # 프로세스가 여전히 실행 중인지 확인
  if ! ps -p $NEW_PID > /dev/null; then
    log "❌ 애플리케이션 프로세스가 헬스 체크 중 비정상 종료됨. 로그 확인:"
    tail -n 50 "$LOG_DIR/nohup-$IDLE_PORT.log" | tee -a $DEPLOY_LOG
    exit 1
  fi

  if [ "$i" -eq 12 ]; then
    log "❌ 60초 내 헬스체크 실패. 배포 중단. 로그 확인:"
    tail -n 50 "$LOG_DIR/nohup-$IDLE_PORT.log" | tee -a $DEPLOY_LOG
    kill -15 $NEW_PID
    exit 1
  fi

  # 5초 간격으로 체크
  sleep 5
done

# -------------------------------------
# Nginx 설정 수정 및 재시작
# -------------------------------------
log "📝 Nginx 설정 변경 중 (proxy_pass 포트 $CURRENT_PORT → $IDLE_PORT)..."
sed -i "s/proxy_pass http:\/\/localhost:$CURRENT_PORT;/proxy_pass http:\/\/localhost:$IDLE_PORT;/" "$NGINX_CONF"

log "🔄 Nginx 설정 검증..."
nginx -t

if [ $? -ne 0 ]; then
  log "❌ Nginx 설정 오류. 이전 설정으로 복원 중..."
  sed -i "s/proxy_pass http:\/\/localhost:$IDLE_PORT;/proxy_pass http:\/\/localhost:$CURRENT_PORT;/" "$NGINX_CONF"
  kill -15 $NEW_PID
  exit 1
fi

log "🔁 Nginx 재시작..."
nginx -s reload

log "⏳ Nginx 재시작 후 5초 대기..."
sleep 5

# -------------------------------------
# 서비스 전환 확인 및 이전 버전 종료
# -------------------------------------
log "🔍 새로운 서비스 상태 확인..."
HEALTH_CHECK=$(curl -s http://localhost:$IDLE_PORT/actuator/health)
if [[ "$HEALTH_CHECK" == *'"status":"UP"'* ]]; then
  log "✅ 새 버전 서비스 정상 작동 중"

  # 이전 포트의 애플리케이션을 안전하게 종료
  log "🛑 이전 포트($CURRENT_PORT)의 애플리케이션 종료 중..."
  OLD_PID=$(lsof -t -i:$CURRENT_PORT)
  if [ -n "$OLD_PID" ]; then
    kill -15 $OLD_PID
    log "✅ 이전 애플리케이션 종료 요청 완료"

    # 메모리 사용량 확인
    sleep 5
    log "이전 애플리케이션 종료 후 메모리 상태:"
    free -m | tee -a $DEPLOY_LOG
  fi

  log "🎉 배포 완료! 현재 서비스 포트는 $IDLE_PORT 입니다."
else
  log "❌ 새 버전 서비스가 정상 작동하지 않음. Nginx를 이전 포트로 복원 중..."
  sed -i "s/proxy_pass http:\/\/localhost:$IDLE_PORT;/proxy_pass http:\/\/localhost:$CURRENT_PORT;/" "$NGINX_CONF"
  nginx -s reload

  kill -15 $NEW_PID
  log "⚠️ 배포 실패! 이전 버전으로 운영 계속"
  exit 1
fi

# -------------------------------------
# 로그 파일 정리 (오래된 로그 파일 삭제)
# -------------------------------------
log "🧹 오래된 로그 파일 정리 중..."
find $LOG_DIR -name "nohup-*.log" -type f -mtime +7 -delete
find $LOG_DIR -name "application-*.log" -type f -mtime +7 -delete
find $LOG_DIR -name "deploy-*.log" -type f -mtime +30 -delete
log "✅ 로그 파일 정리 완료"

log "📊 최종 시스템 상태:"
free -m | tee -a $DEPLOY_LOG
df -h / | tee -a $DEPLOY_LOG
