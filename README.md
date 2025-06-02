### 1차 Goal

1. 접속한 위치 정보(좌표) 관리하고 화면에 출력하기
2. 1.에서 구한 좌표 기준하여 근처에 있는 버스정류장 목록 List로 출력하기
3. 2.에서 출력된 리스트 중 특정 버스 정류장 클릭했을때 버스정류장에 도착 예정인 버스 목록을 Detail에 출력하기


### ETC

| 패키지명         | 용도 설명                                    |
| ------------ | ---------------------------------------- |
| `config`     | CORS, Swagger, WebMvc, Security 등 설정 클래스 |
| `controller` | `@RestController`나 `@Controller`를 담는 위치  |
| `service`    | 서비스 로직 (비즈니스 처리 담당)                      |
| `repository` | JPA Repository 또는 외부 API 호출 등 데이터 관련 처리  |
| `domain`     | DTO, Entity, Enum, VO 등 데이터 모델 클래스       |
| `util`       | 공통적으로 쓰이는 유틸성 클래스 (날짜 변환, 공공데이터 파서 등)    |
