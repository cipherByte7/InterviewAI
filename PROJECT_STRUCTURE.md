# InterviewAI — Project Structure & Architecture

> **Last Updated**: August 6, 2026

---

## High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                     ANDROID CLIENT (Kotlin)                      │
│                                                                  │
│  ┌──────────┐    ┌──────────────┐    ┌───────────────────────┐   │
│  │  Screens  │◄──│  ViewModels  │◄──│  Data Layer (Retrofit) │   │
│  │ (Compose) │    │ (StateFlow)  │    │  + DataStore + Models  │   │
│  └──────────┘    └──────────────┘    └───────────┬───────────┘   │
│                                                   │               │
│  ┌──────────────────────────────────────────────┐ │               │
│  │  Utils: TextToSpeech / SpeechRecognizer       │ │               │
│  └──────────────────────────────────────────────┘ │               │
└───────────────────────────────────────────────────┼───────────────┘
                                                    │ HTTP (REST)
                                                    ▼
┌──────────────────────────────────────────────────────────────────┐
│                    NODE.JS BACKEND (Express)                     │
│                                                                  │
│  ┌────────┐    ┌──────────────┐    ┌───────────────────────────┐ │
│  │ Routes │───►│ Controllers  │───►│ Services (Gemini AI SDK)  │ │
│  └────────┘    └──────────────┘    └───────────────────────────┘ │
│       │                                                          │
│  ┌────────────┐    ┌───────────────────────────────────────────┐ │
│  │ Middleware  │    │  Models (Mongoose) → MongoDB Atlas        │ │
│  │  (JWT Auth) │    │  + In-Memory Fallback Arrays              │ │
│  └────────────┘    └───────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

---

## Full Directory Tree

