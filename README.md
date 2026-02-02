# TimeCapsule

미래에 대한 예측을 기록하고, 지정한 날짜에 공개하는 타임캡슐 서비스입니다.

## 소개

TimeCapsule은 사용자가 미래에 대한 예측을 작성하고, 원하는 날짜에 특정 수신자에게 공개할 수 있는 서비스입니다. 예측 작성 시점의 타임스탬프가 기록되어, "내가 미리 알고 있었다"는 것을 증명할 수 있습니다.

### 주요 기능

- **예측 작성**: 미래에 대한 예측을 제목, 내용과 함께 작성
- **공개일 설정**: 예측이 공개될 날짜를 지정
- **수신자 지정**: 예측을 공개할 대상 지정 (이메일 또는 이름)
- **파일 첨부**: 예측에 관련 파일 첨부 가능
- **공유 링크**: 고유한 접근 코드로 비회원도 예측 확인 가능
- **이메일 알림**: 공개일에 수신자에게 자동 이메일 발송
- **타임스탬프 증명**: 예측 작성 시점이 기록되어 선견지명 증명

## 기술 스택

### Backend
- Java 21
- Spring Boot 3.2.2
- Spring Security + JWT
- Spring Data JPA
- H2 Database (개발용)
- Spring Mail

### Frontend
- React 18
- Vite
- React Router v6
- Axios
- Tailwind CSS v4

## 시작하기

### 사전 요구사항

- Java 21 이상
- Node.js 18 이상
- npm 9 이상

### 환경 설정

1. 프로젝트 클론
```bash
git clone https://github.com/gemdoq/time-capsule.git
cd time-capsule
```

2. 환경 변수 설정
```bash
# 프로젝트 루트에 .env 파일 생성
cat > .env << EOF
# Gmail SMTP (앱 비밀번호 사용)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# JWT Secret (32자 이상)
JWT_SECRET=your-super-secret-key-must-be-at-least-32-characters

# 프론트엔드 URL
FRONTEND_BASE_URL=http://localhost:5173
EOF
```

3. 프론트엔드 환경 변수 설정
```bash
# frontend/.env 파일 생성
echo "VITE_API_URL=http://localhost:8080/api/v1" > frontend/.env
```

### 백엔드 실행

```bash
# 프로젝트 루트에서
./gradlew bootRun
```

백엔드 서버가 `http://localhost:8080`에서 실행됩니다.

### 프론트엔드 실행

```bash
# frontend 디렉토리에서
cd frontend
npm install
npm run dev
```

프론트엔드가 `http://localhost:5173`에서 실행됩니다.

### H2 콘솔 접근

개발 중 데이터베이스 확인이 필요한 경우:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:timecapsule`
- Username: `sa`
- Password: (비워두기)

## 프로젝트 구조

```
time-capsule/
├── src/main/java/com/timecapsule/
│   ├── config/          # 설정 클래스
│   ├── controller/      # REST API 컨트롤러
│   ├── dto/             # 데이터 전송 객체
│   │   ├── request/     # 요청 DTO
│   │   └── response/    # 응답 DTO
│   ├── entity/          # JPA 엔티티
│   ├── exception/       # 예외 처리
│   ├── repository/      # 데이터 접근 계층
│   ├── security/        # 보안 관련 클래스
│   └── service/         # 비즈니스 로직
├── src/main/resources/
│   └── application.yml  # 애플리케이션 설정
├── frontend/
│   ├── src/
│   │   ├── api/         # API 클라이언트
│   │   ├── components/  # 재사용 컴포넌트
│   │   ├── contexts/    # React Context
│   │   ├── pages/       # 페이지 컴포넌트
│   │   └── utils/       # 유틸리티 함수
│   └── package.json
├── build.gradle
└── README.md
```

## API 엔드포인트

### 인증 (Auth)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/auth/signup` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 |

### 사용자 (Users)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/users/me` | 내 정보 조회 |
| PUT | `/api/v1/users/me` | 프로필 수정 |
| PUT | `/api/v1/users/me/password` | 비밀번호 변경 |
| DELETE | `/api/v1/users/me` | 계정 삭제 |

### 예측 (Predictions)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/predictions` | 예측 생성 |
| GET | `/api/v1/predictions` | 내 예측 목록 |
| GET | `/api/v1/predictions/{id}` | 예측 상세 |
| DELETE | `/api/v1/predictions/{id}` | 예측 삭제 |

### 공개 API (Public)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/public/predictions/{accessCode}` | 공유 링크로 예측 조회 |
| GET | `/api/v1/public/attachments/{accessCode}/{id}` | 공개 첨부파일 다운로드 |

### 클레임 (Claims)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/claims` | 예측 클레임 (내 계정에 연결) |
| GET | `/api/v1/claims` | 받은 예측 목록 |
| DELETE | `/api/v1/claims/{id}` | 클레임 해제 |

## 데이터베이스 스키마

```
┌─────────────┐     ┌──────────────┐     ┌─────────────────────┐
│    users    │     │  predictions │     │ prediction_recipients│
├─────────────┤     ├──────────────┤     ├─────────────────────┤
│ id          │◄────│ author_id    │     │ id                  │
│ email       │     │ id           │◄────│ prediction_id       │
│ password    │     │ title        │     │ recipient_email     │
│ nickname    │     │ content      │     │ recipient_name      │
│ created_at  │     │ release_date │     │ access_code         │
└─────────────┘     │ is_public    │     │ notified            │
       ▲            │ created_at   │     │ created_at          │
       │            └──────────────┘     └─────────────────────┘
       │                   │                       │
       │                   │                       │
       │                   ▼                       ▼
       │            ┌──────────────┐     ┌─────────────────────┐
       │            │ attachments  │     │  recipient_claims   │
       │            ├──────────────┤     ├─────────────────────┤
       │            │ id           │     │ id                  │
       │            │ prediction_id│     │ recipient_id        │
       │            │ original_name│     │ claimed_by          │────┘
       └────────────│ stored_name  │     │ claimed_at          │
                    │ file_path    │     └─────────────────────┘
                    │ file_size    │
                    │ mime_type    │
                    └──────────────┘
```

## 사용 시나리오

### 예측 작성자
1. 회원가입 및 로그인
2. 새 예측 작성 (제목, 내용, 공개일, 수신자, 첨부파일)
3. 생성된 공유 링크를 수신자에게 전달
4. 공개일에 수신자가 예측 내용 확인

### 예측 수신자
1. 공유 링크 클릭
2. 공개일 전: 제목과 공개 예정일만 확인
3. 공개일 후: 전체 내용과 첨부파일 확인
4. (선택) 회원가입 후 클레임하여 마이페이지에서 관리

## Gmail SMTP 설정

이메일 알림을 사용하려면 Gmail 앱 비밀번호가 필요합니다:

1. Google 계정 → 보안 → 2단계 인증 활성화
2. 앱 비밀번호 생성 (메일 앱 선택)
3. 생성된 16자리 비밀번호를 `.env`의 `MAIL_PASSWORD`에 설정

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
