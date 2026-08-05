require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const cors = require('cors');
const { GoogleGenAI } = require('@google/generative-ai');

// Import Models & Middleware
const User = require('./models/User');
const Report = require('./models/Report');
const auth = require('./middleware/auth');

const app = express();
app.use(express.json());
app.use(cors());

// In-Memory Database Fallbacks if MongoDB is unavailable
const memoryUsers = [];
const memoryReports = [];
let isMongoConnected = false;

// Connect to MongoDB with graceful local fallback
mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/interview_ai')
  .then(() => {
    console.log('MongoDB Connected successfully.');
    isMongoConnected = true;
  })
  .catch(err => {
    console.warn('MongoDB connection failed. Falling back to In-Memory DB cache.');
    console.warn(err.message);
  });

// Initialize Gemini API
let genAI = null;
if (process.env.GEMINI_API_KEY) {
  try {
    // Initializing new Gemini SDK client
    const { GoogleGenerativeAI } = require('@google/generative-ai');
    genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
    console.log('Gemini API Engine initialized.');
  } catch (e) {
    console.warn('Failed to initialize Gemini API. Using local mock generator instead.');
  }
} else {
  console.warn('No GEMINI_API_KEY configured in .env. Falling back to local simulated AI generator.');
}

// ----------------------------------------------------
// AUTHENTICATION ENDPOINTS
// ----------------------------------------------------

app.post('/api/auth/register', async (req, res) => {
  const { name, email, password, targetRole } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({ msg: 'Please enter all required fields' });
  }

  try {
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    let user;
    if (isMongoConnected) {
      const existingUser = await User.findOne({ email });
      if (existingUser) return res.status(400).json({ msg: 'User already exists' });

      user = new User({ name, email, password: hashedPassword, targetRole: targetRole || 'Android Developer' });
      await user.save();
    } else {
      const existingUser = memoryUsers.find(u => u.email === email);
      if (existingUser) return res.status(400).json({ msg: 'User already exists' });

      user = {
        _id: new mongoose.Types.ObjectId().toString(),
        name,
        email,
        password: hashedPassword,
        targetRole: targetRole || 'Android Developer',
        date: new Date()
      };
      memoryUsers.push(user);
    }

    const token = jwt.sign({ userId: user._id.toString() }, process.env.JWT_SECRET || 'super_secret_interview_key_123', { expiresIn: '7d' });
    res.json({
      token,
      user: { id: user._id.toString(), name: user.name, email: user.email, targetRole: user.targetRole }
    });
  } catch (err) {
    res.status(500).json({ msg: 'Server registration error', error: err.message });
  }
});

app.post('/api/auth/login', async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ msg: 'Please enter all fields' });
  }

  try {
    let user;
    if (isMongoConnected) {
      user = await User.findOne({ email });
    } else {
      user = memoryUsers.find(u => u.email === email);
    }

    if (!user) return res.status(400).json({ msg: 'User does not exist' });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(400).json({ msg: 'Invalid credentials' });

    const token = jwt.sign({ userId: user._id.toString() }, process.env.JWT_SECRET || 'super_secret_interview_key_123', { expiresIn: '7d' });
    res.json({
      token,
      user: { id: user._id.toString(), name: user.name, email: user.email, targetRole: user.targetRole }
    });
  } catch (err) {
    res.status(500).json({ msg: 'Server login error', error: err.message });
  }
});

app.get('/api/auth/user', auth, async (req, res) => {
  try {
    let user;
    if (isMongoConnected) {
      user = await User.findById(req.userId).select('-password');
    } else {
      const match = memoryUsers.find(u => u._id.toString() === req.userId);
      if (match) {
        user = { id: match._id, name: match.name, email: match.email, targetRole: match.targetRole };
      }
    }

    if (!user) return res.status(404).json({ msg: 'User not found' });
    res.json(user);
  } catch (err) {
    res.status(500).json({ msg: 'Server fetch error' });
  }
});

// ----------------------------------------------------
// RESUME PARSING ENDPOINT
// ----------------------------------------------------

