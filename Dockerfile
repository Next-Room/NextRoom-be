# syntax=docker/dockerfile:1

# ---- Build stage ----
# gradle 빌드를 이미지 안에서 수행해 CI는 `docker build`만으로 빌드 가능(setup-java/gradle 단계 불필요).
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# 의존성 캐시 최적화: 래퍼/빌드 스크립트 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드 (테스트 제외 — 기존 CI와 동일)
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# 프로필은 컨테이너 실행 시 SPRING_PROFILES_ACTIVE 로 주입 (compose에서 live=prod, dev=dev)
ENV SPRING_PROFILES_ACTIVE=""
# IAP 크레덴셜 등 시크릿 파일은 이미지에 굽지 않고 런타임에 /app/config 로 마운트
ENV IAP_GOOGLE_CREDENTIALS_PATH="/app/config/iap-google-credentials.json"

# build.gradle 에서 plain jar(jar.enabled=false)는 생성되지 않으므로 boot fat jar 만 존재
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

# JAVA_OPTS 로 힙 등 튜닝 주입 가능
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
