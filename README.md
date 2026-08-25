## 2. TourAPI 연동 구현 범위

TourAPI의 모든 기능을 한 번에 연동하지 않고, 프론트엔드에서 필요한 기능부터 단계적으로 구현합니다.

### 2.1 1차 구현 범위

- TourAPI 연결 설정
- 관광지 목록 조회
- 키워드 기반 관광지 검색
- 관광지 상세정보 조회
- 지역 및 콘텐츠 유형 필터링
- TourAPI 응답을 WayLog 전용 응답 형식으로 변환
- 외부 API 오류 및 타임아웃 처리

### 2.2 추후 구현 범위

- 축제 및 행사 정보
- 문화시설 정보
- 여행코스 정보
- 레포츠 정보
- 숙박 정보
- 쇼핑 정보
- 음식점 정보
- 관광지 상세 이미지
- 연관 관광지 추천
- 응답 캐싱
- 필요한 관광 데이터의 DB 저장

첫 번째 TourAPI 연동에서는 관광지 목록, 검색, 상세 조회 기능을 우선 구현합니다.

---

## 3. 프론트엔드 연동 API

프론트엔드는 한국관광공사 TourAPI를 직접 호출하지 않습니다.

TourAPI 서비스 키 보호와 응답 형식 통일을 위해 다음 구조로 요청을 처리합니다.

```text
WayLog React 프론트엔드
        ↓
WayLog 백엔드 API
        ↓
한국관광공사 TourAPI
```

### 3.1 관광지 목록 조회

```http
GET /api/tours
```

#### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `page` | Integer | 선택 | 페이지 번호, 기본값 `1` |
| `size` | Integer | 선택 | 페이지당 조회 개수, 기본값 `12` |
| `areaCode` | Integer | 선택 | 지역 코드 |
| `sigunguCode` | Integer | 선택 | 시군구 코드 |
| `contentTypeId` | Integer | 선택 | 관광 콘텐츠 유형 |
| `arrange` | String | 선택 | 정렬 방식 |

#### 요청 예시

```http
GET /api/tours?page=1&size=12&areaCode=1&contentTypeId=12
```

### 3.2 관광지 키워드 검색

```http
GET /api/tours/search
```

#### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `keyword` | String | 필수 | 검색어 |
| `page` | Integer | 선택 | 페이지 번호, 기본값 `1` |
| `size` | Integer | 선택 | 페이지당 조회 개수, 기본값 `12` |
| `areaCode` | Integer | 선택 | 지역 코드 |
| `contentTypeId` | Integer | 선택 | 관광 콘텐츠 유형 |

#### 요청 예시

```http
GET /api/tours/search?keyword=경복궁&page=1&size=12
```

### 3.3 관광지 상세 조회

```http
GET /api/tours/{contentId}
```

`contentId`는 TourAPI에서 관광 콘텐츠를 식별하는 고유 ID입니다.

#### 요청 예시

```http
GET /api/tours/126508
```

상세 조회에서는 다음 정보를 제공합니다.

- 관광지명
- 주소
- 대표 이미지
- 상세 설명
- 위치 정보
- 전화번호
- 홈페이지
- 이용시간
- 휴무일
- 주차 정보
- 콘텐츠 유형별 추가 정보

일부 정보는 콘텐츠 유형에 따라 제공되지 않을 수 있습니다.

### 3.4 지역 코드 조회

```http
GET /api/tours/areas
```

#### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `areaCode` | Integer | 선택 | 입력 시 해당 지역의 시군구 코드 조회 |

#### 전체 광역 지역 조회

```http
GET /api/tours/areas
```

#### 특정 지역의 시군구 조회

```http
GET /api/tours/areas?areaCode=1
```

### 3.5 콘텐츠 유형 코드

| 콘텐츠 | `contentTypeId` |
| --- | --- |
| 관광지 | `12` |
| 문화시설 | `14` |
| 축제·공연·행사 | `15` |
| 여행코스 | `25` |
| 레포츠 | `28` |
| 숙박 | `32` |
| 쇼핑 | `38` |
| 음식점 | `39` |

콘텐츠 유형 코드는 TourAPI의 국문 관광정보 서비스 기준으로 관리합니다.

### 3.6 목록 응답 예시

```json
{
  "items": [
    {
      "id": "126508",
      "contentId": "126508",
      "contentTypeId": "12",
      "title": "경복궁",
      "address": "서울특별시 종로구 사직로 161",
      "image": "https://example.com/image.jpg",
      "thumbnail": "https://example.com/thumbnail.jpg",
      "category": "관광지",
      "areaCode": "1",
      "sigunguCode": "23",
      "latitude": 37.5788222356,
      "longitude": 126.9770170625
    }
  ],
  "page": 1,
  "size": 12,
  "totalCount": 1
}
```

TourAPI의 원본 응답은 프론트엔드에 그대로 전달하지 않습니다. 백엔드에서 WayLog 화면에 필요한 형태로 변환한 뒤 반환합니다.

---

## 4. 백엔드 패키지 구조