```
InterviewAI/
│
├── README.md                            # GitHub documentation
├── project_context.md                   # Detailed handoff & milestone history
├── .gitignore
│
├── frontend/                            # ─── ANDROID CLIENT ───────────────────
│   ├── app/
│   │   ├── build.gradle.kts             # App-level Gradle (dependencies, SDK config)
│   │   └── src/main/
│   │       ├── AndroidManifest.xml       # Permissions: INTERNET, RECORD_AUDIO, READ_EXTERNAL_STORAGE
│   │       │
│   │       ├── res/
│   │       │   ├── font/                 # Inter font family (4 weights)
│   │       │   │   ├── inter_regular.ttf
│   │       │   │   ├── inter_medium.ttf
│   │       │   │   ├── inter_semibold.ttf
│   │       │   │   └── inter_bold.ttf
│   │       │   ├── values/
│   │       │   │   ├── colors.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   ├── drawable/
│   │       │   ├── mipmap-*/             # Launcher icons
│   │       │   └── xml/                  # Backup & data extraction rules
│   │       │
│   │       └── java/com/example/interview_ai/
│   │           │
│   │           ├── MainActivity.kt       # Single Activity entry point → NavGraph
│   │           │
│   │           ├── data/                 # ── DATA LAYER ──────────────────────
│   │           │   ├── api/
│   │           │   │   ├── RetrofitClient.kt        # Singleton HTTP client (OkHttp + Gson)
│   │           │   │   │                             #   • Base URL: http://10.0.2.2:5000/
│   │           │   │   │                             #   • Auth interceptor injects Bearer token
│   │           │   │   └── InterviewApiService.kt   # Retrofit interface — all API endpoints
│   │           │   │                                 #   • Auth: login, register, getUser, logout
│   │           │   │                                 #   • Resume: upload, parse, confirm, delete
│   │           │   │                                 #   • Interview: start, nextQuestion, evaluate
│   │           │   │                                 #   • History: getHistory, getReport
│   │           │   │
│   │           │   ├── datastore/
│   │           │   │   └── AuthPreferences.kt       # Jetpack DataStore wrapper
│   │           │   │                                 #   • Persists JWT token, user name, email, role
│   │           │   │                                 #   • Exposes Flow-based reads for reactive auth
│   │           │   │
│   │           │   └── model/                        # Domain & UI state data classes
│   │           │       ├── User.kt                   #   User(id, name, email, targetRole)
│   │           │       ├── AuthUiState.kt            #   Form inputs, field errors, loading, auth status
│   │           │       ├── DashboardUiState.kt       #   Resume state, stats, parse errors
│   │           │       ├── ParsedResume.kt           #   Skills list, experience, education, projects
│   │           │       ├── Question.kt               #   Question text, category, difficulty
│   │           │       ├── InterviewUiState.kt       #   InterviewState enum, conversation tracking
│   │           │       └── EvaluationReport.kt       #   Dimensions[], strengths[], weaknesses[], suggestion
│   │           │
│   │           ├── theme/                # ── DESIGN SYSTEM ──────────────────
│   │           │   ├── Color.kt          #   20+ semantic color tokens (dark palette)
│   │           │   ├── Dimensions.kt     #   AppSpacing, AppRadius, AppIconSize tokens
│   │           │   ├── Type.kt           #   Inter typography mapped to all M3 slots
│   │           │   └── Theme.kt          #   InterviewAITheme — enforced dark-only colorScheme
│   │           │
│   │           ├── ui/                   # ── PRESENTATION LAYER ─────────────
│   │           │   ├── components/       #   Reusable composables (shared across screens)
│   │           │   │   ├── PrimaryButton.kt      # Full-width CTA with loading spinner
│   │           │   │   ├── SecondaryButton.kt    # Outlined action button
│   │           │   │   ├── AppTextField.kt       # Labeled input with validation errors
│   │           │   │   ├── SurfaceCard.kt        # Dark bordered card container
│   │           │   │   ├── AppTopBar.kt          # Header with back nav + subtitle
│   │           │   │   └── BottomNavBar.kt       # Home / History / Profile tab bar
│   │           │   │
│   │           │   ├── navigation/
│   │           │   │   ├── Routes.kt             # Sealed class of all route string keys
│   │           │   │   └── NavGraph.kt           # Central NavHost wiring all screens
│   │           │   │
│   │           │   └── screens/
│   │           │       ├── splash/
│   │           │       │   └── SplashScreen.kt       # Animated radial glow logo → auto-nav
│   │           │       ├── auth/
│   │           │       │   ├── LoginScreen.kt        # Email/password sign-in form
│   │           │       │   └── RegisterScreen.kt     # Name/role/email/password sign-up form
│   │           │       ├── dashboard/
│   │           │       │   └── DashboardScreen.kt    # Stats cards, resume upload, quick actions
│   │           │       ├── interview/
│   │           │       │   └── InterviewScreen.kt    # Voice interview — pulsing visualizer orb
│   │           │       ├── report/
│   │           │       │   └── ReportScreen.kt       # Score dimensions, strengths, action plan
│   │           │       ├── profile/
│   │           │       │   └── ProfileScreen.kt      # User info, role editor, sign-out
│   │           │       └── history/
│   │           │           └── HistoryScreen.kt      # Past sessions list with search/filter
│   │           │
│   │           ├── utils/                # ── PLATFORM UTILITIES ──────────────
│   │           │   ├── TextToSpeechEngine.kt     # Android TTS wrapper (speaks AI questions)
│   │           │   └── SpeechToTextEngine.kt     # Android SpeechRecognizer wrapper (captures user answers)
│   │           │
│   │           ├── viewmodel/            # ── BUSINESS LOGIC ─────────────────
│   │           │   ├── AuthViewModel.kt          # Login/register flows, JWT session management
│   │           │   ├── DashboardViewModel.kt     # Resume upload/parse, stats aggregation
│   │           │   ├── InterviewViewModel.kt     # Voice loop state machine, silence detection
│   │           │   ├── ReportViewModel.kt        # Fetches latest evaluation report
│   │           │   └── HistoryViewModel.kt       # Queries past interview reports
│   │           │
│   │           └── di/                   # (Reserved for dependency injection)
│   │
│   ├── build.gradle.kts                  # Root Gradle config
│   ├── settings.gradle.kts               # Module declarations
│   └── gradle/libs.versions.toml         # Version catalog
│
│
└── backend/                              # ─── NODE.JS REST API ────────────────
    ├── server.js                         # Express app entry point (port 5000)
    ├── package.json                      # npm dependencies
    ├── .env                              # MONGO_URI, JWT_SECRET, GEMINI_API_KEY
    │
    ├── config/
    │   └── db.js                         # MongoDB connection + in-memory fallback arrays
    │
    ├── middleware/
    │   └── auth.js                       # JWT verification middleware
    │                                     #   • Extracts Bearer token from Authorization header
    │                                     #   • Verifies via jsonwebtoken
    │                                     #   • Checks token blacklist (logout support)
    │                                     #   • Attaches req.userId for downstream handlers
    │
    ├── models/
    │   ├── User.js                       # Mongoose schema: name, email, password (bcrypt),
    │   │                                 #   targetRole, parsedResume {}, date
    │   └── Report.js                     # Mongoose schema: userId, role, date, overallScore,
    │                                     #   duration, dimensions[], strengths[], weaknesses[],
    │                                     #   suggestion
    │
    ├── controllers/
    │   ├── authController.js             # register, login, getUser, updateProfile, logout,
    │   │                                 #   refreshToken + token blacklist management
    │   ├── resumeController.js           # uploadResume (PDF→Gemini parse), confirmResume,
    │   │                                 #   deleteResume
    │   └── interviewController.js        # startInterview, nextQuestion, evaluateInterview
    │                                     #   (all powered by Gemini adaptive prompts)
    │
    ├── routes/
    │   ├── authRoutes.js                 # POST /register, /login, /logout, /refresh
    │   ├── userRoutes.js                 # GET /user, PUT /user/profile
    │   ├── resumeRoutes.js              # POST /upload, PUT /confirm, DELETE /
    │   └── interviewRoutes.js           # POST /start, /next-question, /evaluate
    │                                     #   GET /history, /report/:id
    │
    └── services/
        └── geminiService.js              # Google Gemini 1.5 Flash AI integration
                                          #   • parseResumeWithAI(text) — extracts skills/experience
                                          #   • generateFirstQuestionWithAI(resume, role)
                                          #   • generateNextQuestionWithAI(history, resume, role)
                                          #   • evaluateInterviewWithAI(history, resume, role)
```

