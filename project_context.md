# InterviewAI — Project Context & Handoff Document

> **Last Updated**: August 6, 2026
> **Platform**: Android (Native)
> **Language**: Kotlin
> **UI Framework**: Jetpack Compose + Material 3
> **Architecture**: MVVM (Model-View-ViewModel)
> **Package Name**: `com.example.interview_ai`
> **Min SDK**: 24 (Android 7.0) | **Target SDK**: 36

---

## 1. Project Aim

InterviewAI is a **production-quality Android application** that helps job seekers prepare for technical and behavioral interviews using artificial intelligence.

The user uploads their resume (PDF), and the app:
1. Parses and analyzes the resume using AI.
2. Generates personalized interview questions based on the resume content, target role, and experience level.
3. Conducts a real-time voice-based mock interview (Speech-to-Text for user responses, Text-to-Speech for the AI interviewer).
4. Asks adaptive follow-up questions based on the quality and content of the user's answers.
5. Evaluates performance across multiple dimensions (technical accuracy, communication clarity, confidence, depth of knowledge).
6. Produces a detailed performance report with scores, strengths, weaknesses, and improvement suggestions.
7. Maintains a history of all past interviews so the user can track progress over time.

The end goal is an app that could be **published on the Google Play Store** and demonstrated during campus placements or interviews as a portfolio project.

---

## 2. Design Philosophy

The UI is designed to feel like **ChatGPT, Linear, Notion, Cursor, or Perplexity** — not a colorful student project.

- **Dark theme only** (enforced, no light mode toggle)
- **Minimal and premium** aesthetic
- **Consistent spacing** using design tokens
- **Inter font family** throughout
- **Subtle micro-animations** for transitions
- **Reusable component library** (no duplicated UI code)
- **Material 3** with custom dark color scheme

---

## 3. Tech Stack

### Currently Used
| Layer | Technology | Purpose |
|-------|------------|---------|
| Language | Kotlin | Android client-side coding |
| UI | Jetpack Compose | Modern declarative screen composable layers |
| Design System | Material 3 (custom dark theme) | Sleek dark theme tokens mapping |
| Navigation | Navigation Compose | Centralized routing paths |
| Networking | Retrofit + OkHttp | REST API call triggers to Node.js backend |
| Local Storage | DataStore Preferences | Caching JWT tokens and active target roles |
| Voice Synthesizing | Native Android TTS Engine | Reading mock questions to candidate |
| Voice Recording | Native Android STT Engine | Capturing verbal speech to text transcript feeds |
| Backend API | Node.js + Express | Routing auth endpoints and calling Gemini APIs |
| Database | MongoDB | Storing user models and completed session evaluations |
| Artificial Intelligence | Gemini API (Gemini 1.5 Flash) | Resume parsing, questions creator, transcript analysis |

---

## 4. Project Structure

The codebase is organized into two primary root directories: `frontend/` (Android App) and `backend/` (Express API server).

### Backend (NodeJS Server)
```
backend/
├── config/
│   └── db.js                       # MongoDB connection & in-memory cache definitions
├── controllers/
│   ├── authController.js           # Auth handlers (login, register, logout, profile)
│   ├── resumeController.js         # Resume parser handlers
│   └── interviewController.js      # Questions generator and evaluation reports
├── middleware/
│   └── auth.js                     # JWT verification & token blacklist interceptor
├── models/
│   ├── User.js                     # User database schema model
│   └── Report.js                   # Evaluation report database schema model
├── routes/
│   ├── authRoutes.js               # Route maps for auth operations
│   ├── userRoutes.js               # Route maps for user profiles
│   ├── resumeRoutes.js             # Route maps for resume uploads
│   └── interviewRoutes.js          # Route maps for question prep & mock evaluations
├── services/
│   └── geminiService.js            # Gemini 1.5 Flash API connector wrapper
├── package.json                    # Npm metadata and dependencies config
├── .env                            # Host config parameters, Mongo URI, Gemini API Key
└── server.js                       # Clean, slim Express server entry point
```