Spring Boot 기준으로 TourAPI 연동 코드는 다음과 같이 구성합니다.

```text
src/main/java/com/waylog/tour/
├── controller/
│   └── TourController.java
├── service/
│   └── TourService.java
├── client/
│   └── TourApiClient.java
├── dto/
│   ├── request/
│   │   └── TourSearchRequest.java
│   ├── response/
│   │   ├── TourListResponse.java
│   │   ├── TourSummaryResponse.java
│   │   └── TourDetailResponse.java
│   └── external/
│       └── TourApiResponse.java
├── mapper/
│   └── TourMapper.java
├── config/
│   └── TourApiProperties.java
└── exception/
    ├── TourApiException.java
    └── TourExceptionHandler.java
```

### 4.1 Controller

`TourController`는 프론트엔드의 HTTP 요청을 받습니다.

주요 역할은 다음과 같습니다.

- 요청 URL과 HTTP Method 정의
- Query Parameter와 Path Variable 검증
- `TourService` 호출
- WayLog 표준 응답 반환

Controller에서는 TourAPI를 직접 호출하거나 복잡한 데이터 변환을 수행하지 않습니다.

### 4.2 Service

`TourService`는 TourAPI 연동의 비즈니스 흐름을 관리합니다.

주요 역할은 다음과 같습니다.

- 관광지 목록 조회
- 키워드 검색
- 관광지 상세 조회
- 지역 및 콘텐츠 유형 필터 처리
- 필요한 TourAPI 호출 조합
- 외부 API 응답을 Mapper에 전달
- 조회 결과가 없는 경우 처리

상세 화면에 여러 TourAPI 호출이 필요한 경우 Service에서 결과를 조합합니다.

### 4.3 Client

`TourApiClient`는 한국관광공사 TourAPI와 직접 통신합니다.

주요 역할은 다음과 같습니다.

- TourAPI 요청 URL 생성
- 공통 파라미터 설정
- 서비스 키 전달
- 외부 API 호출
- 응답 역직렬화
- 연결 실패 및 타임아웃 처리

공통 요청 파라미터는 다음과 같습니다.

```text
serviceKey
MobileOS
MobileApp
_type
pageNo
numOfRows
```

외부 API 호출 코드는 Service나 Controller에 직접 작성하지 않고 Client에서 관리합니다.

### 4.4 DTO

DTO는 외부 TourAPI 응답과 WayLog 프론트엔드 응답을 분리하는 데 사용합니다.

#### 외부 API DTO

```text
dto/external/TourApiResponse.java
```

TourAPI에서 전달되는 원본 JSON 구조를 역직렬화합니다.

#### 프론트엔드 응답 DTO

```text
dto/response/TourSummaryResponse.java
dto/response/TourDetailResponse.java
dto/response/TourListResponse.java
```

WayLog 프론트엔드에서 사용할 데이터만 선별하여 반환합니다.

외부 API DTO를 Controller 응답으로 직접 사용하지 않습니다. 이를 통해 TourAPI 응답 구조가 변경되더라도 프론트엔드에 미치는 영향을 줄일 수 있습니다.

### 4.5 Mapper

`TourMapper`는 TourAPI 원본 데이터를 WayLog 응답 DTO로 변환합니다.

| TourAPI 필드 | WayLog 필드 |
| --- | --- |
| `contentid` | `contentId`, `id` |
| `contenttypeid` | `contentTypeId` |
| `title` | `title` |
| `addr1`, `addr2` | `address` |
| `firstimage` | `image` |
| `firstimage2` | `thumbnail` |
| `areacode` | `areaCode` |
| `sigungucode` | `sigunguCode` |
| `mapy` | `latitude` |
| `mapx` | `longitude` |
| `overview` | `description` |

데이터가 없는 필드는 빈 문자열보다 `null`을 반환하는 것을 기본 원칙으로 합니다.

### 4.6 Config

`TourApiProperties`는 TourAPI 연결에 필요한 설정을 관리합니다.

관리 대상은 다음과 같습니다.

- TourAPI Base URL
- 서비스 키
- `MobileOS`
- `MobileApp`
- 연결 타임아웃
- 응답 타임아웃

실제 서비스 키는 소스 코드와 Git 저장소에 포함하지 않고 환경변수로 주입합니다.

### 4.7 Exception

TourAPI 연결 과정에서 발생할 수 있는 오류를 WayLog 표준 오류 응답으로 변환합니다.

처리 대상은 다음과 같습니다.

- TourAPI 연결 실패
- 응답 타임아웃
- 잘못된 서비스 키
- TourAPI 오류 코드 반환
- JSON 응답 변환 실패
- 존재하지 않는 `contentId`
- 잘못된 요청 파라미터

오류 응답 예시는 다음과 같습니다.

```json
{
  "code": "TOUR_API_ERROR",
  "message": "관광 정보를 불러오지 못했습니다.",
  "timestamp": "2026-08-25T18:00:00"
}
```

내부 오류 내용이나 TourAPI 서비스 키가 프론트엔드 응답 또는 로그에 노출되지 않도록 주의합니다.
