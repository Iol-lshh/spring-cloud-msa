# config-server 를 위한, 설정 저장소 입니다.

## 임시 테스트 포트
- service-registry(Eureka): 8761
- config-server: 8888
- api-gateway: 8080

- 로컬 환경 도커에 올려서 테스트 할 것!

```console
java -jar ./service-registry-0.0.1-SNAPSHOT.jar
java -jar ./configserver-0.0.1-SNAPSHOT.jar
java -jar ./assign-service-0.0.1-SNAPSHOT.jar
```

### 확인
- 유레카 앱 확인: 
    - http://localhost:8761/eureka/apps
- 열린 게이트웨이 확인: 
    - http://localhost:55957/actuator/gateway/routes