### Frontend (Android Application)
```
frontend/app/src/main/java/com/example/interview_ai/
│
├── MainActivity.kt                 # Entry point, sets up theme and NavGraph
│
├── data/
│   ├── api/                        # Retrofit client and API interface endpoints
│   ├── datastore/                  # Jetpack DataStore preferences caching sessions
│   ├── model/
│   │   ├── User.kt                # User credentials data class
│   │   ├── AuthUiState.kt         # UI state validation fields
│   │   ├── DashboardUiState.kt    # Dashboard statistics counters
│   │   ├── ParsedResume.kt        # Structured parsed resume tag models
│   │   ├── Question.kt            # Tailored question structure details
│   │   ├── InterviewUiState.kt    # Active voice interview configurations
│   │   └── EvaluationReport.kt    # Performance scores metrics
│
├── theme/
│   ├── Color.kt                   # Theme colors
│   ├── Dimensions.kt              # Spacing, padding, and corner radius tokens
│   ├── Theme.kt                   # Material Theme colors mappings
│   └── Type.kt                    # Inter Typography definitions
│
├── ui/
│   ├── components/
│   │   ├── PrimaryButton.kt       # Premium primary actions indicator
│   │   ├── SecondaryButton.kt     # Secondary custom boundary links
│   │   ├── AppTextField.kt        # Custom text validator input layouts
│   │   ├── SurfaceCard.kt         # Card containers
│   │   └── AppTopBar.kt           # Central header elements
│   │
│   ├── navigation/
│   │   ├── Routes.kt              # String route keys definitions
│   │   └── NavGraph.kt            # Central NavHost transitions router
│   │
│   └── screens/
│       ├── splash/
│       │   └── SplashScreen.kt    # Animated splash loader
│       ├── auth/
│       │   ├── LoginScreen.kt     # Log-in email inputs form
│       │   └── RegisterScreen.kt  # User sign-up roles form
│       ├── dashboard/
│       │   └── DashboardScreen.kt # Home analytics board and file uploads sheet
│       ├── interview/
│       │   └── InterviewScreen.kt # Prep details and active visualizer TTS/STT flows
│       ├── report/
│       │   └── ReportScreen.kt    # Dimensions breakdown scores dashboard
│       ├── profile/
│       │   └── ProfileScreen.kt   # Profile details, target roles editor, and sign-out controls
│       └── history/
│           └── HistoryScreen.kt   # Previous sessions lists and search filters
│
├── utils/
│   ├── TextToSpeechEngine.kt       # Native TextToSpeech player wrapper
│   └── SpeechToTextEngine.kt       # Native SpeechRecognizer recorders
│
└── viewmodel/
    ├── AuthViewModel.kt           # Controls user authentication, login/register flows, and session management
    ├── DashboardViewModel.kt      # Manages resume parsing and aggregate totals
    ├── InterviewViewModel.kt      # Runs voice mock sessions and evaluations
    ├── ReportViewModel.kt         # Retrieves latest report details
    └── HistoryViewModel.kt        # Queries past reports arrays
```

### Resource Files
```
app/src/main/res/
├── font/
│   ├── inter_regular.ttf
│   ├── inter_medium.ttf
│   ├── inter_semibold.ttf
│   └── inter_bold.ttf
├── values/
│   ├── colors.xml
│   ├── strings.xml
│   └── themes.xml
├── drawable/
├── mipmap-*/                       # App launcher icons (default)
└── xml/                            # Backup rules
```

---

## 5. Color Palette

| Token | Hex | Usage |
|-------|-----|-------|
| `BackgroundDark` | `#090D16` | App background |
| `SurfaceDark` | `#111726` | Cards, input fields |
| `SurfaceVariantDark` | `#1A2235` | Elevated surfaces |
| `SurfaceElevated` | `#222C42` | Higher elevation containers |
| `Primary` | `#6366F1` | Primary accent (Indigo) |
| `PrimaryVariant` | `#4F46E5` | Primary container |
| `PrimaryGlow` | `#336366F1` | Radial glow overlays |
| `AccentCyan` | `#06B6D4` | Secondary accent |
| `AccentPurple` | `#A855F7` | Tertiary accent |
| `TextPrimary` | `#F8FAFC` | Main text |
| `TextSecondary` | `#94A3B8` | Secondary text |
| `TextMuted` | `#64748B` | Placeholders, hints |
| `BorderSubtle` | `#1E293B` | Default borders |
| `BorderHighlight` | `#334155` | Active/hover borders |
| `Success` | `#10B981` | Positive indicators |
| `Error` | `#EF4444` | Error states |
| `Warning` | `#F59E0B` | Warning states |
| `Info` | `#3B82F6` | Informational states |

