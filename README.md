### 1차 Goal

1. 접속한 위치 정보(좌표) 관리하고 화면에 출력하기
2. 1.에서 구한 좌표 기준하여 근처에 있는 버스정류장 목록 List로 출력하기
3. 2.에서 출력된 리스트 중 특정 버스 정류장 클릭했을때 버스정류장에 도착 예정인 버스 목록을 Detail에 출력하기


### ETC

```
src/main/java/com/nowwhere/nowwhere_back/
│
├── config         // 설정 관련 클래스 (WebClient 설정 등)
├── controller     // API 요청 처리 (Controller)
├── domain         // 핵심 도메인
│   ├── dto        // 요청/응답 DTO
│   └── model      // Entity 혹은 VO (없다면 생략 가능)
├── repository     // DB 접근 관련
├── service        // 서비스 계층 - 비즈니스 로직 (← 여기에 넣는 걸 추천!)
├── util           // 공용 유틸 클래스
```# Auto Deploy Test
# Auto Deploy Test2
