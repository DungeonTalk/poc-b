# 🏰 던전톡 (DungeonTalk) - TRPG 웹 게임

AI 던전마스터와 함께하는 판타지 TRPG(테이블탑 롤플레잉 게임) 웹 서비스입니다.

## ✨ 주요 기능

- 🤖 **AI 던전마스터**: Google Gemini API를 활용한 지능형 게임 진행
- 💬 **실시간 채팅**: 플레이어와 던전마스터 간 자연스러운 대화
- 🎮 **빠른 행동**: 자주 사용하는 액션들을 버튼으로 제공
- 📱 **반응형 UI**: 데스크톱과 모바일 모두 지원
- 🇰🇷 **완전한 한국어 지원**: 한국어로 진행되는 TRPG 게임

## 🚀 시작하기

### 필요 조건

- Java 21+
- Gradle 8.0+
- Google Gemini API 키

### 설치 및 실행

1. **저장소 클론**
   ```bash
   git clone https://github.com/DungeonTalk/poc-b.git
   cd poc-b
   ```

2. **환경 변수 설정**
   
   Gemini API 키를 환경 변수로 설정해주세요:
   
   **Windows:**
   ```cmd
   set GEMINI_API_KEY=your_gemini_api_key_here
   ```
   
   **Linux/Mac:**
   ```bash
   export GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

4. **웹 브라우저에서 접속**
   ```
   http://localhost:8083
   ```

## 🎲 게임 방법

1. 웹 페이지에 접속하면 던전마스터가 환영 메시지를 보여줍니다
2. 채팅창에 원하는 행동을 입력하거나 빠른 행동 버튼을 클릭하세요
3. 던전마스터가 상황을 묘사하고 새로운 선택지를 제시합니다
4. 계속해서 상호작용하며 판타지 모험을 즐기세요!

### 빠른 행동 예시

- 🗡️ **새 모험 시작**: 새로운 TRPG 모험 시작
- 👀 **주변 탐색**: 현재 위치 주변을 자세히 살펴보기  
- 🚶 **조심스럽게 전진**: 안전하게 앞으로 이동
- 😴 **휴식**: 체력 회복 및 휴식
- 🎒 **인벤토리**: 소지품 확인

## 🛠 기술 스택

- **Backend**: Spring Boot 3.4.0 + WebFlux
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **AI**: Google Gemini API
- **Build Tool**: Gradle
- **Java Version**: 21

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/pocai/
│   │   ├── config/          # 설정 클래스들
│   │   ├── controller/      # REST API 컨트롤러
│   │   ├── dto/            # 데이터 전송 객체
│   │   └── service/        # 비즈니스 로직
│   └── resources/
│       ├── static/         # 웹 리소스 (HTML, CSS, JS)
│       └── application.properties
```

## 🔧 설정

### application.properties

```properties
server.port=8083

# Google Gemini API 설정
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent
gemini.api.key=${GEMINI_API_KEY:your_gemini_api_key_here}

# 로깅 설정
logging.level.com.dungeontalk=DEBUG
```

## 🔐 보안

- API 키는 환경 변수로 관리
- .gitignore에 민감한 정보 제외
- CORS 설정으로 안전한 브라우저 접근

## 🤝 기여하기

1. 이 저장소를 포크하세요
2. 새로운 기능 브랜치를 만드세요 (`git checkout -b feature/새로운기능`)
3. 변경사항을 커밋하세요 (`git commit -am '새로운 기능: 설명'`)
4. 브랜치에 푸시하세요 (`git push origin feature/새로운기능`)
5. Pull Request를 생성하세요

## 📜 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다.

## 🔮 향후 계획

- [ ] STOMP 웹소켓을 통한 실시간 통신
- [ ] 사용자 계정 및 세션 관리
- [ ] 다이스 롤링 시스템
- [ ] 캐릭터 시트 관리
- [ ] 멀티플레이어 지원
- [ ] 게임 저장/불러오기 기능

---

**Made with ❤️ by DungeonTalk Team**