---

## 6. Features Completed So Far

### Phase 1 — Project Setup & Navigation ✅
- Android project initialized with Kotlin + Jetpack Compose
- GitHub repository created and connected
- MVVM folder structure established (`data/`, `di/`, `ui/`, `theme/`, `utils/`, `viewmodel/`)
- Navigation Compose integrated with centralized `Routes.kt` (sealed class) and `NavGraph.kt`
- Placeholder screens created for all routes (Splash, Login, Register, Dashboard, Interview, Report, History, Profile)
- `MainActivity.kt` connected to `InterviewAITheme` and `AppNavGraph`

### Phase 2 — Design System & Splash Screen ✅
- **Color System**: Comprehensive dark color palette defined in `Color.kt` (backgrounds, surfaces, accents, text, borders, status, overlays)
- **Typography**: Inter font family (4 weights) loaded from `res/font/` and mapped to all 12 Material 3 Typography slots in `Type.kt`
- **Design Tokens**: `AppSpacing` (xs through xxxl), `AppRadius` (sm through full), `AppIconSize` (sm through xl) defined in `Dimensions.kt`
- **Theme**: `InterviewAITheme` enforces dark-only mode, custom Material 3 `darkColorScheme`, and sets system status/navigation bar colors to match
- **Reusable Components**:
  - `PrimaryButton` — full-width action button with loading spinner state, leading/trailing icon slots
  - `SecondaryButton` — outlined button with subtle border on dark surface
  - `AppTextField` — labeled text input with placeholder, leading/trailing icons, error message display, focus-aware border color
  - `SurfaceCard` — bordered dark card container with optional click handler
  - `AppTopBar` — top bar with optional back navigation, subtitle, and action slot
- **Animated Splash Screen**: Radial glowing background (PrimaryGlow gradient), scale + fade animation on logo emblem and text, auto-navigates to Login after delay

### Phase 3 — Authentication (Login & Register) ✅
- **Data Models**:
  - `User.kt` — domain model with `id`, `name`, `email`, `targetRole`
  - `AuthUiState.kt` — UI state holding all form inputs, per-field error messages, loading flag, and authenticated user
- **AuthViewModel**:
  - Reactive `StateFlow<AuthUiState>` for form state management
  - Real-time input handlers (`onEmailChanged`, `onPasswordChanged`, `onNameChanged`, `onTargetRoleChanged`)
  - Password visibility toggle
  - Validation logic: email format check (Android `Patterns.EMAIL_ADDRESS`), password minimum length (6 chars), required field checks
  - Simulated async `login()` and `register()` functions with loading delay and success callback
- **LoginScreen**:
  - Dark radial background with glowing emblem header
  - Email input with email icon and keyboard type
  - Password input with lock icon and SHOW/HIDE text toggle
  - "Forgot Password?" link (placeholder action)
  - "Sign In" primary button with loading state
  - "Don't have an account? Sign Up" footer navigation
  - On successful login → navigates to Dashboard (pops Login from backstack)
- **RegisterScreen**:
  - Same premium dark aesthetic as Login
  - Full Name input (person icon)
  - Target Role / Specialization input (edit icon)
  - Email input (email icon)
  - Password input with SHOW/HIDE toggle
  - "Create Account" primary button with loading state
  - "Already have an account? Sign In" footer navigation (pops back to Login)
  - On successful register → navigates to Dashboard
- **NavGraph Updated**: `AuthViewModel` instantiated at NavGraph level and shared between Login and Register screens

### Phase 4 — Dashboard ✅
- **UI Architecture**: Implemented custom home layout with a Radial Glow brush background in dark theme.
- **Greeting & Roles**: Welcome greeting displaying user's first name with a custom Target Role border badge.
- **Readiness progress bar**: Dynamic `LinearProgressIndicator` showing total Readiness score (e.g. 84%) with action helper notes.
- **Summary Cards**: Stats overview displaying total completed sessions (12) and total practice hours (4.5h).
- **Quick Action Triggers**: Highly styled clickable cards for launching "Interactive AI Interview" and "Upload Resume".
- **Activity Feed**: Interactive list of recent sessions displaying role titles, date details, and custom colored score badges (Success/Warning/Error) for status.
- **Central Navigation**: Customized Bottom Navigation Bar (Home, History, Profile) with active indicator highlights.

