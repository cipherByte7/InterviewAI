# 📂 Project Structure

```text
InterviewAI
│
├── backend
│   ├── config/
│   ├── controllers/
│   │   ├── authController.js
│   │   ├── interviewController.js
│   │   ├── reportController.js
│   │   └── resumeController.js
│   │
│   ├── middleware/
│   │   ├── authMiddleware.js
│   │   └── uploadMiddleware.js
│   │
│   ├── models/
│   │   ├── User.js
│   │   └── Report.js
│   │
│   ├── routes/
│   │   ├── authRoutes.js
│   │   ├── interviewRoutes.js
│   │   ├── reportRoutes.js
│   │   └── resumeRoutes.js
│   │
│   ├── services/
│   │   └── aiService.js
│   │
│   ├── uploads/
│   ├── .env
│   ├── package.json
│   └── server.js
│
├── frontend
│   └── app
│       └── src
│           └── main
│               ├── java
│               │   └── com
│               │       └── example
│               │           └── interview_ai
│               │               ├── data
│               │               │   ├── api/
│               │               │   ├── datastore/
│               │               │   ├── model/
│               │               │   └── preferences/
│               │               │
│               │               ├── theme/
│               │               │
│               │               ├── ui
│               │               │   ├── components/
│               │               │   ├── navigation/
│               │               │   └── screens/
│               │               │       ├── auth/
│               │               │       ├── dashboard/
│               │               │       ├── history/
│               │               │       ├── interview/
│               │               │       ├── profile/
│               │               │       ├── report/
│               │               │       └── splash/
│               │               │
│               │               ├── utils/
│               │               │   ├── SpeechToTextEngine.kt
│               │               │   └── TextToSpeechEngine.kt
│               │               │
│               │               ├── viewmodel/
│               │               │
│               │               └── MainActivity.kt
│               │
│               └── res/
│                   ├── drawable/
│                   ├── mipmap/
│                   └── values/
│
├── screenshots
│   ├── splash.jpg
│   ├── login.jpg
│   ├── register.jpg
│   ├── dashboard.jpg
│   ├── resume_upload.jpg
│   ├── configure_interview.jpg
│   ├── interview.jpg
│   ├── report.jpg
│   ├── history.jpg
│   └── profile.jpg
│
├── README.md
├── LICENSE
└── .gitignore
```