app.post('/api/resume/parse', auth, async (req, res) => {
  const { resumeText, fileName } = req.body;
  
  // Standard fallback parsed data
  const fallbackParsed = {
    parsedRole: "Senior Android Dev",
    experienceYears: 3,
    skills: ["Kotlin", "Jetpack Compose", "Coroutines", "Dagger Hilt", "Clean Architecture"],
    education: "B.Tech in Computer Science",
    projectsCount: 4,
    isConfirmed: false
  };

  if (!genAI || !resumeText) {
    // If Gemini is not set or input is mock, return beautiful mockup data
    await new Promise(resolve => setTimeout(resolve, 1000));
    return res.json(fallbackParsed);
  }

  try {
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    const prompt = `Analyze this extracted resume text and return structured details strictly in JSON format. The JSON must match this structure:
    {
      "parsedRole": "Role name e.g. Senior Android Dev",
      "experienceYears": 3,
      "skills": ["Skill1", "Skill2", "Skill3"],
      "education": "Education e.g. B.Tech in CS",
      "projectsCount": 4
    }
    Resume text: ${resumeText}`;

    const result = await model.generateContent(prompt);
    const responseText = result.response.text();
    
    // Clean code fences if output is wrapped in ```json
    const cleanJson = responseText.replace(/```json|```/g, '').trim();
    const parsedData = JSON.parse(cleanJson);
    res.json({ ...parsedData, isConfirmed: false });
  } catch (err) {
    console.error('Gemini Resume parsing failed, falling back to mock details.', err.message);
    res.json(fallbackParsed);
  }
});

// ----------------------------------------------------
// INTERVIEW QUESTION GENERATION
// ----------------------------------------------------

app.post('/api/interview/generate', auth, async (req, res) => {
  const { targetRole, skills, count, category, difficulty } = req.body;
  const qCount = count || 5;
  const qCat = category || 'Technical';
  const qDiff = difficulty || 'Mid-Level';
  const role = targetRole || 'Android Developer';
  const skillList = skills || ['Kotlin', 'Android SDK'];

  // Local generator fallback
  const generateLocalMock = () => {
    const technicalQuestions = [
      "Explain the difference between launch and async in Kotlin Coroutines. When would you use each?",
      "What is Jetpack Compose Recomposition? How can you optimize a Composable to prevent unnecessary recompositions?",
      "How does ViewModel survive configuration changes under the hood? Explain the role of ViewModelStore.",
      "Explain clean architecture layers in Android. Why should the domain layer be decoupled from framework details?",
      "What are Kotlin StateFlow and SharedFlow? Describe a scenario where SharedFlow is preferred over StateFlow.",
      "How do you implement dependency injection in Android using Dagger Hilt? What are Scopes and Components?",
      "Describe the lifecycle steps of a Composable function. What is SideEffect and DisposableEffect?",
      "What is Android's garbage collection mechanism, and how do you profile and debug memory leaks using LeakCanary?"
    ];

    const behavioralQuestions = [
      "Tell me about a challenging Android bug you faced in a project. How did you diagnose and resolve it?",
      "Describe a situation where you had to work with a teammate who had a conflicting technical perspective. How did you handle it?",
      "How do you manage deadlines when multiple high-priority tasks are assigned to you simultaneously?",
      "Tell me about a project where you implemented a new technology or architecture. What was the learning curve like?",
      "Describe a time when you received constructive feedback on your code quality. What steps did you take to address it?"
    ];

    const results = [];
    const pool = qCat === 'Behavioral' ? behavioralQuestions : technicalQuestions;

    for (let i = 0; i < qCount; i++) {
      const baseQ = pool[i % pool.length];
      results.push({
        id: (i + 1).toString(),
        text: baseQ,
        category: qCat,
        estimatedTimeMinutes: 2
      });
    }
    return results;
  };

  if (!genAI) {
    await new Promise(resolve => setTimeout(resolve, 800));
    return res.json(generateLocalMock());
  }

  try {
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    const prompt = `Generate exactly ${qCount} interview questions for the role: ${role}.
    Difficulty Level: ${qDiff}
    Category Focus: ${qCat}
    Extracted skills from candidate: ${skillList.join(', ')}
    
    Return the response strictly as a JSON array matching this format:
    [
      {
        "id": "1",
        "text": "The full text of the interview question.",
        "category": "${qCat}",
        "estimatedTimeMinutes": 2
      }
    ]`;

    const result = await model.generateContent(prompt);
    const responseText = result.response.text();
    const cleanJson = responseText.replace(/```json|```/g, '').trim();
    const questionsList = JSON.parse(cleanJson);
    res.json(questionsList);
  } catch (err) {
    console.error('Gemini generation failed, using mock generator.', err.message);
    res.json(generateLocalMock());
  }
});

