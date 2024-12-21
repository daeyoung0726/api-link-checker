# API Link Checker

[한국어](#한국어) | [English](#english)

---

## 🚨 Important Notice

`0.0.5` 이전 버전에서 오류가 발생할 수 있습니다.  
👉 **최신 버전(0.0.5 이상)**을 사용해 주세요.  
Critical issues have been identified in versions earlier than `0.0.5`.  
👉 Please use **version 0.0.5 or later**.

## Demo Video



https://github.com/user-attachments/assets/4b7d2b1d-1389-47fd-9f26-bebcc00dc1d1





## 한국어

---

### 1. 이 프로젝트는 무엇인가요?

**API Link Checker**는 사용자가 설정한 RESTful API의 연결 여부를 확인하고 관리할 수 있는 라이브러리입니다.  
Swagger와 연동하면 특정 RESTful API를 Swagger 문서의 설명으로 바로 이동할 수 있는 기능도 제공합니다.  
RESTful API 설정 데이터를 로컬 파일 시스템에 JSON 형식으로 저장하여 영구적으로 관리할 수 있습니다.

---

### 2. 주요 기능

- **RESTful API 연결 여부 체크**: API의 연결 상태를 UI에서 확인 및 관리 가능.
- **Swagger 연동**: 특정 API를 Swagger 문서로 바로 이동할 수 있는 버튼 제공.
- **데이터 영구 저장**: JSON 형식으로 로컬 파일 시스템에 저장하여 데이터베이스 없이도 관리 가능.
- **환경별 파일 경로 지원**: Mac, Windows, Linux 등 다양한 운영 체제에서 파일 경로를 유연하게 설정 가능.
- **간단한 UI 제공**: `http(s)://{domain}:{port}/api-checker/index.html`에서 API 상태 확인 및 변경 가능.

---

### 3. 설치 방법

#### Maven 설정

```xml
<dependency>
    <groupId>io.github.daeyoung0726</groupId>
    <artifactId>api-link-checker</artifactId>
    <version>{version}</version>
</dependency>
```

#### Gradle 설정

```xml
implementation 'io.github.daeyoung0726:api-link-checker:{version}'
```

`ex) implementation 'io.github.daeyoung0726:api-link-checker:0.0.5'`

### 4. 사용법

#### 1. 애플리케이션에서 활성화하기

@EnableApiLinkChecker 애너테이션으로 라이브러리를 활성화합니다.

``` java
@SpringBootApplication
@EnableApiLinkChecker
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### 2. API 그룹 정의하기

@ApiGroup을 사용하여 API 그룹을 정의합니다.

``` java
@ApiGroup(value = "사용자 API")
@RestController
@RequestMapping("/users")
public class UserController {

    @TrackApi(description = "사용자 정보 가져오기")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        // 사용자 정보 조회 로직
    }

    @TrackApi(description = "사용자 등록하기")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        // 사용자 등록 로직
    }
}
```

#### 3. application.yml 설정

``` xml
api:
  checker:
    storage:
      filepath: ${user.home}/{custom-file-name}.json
```

#### 4. application.properties 설정

``` xml
api.checker.storage.filepath=${user.home}/{custom-file-name}.json
```

`ex) ${user.home}/api-status.json`

#### 5. 파일 경로 예시 (${user.home})

``` xml
Mac: /Users/{username}/{custom-file-name}.json
Windows: C:\Users\{username}\{custom-file-name}.json
Linux: /home/{username}/{custom-file-name}.json
```

#### 6. UI 접근 방법

`http(s)://{domain}:{port}/api-checker/index.html`로 접속하여 API 상태를 확인하고 관리합니다.

#### 7. Swagger 연동

Swagger와의 연동을 통해 API 설명 문서로 바로 이동할 수 있습니다.

`ex) GET /users (api-link-checker) -> GET /users (swagger-ui)`

#### 8. API 요청 허용 설정

Spring Security를 사용하는 경우, 다음 경로를 허용해야 합니다:

``` java
"/api-checker/**", // UI 리소스 (index.html, script.js, styles.css)
"/v1/api/link/checker/**" // API 요청 (/v1/api/link/checker, /v1/api/link/checker/check, /v1/api/link/checker/swagger-links)
```

---

## English

---

### 1. What is this project?

**API Link Checker** is a library that allows users to monitor and manage the connectivity status of their configured RESTful APIs.  
It also provides integration with Swagger, enabling users to navigate directly to specific API documentation in Swagger.  
The configuration data for RESTful APIs is persistently stored as a JSON file on the local file system.

---

### 2. Key Features

- **Check API Connectivity**: Monitor and manage the connectivity status of APIs via a user interface.
- **Swagger Integration**: Navigate directly to specific API documentation in Swagger through a dedicated button.
- **Persistent Data Storage**: Manage API data without a database by saving it in JSON format on the local file system.
- **Support for Various Environments**: Flexible file path configuration for different operating systems such as Mac, Windows, and Linux.
- **Simple UI**: Access and manage API statuses via `http(s)://{domain}:{port}/api-checker/index.html`.

---

### 3. Installation

#### Maven Configuration

```xml
<dependency>
    <groupId>io.github.daeyoung0726</groupId>
    <artifactId>api-link-checker</artifactId>
    <version>{version}</version>
</dependency>
```

####Gradle Configuration

```xml
implementation 'io.github.daeyoung0726:api-link-checker:{version}'
```
`ex) implementation 'io.github.daeyoung0726:api-link-checker:0.0.5'`

### 4. How to Use

#### 1. Enable in Your Application

Activate the library by adding the @EnableApiLinkChecker annotation.

``` java
@SpringBootApplication
@EnableApiLinkChecker
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### 2. Define API Groups

Define API groups using the @ApiGroup annotation.

``` java
@ApiGroup(value = "User API")
@RestController
@RequestMapping("/users")
public class UserController {

    @TrackApi(description = "Fetch user details")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        // Logic to fetch user details
    }

    @TrackApi(description = "Register a user")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        // Logic to register a user
    }
}
```

#### 3. application.yml Configuration

```xml
api:
  checker:
    storage:
      filepath: ${user.home}/{custom-file-name}.json
```

#### 4. application.properties Configuration

``` xml
api.checker.storage.filepath=${user.home}/{custom-file-name}.json
```

`ex) ${user.home}/api-status.json`

#### 5. File Path Examples (${user.home})

``` xml
Mac: /Users/{username}/{custom-file-name}.json
Windows: C:\Users\{username}\{custom-file-name}.json
Linux: /home/{username}/{custom-file-name}.json
```

#### 6. UI Access

Access the user interface at `http(s)://{domain}:{port}/api-checker/index.html` to monitor and manage API statuses.

#### 7. Swagger Integration

Integrate with Swagger to navigate directly to API documentation.

`ex): GET /users (api-link-checker) → GET /users (swagger-ui)`

#### 8. API Request Allowlist

When using Spring Security, the following paths must be allowed:

``` java
"/api-checker/**", // UI resources (index.html, script.js, styles.css)
"/v1/api/link/checker/**" // API endpoints (link checker, status check, Swagger integration)
```