---

## Data Flow

### Authentication Flow
```
LoginScreen → AuthViewModel.login()
  → POST /api/auth/login { email, password }
    → authController.login()
      → User.findOne({ email })
      → bcrypt.compare(password, hash)
      → jwt.sign({ userId }) → 7-day token
    ← { token, user }
  → RetrofitClient.setToken(token)
  → AuthPreferences.saveAuthSession(token, name, email, role)
  → Navigate to Dashboard
```

### Interview Flow (Adaptive Voice Loop)
```
DashboardScreen → "Start Interview"
  → InterviewViewModel.startSession()
    → POST /api/interview/start { resumeText, targetRole }
      → geminiService.generateFirstQuestionWithAI()
    ← { sessionId, question, questionNumber, totalQuestions }

  ┌─── LOOP ──────────────────────────────────────────┐
  │  State: AI_SPEAKING                                │
  │    TextToSpeechEngine speaks the question           │
  │                    ▼                               │
  │  State: LISTENING                                  │
  │    SpeechToTextEngine captures user's voice         │
  │    Live transcript updates in real-time             │
  │                    ▼                               │
  │  State: SILENCE_DETECTION                          │
  │    5-second silence timer (coroutine debouncer)     │
  │    Auto-submits when user stops speaking            │
  │                    ▼                               │
  │  State: PROCESSING                                 │
  │    → POST /api/interview/next-question              │
  │      { conversationHistory, resumeText, targetRole }│
  │      → geminiService.generateNextQuestionWithAI()   │
  │    ← { question, questionNumber, isComplete }       │
  │                    ▼                               │
  │  If !isComplete → loop back to AI_SPEAKING         │
  └────────────────────────────────────────────────────┘

  State: COMPLETED
    → POST /api/interview/evaluate { conversationHistory }
      → geminiService.evaluateInterviewWithAI()
      → Report saved to MongoDB
    ← EvaluationReport
    → Navigate to ReportScreen
```

### Resume Upload Flow
```
DashboardScreen → File Picker → PDF selected
  → DashboardViewModel.uploadResume(uri)
    → Read bytes from ContentResolver
    → POST /api/resume/upload (multipart/form-data)
      → resumeController.uploadResume()
        → Extract text from PDF (pdf-parse)
        → geminiService.parseResumeWithAI(text)
        → Save parsedResume to User document
      ← { skills[], experience, education, projects }
    → Display parsed resume tags on Dashboard
```

