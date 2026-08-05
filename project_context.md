# InterviewAI — Project Context & Handoff Document

> **Last Updated**: August 5, 2026
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
├── models/
│   ├── User.js                     # MongoDB Mongoose User schema definition
│   └── Report.js                   # MongoDB Mongoose AI Evaluation report schema
├── middleware/
│   └── auth.js                     # JWT token validation middleware interceptor
├── package.json                    # Npm metadata and dependencies config
├── .env                            # Host config parameters, Mongo URI, Gemini API Key
└── server.js                       # Express routing and controller logic triggers
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
│       └── history/
│           └── HistoryScreen.kt   # Previous sessions lists and search filters
│
├── utils/
│   ├── TextToSpeechEngine.kt       # Native TextToSpeech player wrapper
│   └── SpeechToTextEngine.kt       # Native SpeechRecognizer recorders
│
└── viewmodel/
    ├── AuthViewModel.kt           # Controls user authentications and offline bypasses
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

### Phase 10 — Reports & History ✅
- **Evaluation Dashboard**: Built comprehensive per-session perfor## 7. Features Completed (Post-MVP Integrations) ✅
- **Node.js Express Backend**: Fully operational HTTP REST API server running on port 5000 with MongoDB user data storage.
- **Gemini API prompting**: Structured generative models parsing resumes, tailoring technical interview questions, and grading transcript evaluations.
- **Retrofit Networking client**: Active authorization headers injection, mapping request payloads, and executing async API queries.
- **Jetpack DataStore preferences**: Local user authentication JWT session storage and persistent profile management.
- **Native Android voice speech**: Dynamic TTS audios reading questions and real-time partial voice STT transcript recordings.

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
        └── LoginScreen
              ├── Sign In / Sign Up (authenticated via JWT tokens on NodeJS backend)
              └── Dashboard
                    ├── Resume Picker & Parser (AI skill tags rendering)
                    ├── Session History (filter and search previous reports)
                    └── Interview Setup
                          └── Prep Screen (questions review list)
                                └── Active Mock Interview (pulsing visualizer, TTS/STT speaking loops)
                                      └── Evaluation Report Dashboard (score dimensions & action plans)
```