// ----------------------------------------------------
// INTERVIEW EVALUATION & HISTORY
// ----------------------------------------------------

app.post('/api/interview/evaluate', auth, async (req, res) => {
  const { duration, transcript, role } = req.body;
  const activeRole = role || 'Senior Android Developer';

  const localFeedbackMock = {
    role: activeRole,
    date: new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }),
    overallScore: 86,
    duration: duration || "08:45",
    dimensions: [
      { title: "Technical Accuracy", score: 88, description: "Strong understanding of Kotlin Coroutines launch/async concepts and Compose lifecycle recomposition steps." },
      { title: "Communication Clarity", score: 85, description: "Articulation was direct and concise. Avoided rambling and focused on engineering design trade-offs." },
      { title: "Depth of Knowledge", score: 90, description: "Detailed knowledge of ViewModelStore configuration change behaviors under the hood." },
      { title: "Confidence & Articulation", score: 82, description: "Spoke clearly but paced a bit quickly during advanced system architecture questions." }
    ],
    strengths: [
      "Accurately mapped the difference between fire-and-forget (launch) and result-seeking (async) concurrency triggers.",
      "Described memory leak debugging flows detailing how LeakCanary isolates heap dumps.",
      "Properly illustrated clean architecture layers decoupling domain layer models."
    ],
    weaknesses: [
      "Could expand further on recomposition optimization strategies using @Stable annotations.",
      "Pacing was slightly hurried during discussions about Dagger Hilt Scopes."
    ],
    suggestion: "Practice speaking at a steadier cadence. Review Jetpack Compose stability optimizations and learn how to write custom Compose compiler stabilization rules."
  };

  const saveReport = async (reportData) => {
    if (isMongoConnected) {
      const dbReport = new Report({
        userId: req.userId,
        ...reportData
      });
      await dbReport.save();
      return dbReport;
    } else {
      const memoryReport = {
        _id: new mongoose.Types.ObjectId().toString(),
        userId: req.userId,
        ...reportData
      };
      memoryReports.push(memoryReport);
      return memoryReport;
    }
  };

  if (!genAI || !transcript) {
    await new Promise(resolve => setTimeout(resolve, 1500));
    const saved = await saveReport(localFeedbackMock);
    return res.json(saved);
  }

  try {
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    const prompt = `Analyze this transcript of questions and answers from a mock interview.
    Role: ${activeRole}
    Transcript details:
    ${JSON.stringify(transcript)}
    
    Evaluate the candidate and return response strictly in JSON matching this structure:
    {
      "overallScore": 86,
      "dimensions": [
        { "title": "Technical Accuracy", "score": 88, "description": "Specific feedback detail" },
        { "title": "Communication Clarity", "score": 85, "description": "Specific feedback detail" },
        { "title": "Depth of Knowledge", "score": 90, "description": "Specific feedback detail" },
        { "title": "Confidence & Articulation", "score": 82, "description": "Specific feedback detail" }
      ],
      "strengths": ["Strength detail 1", "Strength detail 2"],
      "weaknesses": ["Improvement detail 1", "Improvement detail 2"],
      "suggestion": "Actionable overall plan instructions..."
    }`;

    const result = await model.generateContent(prompt);
    const responseText = result.response.text();
    const cleanJson = responseText.replace(/```json|```/g, '').trim();
    const parsedReport = JSON.parse(cleanJson);
    
    const saved = await saveReport({
      role: activeRole,
      duration: duration || "08:45",
      ...parsedReport
    });
    res.json(saved);
  } catch (err) {
    console.error('Gemini Evaluation failed, using local mock.', err.message);
    const saved = await saveReport(localFeedbackMock);
    res.json(saved);
  }
});

app.get('/api/interview/history', auth, async (req, res) => {
  try {
    let reports;
    if (isMongoConnected) {
      reports = await Report.find({ userId: req.userId }).sort({ _id: -1 });
    } else {
      reports = memoryReports.filter(r => r.userId === req.userId).reverse();
    }
    res.json(reports);
  } catch (err) {
    res.status(500).json({ msg: 'Server fetch error' });
  }
});

// Start Server
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`InterviewAI Server launched on port ${PORT}`));