### Phase 5 — Resume Upload ✅
- **Interactive sheet**: Integrated custom M3 `ModalBottomSheet` displaying resume status and offering file drop zone.
- **File System Launcher**: Integrated `rememberLauncherForActivityResult` triggering `GetContent` contract to select `.pdf` documents securely from device storage.
- **Metadata query**: Extracted file names from content resolver queries using `OpenableColumns.DISPLAY_NAME`.
- **Upload simulator**: Built ViewModel progress emitter showing simulated progress animations up to 100% completion.
- **State updates**: Dynamically highlighted the Dashboard Resume card with cyan borders when active, displaying file details and providing click-to-delete actions.

### Phase 6 — Resume Parsing ✅
- **Data Models**: Created [ParsedResume.kt](file:///d:/kotlin/InterviewAI/app/src/main/java/com/example/interview_ai/data/model/ParsedResume.kt) to map structured skills, experience level, education details, and project count.
- **Parsing triggers**: Configured ViewModel flow to automatically trigger asynchronous parsing immediately upon successful file upload.
- **Progress indicator**: Designed dynamic progress loader card displayed while parsing is active.
- **Interactive Review Card**: Implemented a profile review card displaying extracted role, education, experience years, and skill tags inside a custom M3 `FlowRow`.
- **Handoff verification**: Enabled user verification action which updates target role configurations dynamically across the app.

### Phase 7 — AI Question Generation ✅
- **Tailored config interface**: Implemented user selector controls for difficulty level (Junior/Mid/Senior), category focus (Technical/Behavioral/Mixed), and question count (5/10/15).
- **Domain logic generator**: Built custom ViewModel mock algorithms creating questions aligned directly to parsed resume skills and target developer roles.
- **Loader animation**: Styled an AI progress loading screen demonstrating step progress from 0% to 100%.
- **Interactive Question review**: Designed scrollable question lists in mock prep view with category badges and duration estimates.
- **ViewModel state wiring**: Connected Dashboard state directly with Interview customizer parameters.

### Phase 8 — Voice-Based Mock Interview ✅
- **Active Mock Session UI**: Designed immersive full-screen active interview view with glowing circular visualizers.
- **Visualizer pulse**: Built infinite scale transitions mapping voice states (AI Speaking, Listening, Paused) to visual pulses.
- **Timer & Progress**: Programmed active duration timers formatting minutes/seconds, alongside current question progress trackers (e.g. Question 2 of 5).
- **Simulated speaking / listening loop**: Coded simulated speech streams feeding word-by-word transcription text to real-time speech preview panels.
- **Session Controls**: Implemented floating controls for Pause, Resume, early Finish and Mute overrides.
- **Report navigation**: Integrated navigation triggers to auto-pass data and redirect directly to the Report Screen on session completion.

### Phase 9 — AI Evaluation ✅
- **Evaluation Entities**: Created [EvaluationReport.kt](file:///d:/kotlin/InterviewAI/app/src/main/java/com/example/interview_ai/data/model/EvaluationReport.kt) modeling performance categories and structured suggestions.
- **ViewModel evaluation loading**: Coded `ReportViewModel` state flow simulating asynchronous analytical delays before loading performance cards.
- **Aesthetic Grade Header**: Implemented glowing grade ring container displaying overall scores (e.g. 86/100) and practice metadata.
- **Score Dimension Cards**: Designed progress metrics for Technical Accuracy, Communication Clarity, Depth, and Confidence.
- **Feedback Breakdown Panels**: Added bullet points isolating Key Strengths (success markers) and Areas of Improvement (warning icons).
- **AI Action Plan block**: Styled a quotation panel highlighting specific custom improvement roadmaps.

### Phase 10 — Production Polish & Handoff ✅
- **Profile Screen**: Built custom profile credentials view, readiness statistics, parsed tags list, target roles editor, and sign-out controls (Phase 8 completion).
- **Project Restructuring**: Organized workspace into separated `frontend/` (Android Compose app) and `backend/` (NodeJS REST API server) folders.
- **Resilient Offline Bypass**: Implemented automated fallback loops to local mock data on database connect failures and request timeouts.
- **GitHub Documentation**: Added root GitHub README documentation covering system architecture flows, setup guides, and feature highlights.

### Milestone 11 — Schema Upgrade & Profile Persistence ✅
- **Database Schema Upgrades**: Extended `User.js` database model schema to store persistent `parsedResume` details (extracted skills list, experience, and uploaded file metadata).
- **PUT Target Role persistence**: Created `PUT /api/user/profile` endpoint on Express and connected it to Profile dialog saves to write role updates directly to MongoDB.
- **Dynamic Stats Aggregation**: Refactored `GET /api/auth/user` to compute candidate statistics (total sessions count, overall average score, total practice hours) using server-side query calculations.
- **Client Sync on Boot**: Programmed `DashboardViewModel` to parse user details, resume tags, and calculated stats directly from the network response on app launch.
- **ParsedResume model extended**: Added `uploadedResumeName` field to `ParsedResume.kt` matching backend schema — compile verified BUILD SUCCESSFUL.

### Milestone 12 — Resilient Speech Loop, Silence Detection & Permissions ✅
- **STT Error Code Handling**: `startSpeechToTextListener()` in `InterviewViewModel.kt` now maps Android SpeechRecognizer error codes:
  - Error 6 (No match) & Error 7 (Timeout) — **Silence detection**: auto-submits if transcript is non-empty, silently restarts listener otherwise.
  - Error 8 (Recognizer busy) — stops, waits 300ms, then safely restarts.
  - All other errors — transparent resilient restart while session is active.
- **`submitUserAnswer()` decoupled from callback**: Session completion callback (`onSessionCompleted`) is stored once at session start, removing the bug where the auto-submit from STT timeout had no `onCompleted` lambda.
- **`startInterviewSession(onCompleted)` upgraded**: Accepts the navigation lambda at session start and stores it — all code paths (normal submit, silence auto-submit, early finish) share one callback.
- **Runtime Audio Permission flow**: `InterviewScreen.kt` now checks `RECORD_AUDIO` permission at composable init:
  - If **granted**: starts session normally.
  - If **denied**: prompts OS rationale dialog via `rememberLauncherForActivityResult`.
  - If **denied during active session**: renders `PermissionDeniedContent` (info icon, explanation, and "Grant Permission" button) instead of the interview visualizer.
- **`PermissionDeniedContent` composable**: Premium dark UI with icon, explanation text, and retry grant button.
- All changes compile cleanly — **BUILD SUCCESSFUL in 14s**.

---

### Milestone 13 — Production Polish, Empty States & Error Handling ✅
- **HistoryViewModel upgraded**:
  - Added `isError: Boolean` and `errorMessage: String` to `HistoryUiState` — network failures now surface as real error states, not silent mock data fallbacks.
  - Exposed `refresh()` function for retry triggers from the UI.
- **HistoryScreen — 4 distinct states**:
  - `isLoading` → `HistoryLoadingSkeleton`: 5-row shimmer skeleton with gradient-filled boxes matching actual list shapes.
  - `isError` → `HistoryErrorContent`: Red radial glow icon, error heading, description, and "Retry" button wired to `viewModel.refresh()`.
  - `sessions.isEmpty()` → `HistoryEmptyContent`: Indigo radial glow icon, motivational copy, and CTA "Start My First Interview" button navigating to Interview route.
  - `filteredSessions.isEmpty()` → Search empty state with search icon and descriptive message.
- **DashboardUiState extended**: Added `parseError: Boolean` and `parseErrorMessage: String` fields.
- **DashboardViewModel — real PDF error handling**:
  - `uploadResume()`: Sets `parseError=true` with user-friendly message when file bytes are empty/unreadable.
  - `parseResume()`: Sets `parseError=true` with actionable message on API failure (password-protected, corrupted, unsupported format).
- **DashboardScreen — Parse Error UI**: Red-bordered `SurfaceCard` with error icon, message, and "Remove & Try Again" action shown when `parseError=true`.
- **AndroidManifest — permissions audit**:
  - Added `READ_EXTERNAL_STORAGE` with `android:maxSdkVersion="32"` for Android 9–12 compatibility.
  - `INTERNET` and `RECORD_AUDIO` confirmed and present.
- All changes compile cleanly — **BUILD SUCCESSFUL in 15s**.

---

### Milestone 14 — Backend API Completion ✅

Five previously missing backend endpoints added to [`server.js`](file:///d:/kotlin/InterviewAI/backend/server.js):

#### `GET /api/interview/report/:id`
- Fetches a **single evaluation report by its ID**, scoped to the authenticated user.
- Works against both MongoDB (`Report.findOne`) and the in-memory fallback.
- Returns 404 if the report doesn't belong to the requesting user.

#### `PUT /api/resume/confirm`
- Sets `parsedResume.isConfirmed = true` on the user document in MongoDB.
- In-memory fallback mutates the `memoryUsers` array entry directly.
- Returns the updated `parsedResume` object so the client can sync state without a separate fetch.

#### `DELETE /api/resume`
- Resets all `parsedResume` fields on the user document to empty defaults (blank strings, empty arrays, `isConfirmed: false`).
- Clears the resume both from MongoDB and the in-memory fallback.
- Returns a success confirmation message.

#### `POST /api/auth/logout`
- Adds the presented Bearer token to an in-memory **token blacklist** (`Set`).
- The `auth.js` middleware was upgraded with a `setBlacklistProvider()` hook so the blacklist is checked on every protected route — rejected with a 401 if the token was previously invalidated.
- Production note: replace the `Set` with Redis for persistence across server restarts.

#### `POST /api/auth/refresh`
- Accepts a valid (non-blacklisted) Bearer token and issues a **fresh 7-day JWT** for the same user.
- Looks up the user from MongoDB or in-memory store to confirm existence before issuing.
- Returns the new `token` + basic user payload so the client can swap it in DataStore seamlessly.

#### `middleware/auth.js` upgraded
- Added `setBlacklistProvider(fn)` export so `server.js` can register the shared `tokenBlacklist` Set.
- Every incoming authenticated request now checks the blacklist before proceeding.

### Milestone 15 — Authentication Polish & Session Handling ✅

- **Instant Offline/Unresponsive Logout**:
  - The logout mechanism in `AuthViewModel.kt` was refactored. The API call is now dispatched asynchronously in the background.
  - Local credentials, session tokens, and cached DataStore parameters are cleared instantaneously on the main thread, routing the user to the Login screen immediately without waiting for a server round-trip.
- **Auto-Login Redirection**:
  - Integrated `LaunchedEffect` in `LoginScreen.kt` observing `uiState.isAuthenticated` status.
  - If a cached session is loaded successfully during application boot, the user is automatically redirected to the `Dashboard` screen rather than getting stuck on the login input form.
- **HTTP Exception Error Parsing & Validation**:
  - Refactored `login` and `register` error handling in `AuthViewModel.kt` to explicitly catch `retrofit2.HttpException`.
  - Added JSON parsing to extract backend-returned validation messages (e.g., "Invalid credentials" or "User already exists").
  - Prevented incorrect auto-fallbacks to offline mode when the server has rejected credentials.

---

### Milestone 16 — Backend Modularization & Refactoring ✅

- **Split monolithic `server.js` into clean, MVC-compliant directories**:
  - `config/db.js`: Isolates database connection settings and exports in-memory DB fallbacks.
  - `services/geminiService.js`: Encapsulates Gemini generative AI client routines.
  - `controllers/`: Created `authController.js`, `resumeController.js`, and `interviewController.js` to manage request/response handlers separately.
  - `routes/`: Standardized routes configuration inside `authRoutes.js`, `userRoutes.js`, `resumeRoutes.js`, and `interviewRoutes.js`.
  - `server.js`: Cleaned to act solely as the app entry point config.
- Verified server launches and binds to port 5000 with zero syntax errors.

---

### Milestone 17 — Evaluation Page Refresh & API Robustness ✅

- **Auto-Refresh on Navigation**:
  - Integrated `LaunchedEffect(Unit)` in [`ReportScreen.kt`](file:///d:/kotlin/InterviewAI/frontend/app/src/main/java/com/example/interview_ai/ui/screens/report/ReportScreen.kt) to dispatch `loadEvaluationReport()` whenever the destination is opened. 
  - This solves the static caching bug where returning to the evaluation screen showed cached performance data from previous mock loops instead of the newly graded transcript.
- **Dynamic Performance Verdicts**:
  - Replaced the hardcoded static verdict string with conditional statements that evaluate the overall score (e.g., exceptional status for >= 85, active action plan details for 70-85, and needs practice directives for under 70).
- **Gson Deserialization Safeguards**:
  - Changed API response definitions of `confirmResume`, `deleteResume`, and `logout` inside [`InterviewApiService.kt`](file:///d:/kotlin/InterviewAI/frontend/app/src/main/java/com/example/interview_ai/data/api/InterviewApiService.kt) to return `okhttp3.ResponseBody`. This prevents Gson deserialization crashes from unstructured plain string responses.

---

### Milestone 18 — Adaptive Voice Interview Redesign ✅

Complete architectural overhaul of the mock interview experience from a static question-list format to a **real-time, voice-driven, adaptive AI interviewer**.

#### New Interview Workflow
```
Dashboard → Upload Resume → Start Interview
  → AI Greeting (TTS)
    → AI Asks First Question (generated from resume via Gemini)
      → User Speaks Answer (STT captures in real-time)
        → 5-second Silence Detected → Auto-Submit Answer
          → Gemini Evaluates Answer & Generates Adaptive Follow-Up
            → Loop (5–10 questions)
              → Final Evaluation → Report Screen
```

#### Backend — New Adaptive Endpoints
- **`POST /api/interview/start`**: Accepts `resumeText` and `targetRole`. Gemini generates the **first interview question** tailored to the candidate's resume and target position. Returns `{ sessionId, question, questionNumber, totalQuestions }`.
- **`POST /api/interview/next-question`**: Accepts the full `conversationHistory` (previous questions + answers). Gemini analyzes depth, coverage gaps, and technical accuracy to generate the **next adaptive follow-up question**. Returns `{ question, questionNumber, totalQuestions, isComplete }`.
- **`POST /api/interview/evaluate`**: Accepts the complete conversation transcript. Gemini produces a multi-dimensional evaluation report with scores, strengths, weaknesses, and an action plan. The report is saved to MongoDB.
- Removed deprecated **`POST /api/interview/generate`** endpoint (predefined question lists).

#### Frontend — State Machine Architecture
- **`InterviewState` enum** in [`InterviewUiState.kt`](file:///d:/kotlin/InterviewAI/frontend/app/src/main/java/com/example/interview_ai/data/model/InterviewUiState.kt):
  - `IDLE` → `GREETING` → `AI_SPEAKING` → `LISTENING` → `SILENCE_DETECTION` → `PROCESSING` → `AI_THINKING` → loop or `COMPLETED`
- **[`InterviewViewModel.kt`](file:///d:/kotlin/InterviewAI/frontend/app/src/main/java/com/example/interview_ai/viewmodel/InterviewViewModel.kt)** rewritten:
  - Coroutine-based 5-second silence debouncer: every partial STT transcription resets a countdown. When 5 seconds of silence pass, the answer is auto-submitted without any button press.
  - State-driven loop cycles through TTS playback → STT recording → API submission → next question fetch.
  - Conversation history maintained in-memory for Gemini context.
- **[`InterviewScreen.kt`](file:///d:/kotlin/InterviewAI/frontend/app/src/main/java/com/example/interview_ai/ui/screens/interview/InterviewScreen.kt)** redesigned:
  - Removed question card lists and chat bubbles.
  - Central glowing audio visualizer orb with state-dependent colors (Indigo = AI speaking, Green = listening, Amber = silence detection, Teal = thinking).
  - Live transcript preview showing real-time STT output.
  - Progress indicators and session timer.
- **[`InterviewApiService.kt`](file:///d:/kotlin/InterviewAI/frontend/app/src/main/java/com/example/interview_ai/data/api/InterviewApiService.kt)** updated:
  - Added `StartInterviewRequest`, `StartInterviewResponse`, `NextQuestionRequest`, `NextQuestionResponse`, `ConversationItem` DTOs.
  - Removed deprecated `GenerateQuestionsRequest` model.

#### Backend — Service Layer
- **[`geminiService.js`](file:///d:/kotlin/InterviewAI/backend/services/geminiService.js)** extended with:
  - `generateFirstQuestionWithAI(resumeText, targetRole)` — prompts Gemini to craft an opening interview question.
  - `generateNextQuestionWithAI(conversationHistory, resumeText, targetRole)` — prompts Gemini with full conversation context for adaptive follow-ups.
  - All AI calls wrapped in try-catch with graceful fallback questions.

---

### Milestone 19 — Authentication Security Fix ✅

- **Removed Offline Authentication Bypass**:
  - Both `login()` and `register()` in [`AuthViewModel.kt`](file:///d:/kotlin/InterviewAI/frontend/app/src/main/java/com/example/interview_ai/viewmodel/AuthViewModel.kt) previously had an "offline fallback" that silently created a fake `User(id = "offline_user")` and granted full app access whenever the backend server was unreachable.
  - This meant **any email/password combination would successfully log in** if the server was down — a critical security flaw.
  - **Fix**: Removed the offline fallback entirely. Non-HTTP exceptions (connection timeouts, server unreachable) now display: *"Unable to connect to server. Please check your internet connection and try again."* without granting access.
- **Backend Auth Verification** (already correct, confirmed):
  - `POST /api/auth/login`: Checks user existence via `User.findOne({ email })`, validates password with `bcrypt.compare()`, returns 400 with specific error messages on failure.
  - `POST /api/auth/register`: Checks for duplicate emails before creating user, hashes password with `bcrypt.genSalt(10)`.

---

## 8. Coding Rules & Conventions

These rules are enforced throughout the codebase and should be maintained by any contributor or AI agent:

1. **Never sacrifice architecture for speed** — every change should be scalable and clean.
2. **Reuse components** — never duplicate UI code. Use the existing component library (`PrimaryButton`, `SecondaryButton`, `AppTextField`, `SurfaceCard`, `AppTopBar`).
3. **Keep composables small** — each composable should do one thing well.
4. **Follow MVVM strictly** — UI state lives in ViewModels, screens observe state, screens never hold business logic.
5. **Never hardcode strings or colors** — use theme tokens (`Color.kt`, `Dimensions.kt`, `Type.kt`) and string resources.
6. **Keep navigation centralized** — all routes are in `Routes.kt`, all wiring is in `NavGraph.kt`.
7. **Write production-level code** — proper error handling, meaningful variable names, consistent formatting.
8. **Use design tokens** — always reference `AppSpacing`, `AppRadius`, `AppIconSize` instead of raw `dp` values.
9. **Dark theme only** — the app enforces a custom `darkColorScheme`. Never add light theme support unless explicitly requested.
10. **Inter font only** — all text uses the `InterFontFamily` defined in `Type.kt` through the Material 3 `Typography` object.

---

## 9. Build & Dependency Info

- **Gradle**: 9.1.0
- **AGP**: 8.11.1
- **Kotlin**: 2.1.21
- **Compose BOM**: 2025.06.01
- **Navigation Compose**: 2.9.3 (hardcoded in `app/build.gradle.kts`)
- **Retrofit & OkHttp**: 2.11.0 / 4.12.0
- **DataStore Preferences**: 1.1.1

---

## 10. How to Run

### 1. Boot Backend Server
1. Navigate to the `server/` directory:
   ```bash
   cd server
   ```
2. Configure your Gemini API Key in the `.env` file (`GEMINI_API_KEY=YOUR_KEY`).
3. Install dependencies and start the Node.js server:
   ```bash
   npm install
   npm start
   ```
4. The server launches on port 5000: `InterviewAI Server launched on port 5000`.

### 2. Run Android App
1. Open the project in **Android Studio**.
2. Sync Gradle.
3. Connect an Android device or start an emulator (API 24+).
4. Run `app` configuration. The app connects to the local Node.js server over the `10.0.2.2:5000` emulator network bridge.

---

## 11. Current App Flow

```
App Launch
  └── SplashScreen (animated logo, 2-second delay)
        └── LoginScreen (JWT auth required — no offline bypass)
              ├── Sign In / Sign Up (bcrypt password validation on NodeJS backend)
              └── Dashboard
                    ├── Resume Picker & Parser (AI skill tags rendering)
                    ├── Session History (filter and search previous reports)
                    └── Start Mock Interview
                          └── Voice Interview Session (adaptive AI interviewer)
                                ├── AI asks questions via TTS (generated by Gemini from resume)
                                ├── User speaks answers via STT (5-second silence auto-submit)
                                ├── Gemini generates adaptive follow-up questions
                                └── Session Complete → Evaluation Report (score dimensions & action plans)
```
