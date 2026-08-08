# 🎙️ InterviewAI – AI Powered Mock Interview Platform

<div align="center">

![Android](https://img.shields.io/badge/Android-Jetpack%20Compose-3DDC84?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin)
![Node.js](https://img.shields.io/badge/Backend-Node.js-339933?logo=node.js)
![Express](https://img.shields.io/badge/Framework-Express.js-000000?logo=express)
![MongoDB](https://img.shields.io/badge/Database-MongoDB-47A248?logo=mongodb)
![OpenRouter](https://img.shields.io/badge/AI-OpenRouter-blue)
![Render](https://img.shields.io/badge/Deployment-Render-46E3B7)

### An AI-powered Android application that conducts adaptive voice-based mock interviews using resume-aware question generation and AI-generated performance evaluation.

</div>

---

# 🚀 Key Highlights

- 🤖 AI-powered adaptive mock interviews
- 📄 Resume-aware question generation using AI
- 🎤 Speech-to-Text + Text-to-Speech interview experience
- 📊 AI-generated interview evaluation and action plan
- ☁️ Cloud backend deployed on Render
- 🗄️ MongoDB Atlas for persistent cloud storage
- 🔐 JWT Authentication
- 📚 Interview history with detailed evaluation reports
- 🎨 Modern Material 3 UI with Light & Dark Theme support

---

# 📱 Screenshots

> All screenshots are captured in the application's Dark Theme.

| Splash | Login | Register |
|---------|--------|----------|
| ![](screenshots/splash.png) | ![](screenshots/login.png) | ![](screenshots/register.png) |

| Dashboard | Resume Upload | Interview Configuration |
|------------|---------------|-------------------------|
| ![](screenshots/dashboard.png) | ![](screenshots/upload_resume.png) | ![](screenshots/configure_interview.png) |

| Live Interview | AI Evaluation | History |
|----------------|---------------|---------|
| ![](screenshots/interview.png) | ![](screenshots/report.png) | ![](screenshots/history.png) |

| Profile |
|----------|
| ![](screenshots/profile.png) |

---

# ✨ Features

## 🔐 Authentication

- Secure User Registration
- JWT Login Authentication
- Persistent User Sessions
- Secure API Authorization

---

## 📄 Resume Intelligence

- Upload Resume (PDF)
- AI Resume Parsing
- Automatic Skill Extraction
- Target Role Identification
- Resume-aware Interview Generation

---

## 🎤 AI Mock Interview

- Voice-based Interview Experience
- Speech-to-Text Transcription
- Text-to-Speech AI Interviewer
- Adaptive Follow-up Questions
- Technical Interview Mode
- Behavioral Interview Mode
- Mixed Interview Mode
- Configurable Difficulty Levels
- Configurable Number of Questions

---

## 📊 AI Evaluation Report

Every interview generates a detailed AI evaluation including:

- Overall Performance Score
- Technical Knowledge
- Communication Skills
- Confidence
- Speaking Fluency
- Resume Match
- Strengths
- Weaknesses
- Personalized AI Action Plan

---

## 📚 Interview History

- View Previous Interviews
- Search Interview Sessions
- Filter by Interview Type
- Open Detailed Reports
- Persistent Cloud Storage

---

## 🎨 Modern UI

- Material 3 Design
- Light & Dark Theme
- Responsive Layout
- Animated Interview Orb
- Glassmorphism Inspired Cards
- Modern Dashboard
- Smooth Navigation

---

# 🏗️ System Architecture

```
                     Android Application
                             │
                             │
                       Retrofit REST API
                             │
                    Node.js + Express Server
                             │
       ┌─────────────────────┴──────────────────────┐
       │                                            │
       │                                            │
MongoDB Atlas                               OpenRouter AI
       │                                            │
       │                                            │
Users                                      Resume Parsing
Reports                                    Question Generation
Interview History                          Interview Evaluation
```

---

# 🛠️ Tech Stack

## Android

- Kotlin
- Jetpack Compose
- MVVM Architecture
- StateFlow
- Kotlin Coroutines
- Retrofit
- Material 3

---

## Backend

- Node.js
- Express.js
- JWT Authentication
- Multer
- Mongoose
- MongoDB Atlas

---

## Artificial Intelligence

- OpenRouter API
- Resume Parsing
- Adaptive Question Generation
- AI Performance Evaluation

---

## Deployment

- Render
- MongoDB Atlas

---

# 📂 Project Structure

```
InterviewAI

├── frontend
│
│   ├── data
│   ├── navigation
│   ├── theme
│   ├── ui
│   ├── utils
│   └── viewmodel
│
├── backend
│
│   ├── config
│   ├── controllers
│   ├── middleware
│   ├── models
│   ├── routes
│   ├── services
│   └── server.js
│
├── screenshots
│
└── README.md
```

---

# 🚀 Getting Started

## Clone Repository

```bash
git clone https://github.com/cipherByte7/InterviewAI.git

cd InterviewAI
```

---

## Backend Setup

```bash
cd backend

npm install
```

Create a `.env` file:

```env
MONGO_URI=YOUR_MONGODB_CONNECTION_STRING
JWT_SECRET=YOUR_SECRET_KEY
OPENROUTER_API_KEY=YOUR_API_KEY
AI_MODEL=qwen/qwen3-30b-a3b
```

Run the backend

```bash
npm start
```

---

## Android Setup

Open the project inside Android Studio.

Update the Retrofit base URL:

```kotlin
private const val BASE_URL = "YOUR_BACKEND_URL/"
```

Run the application on an emulator or physical Android device.

---

# 📡 REST API

## Authentication

```
POST /api/auth/register

POST /api/auth/login
```

---

## Resume

```
POST /api/resume/upload

POST /api/resume/parse
```

---

## Interview

```
POST /api/interview/start

POST /api/interview/next

POST /api/interview/evaluate

GET /api/interview/history

GET /api/interview/report/:id
```

---

# 🧠 AI Workflow

```
Resume Upload
        │
        ▼
Resume Parsing
        │
        ▼
Skill Extraction
        │
        ▼
Generate First Question
        │
        ▼
Voice Interview
        │
        ▼
Adaptive Question Generation
        │
        ▼
Interview Completion
        │
        ▼
AI Performance Evaluation
        │
        ▼
Store Report in MongoDB
        │
        ▼
History Screen
```

---

# 🌟 Future Improvements

- HR Interview Mode
- Coding Interview Mode
- Company-specific Interview Sets
- AI Voice Emotion Analysis
- PDF Report Export
- Leaderboards
- ATS Resume Score
- Multi-language Support
- Interview Recording Playback
- Analytics Dashboard

---

# 👨‍💻 Author

**Aaditya Chitale**

- GitHub: https://github.com/cipherByte7
- LinkedIn: www.linkedin.com/in/aaditya-chitale-41287528b

---

# 📄 License

This project is licensed under the MIT License.

---

# ⭐ Support

If you found this project useful, please consider giving it a ⭐ on GitHub.

It helps others discover the project and motivates future development.