---

## API Endpoint Reference

| Method | Endpoint                        | Auth | Description                              |
|--------|---------------------------------|------|------------------------------------------|
| POST   | `/api/auth/register`            | No   | Create new user account                  |
| POST   | `/api/auth/login`               | No   | Authenticate and receive JWT             |
| POST   | `/api/auth/logout`              | Yes  | Blacklist current token                  |
| POST   | `/api/auth/refresh`             | Yes  | Issue fresh 7-day JWT                    |
| GET    | `/api/auth/user`                | Yes  | Get user profile + computed stats        |
| PUT    | `/api/user/profile`             | Yes  | Update target role                       |
| POST   | `/api/resume/upload`            | Yes  | Upload PDF → AI parse resume             |
| PUT    | `/api/resume/confirm`           | Yes  | Confirm parsed resume data               |
| DELETE | `/api/resume`                   | Yes  | Clear resume from profile                |
| POST   | `/api/interview/start`          | Yes  | Begin interview → first AI question      |
| POST   | `/api/interview/next-question`  | Yes  | Submit answer → next adaptive question   |
| POST   | `/api/interview/evaluate`       | Yes  | Complete interview → evaluation report   |
| GET    | `/api/interview/history`        | Yes  | List all past reports for user           |
| GET    | `/api/interview/report/:id`     | Yes  | Get single report by ID                  |

---

## Key Technology Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Architecture | MVVM | Clean separation: Screens observe ViewModels via StateFlow |
| UI Framework | Jetpack Compose | Modern declarative UI with less boilerplate than XML |
| State Management | StateFlow + collectAsState | Lifecycle-aware, composable-friendly reactive streams |
| Network Layer | Retrofit + OkHttp | Industry standard for type-safe REST clients on Android |
| Auth Persistence | Jetpack DataStore | Async, coroutine-based replacement for SharedPreferences |
| Backend Framework | Express.js | Lightweight, fast to develop REST APIs |
| Database | MongoDB Atlas | Flexible document store; Mongoose ODM for schemas |
| AI Engine | Google Gemini 1.5 Flash | Fast inference, good at structured JSON output |
| Password Security | bcrypt (10 salt rounds) | Industry standard password hashing |
| Token Auth | JWT (7-day expiry) | Stateless auth with blacklist for logout |
| Voice Input | Android SpeechRecognizer | Native STT — no external API calls needed |
| Voice Output | Android TextToSpeech | Native TTS — zero latency for speaking questions |

---

## Build & Run

### Prerequisites
- **Android Studio** (latest stable)
- **Node.js** 18+
- **MongoDB Atlas** connection string (or local MongoDB)
- **Gemini API Key** from Google AI Studio

### 1. Start Backend
```bash
cd backend
npm install
# Configure .env: MONGO_URI, JWT_SECRET, GEMINI_API_KEY
node server.js
# → InterviewAI Server launched on port 5000
```

### 2. Run Android App
```bash
cd frontend
# Open in Android Studio, sync Gradle, run on emulator (API 24+)
# App connects to backend via 10.0.2.2:5000 (emulator loopback)
```

---

## Dependencies

### Android (frontend)
| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 2.1.21 | Language |
| Compose BOM | 2025.06.01 | UI framework |
| Navigation Compose | 2.9.3 | Screen routing |
| Retrofit | 2.11.0 | HTTP client |
| OkHttp | 4.12.0 | HTTP transport |
| Gson Converter | 2.11.0 | JSON serialization |
| DataStore Preferences | 1.1.1 | Auth token persistence |
| Gradle | 9.1.0 | Build system |
| AGP | 8.11.1 | Android Gradle Plugin |

### Backend (Node.js)
| Library | Purpose |
|---------|---------|
| express | REST API framework |
| mongoose | MongoDB ODM |
| bcryptjs | Password hashing |
| jsonwebtoken | JWT token generation/verification |
| multer | Multipart file upload handling |
| pdf-parse | PDF text extraction |
| @google/generative-ai | Gemini AI SDK |
| dotenv | Environment variable management |
| cors | Cross-origin resource sharing |
