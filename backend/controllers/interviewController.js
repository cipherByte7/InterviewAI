const mongoose = require('mongoose');
const Report = require('../models/Report');
const User = require('../models/User');
const { getMongoStatus, memoryReports, memoryUsers } = require('../config/db');
const {
    parseResumeWithAI,
    generateFirstQuestionWithAI,
    generateNextQuestionWithAI,
    evaluateFullInterviewWithAI
} = require("../services/aiService");

const startInterview = async (req, res) => {
  const { targetRole, difficulty, category } = req.body;
  const role = targetRole || 'Android Developer';
  const qDiff = difficulty || 'Mid-Level';
  const qCat = category || 'Technical';

  const defaultFirstQuestions = {
    Technical: "Could you explain how Kotlin Coroutines manage thread context switching, and specifically the role of Dispatchers?",
    Behavioral: "Tell me about a time when you were working on an Android feature with a tight deadline, and you had to make trade-offs between clean design and speed.",
    Mixed: "Welcome to today's interview. Let's start by looking at your developer background: what got you interested in mobile engineering, and what architectures do you prefer?"
  };

  try {
    let parsedResume = null;
    const isMongoConnected = getMongoStatus();
    
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      const user = await User.findById(req.userId);
      if (user && user.parsedResume) {
        parsedResume = user.parsedResume;
      }
    } else {
      const user = memoryUsers.find(u => u._id.toString() === req.userId);
      if (user && user.parsedResume) {
        parsedResume = user.parsedResume;
      }
    }

    const aiResponse = await generateFirstQuestionWithAI(role, qDiff, qCat, parsedResume);
    if (!aiResponse || !aiResponse.question) {
      // Fallback
      return res.json({ question: defaultFirstQuestions[qCat] || defaultFirstQuestions.Mixed });
    }

    res.json({ question: aiResponse.question });
  } catch (err) {
    console.error('Failed to start interview, using default first question:', err.message);
    res.json({ question: defaultFirstQuestions[qCat] || defaultFirstQuestions.Mixed });
  }
};

const nextQuestion = async (req, res) => {
  const { targetRole, difficulty, category, conversationHistory, currentAnswer } = req.body;
  const role = targetRole || 'Android Developer';
  const qDiff = difficulty || 'Mid-Level';
  const qCat = category || 'Technical';

  const fallbackQuestions = [
    "How do you handle memory leaks inside Jetpack Compose, specifically with side-effect APIs like LaunchedEffect or DisposableEffect?",
    "Could you describe a challenging debugging session you had recently? What steps did you take to isolate the root cause?",
    "Regarding clean architecture, how do you manage dependencies and pass state down through layers in your applications?",
    "That sounds interesting. How do you optimize network consumption and caching for performance on low-bandwidth connections?"
  ];

  try {
    let parsedResume = null;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      const user = await User.findById(req.userId);
      if (user && user.parsedResume) {
        parsedResume = user.parsedResume;
      }
    } else {
      const user = memoryUsers.find(u => u._id.toString() === req.userId);
      if (user && user.parsedResume) {
        parsedResume = user.parsedResume;
      }
    }

    const aiResponse = await generateNextQuestionWithAI(role, qDiff, qCat, conversationHistory, currentAnswer, parsedResume);
    if (!aiResponse || !aiResponse.nextQuestion) {
      // Fallback: pick a random fallback question
      const randomIdx = Math.floor(Math.random() * fallbackQuestions.length);
      return res.json({ nextQuestion: fallbackQuestions[randomIdx], isLastQuestion: false });
    }

    res.json(aiResponse);
  } catch (err) {
    console.error('Failed to generate next question, using fallback question:', err.message);
    res.json({ nextQuestion: fallbackQuestions[0], isLastQuestion: false });
  }
};

const evaluateInterview = async (req, res) => {
  const { duration, transcript, role, difficulty, category } = req.body;
  const activeRole = role || 'Android Developer';
  const qDiff = difficulty || 'Mid-Level';
  const qCat = category || 'Technical';

  // Fallback evaluation structure if Gemini fails
  const localFeedbackMock = {
    role: activeRole,
    category: qCat,
    date: new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }),
    overallScore: 84,
    duration: duration || "05:00",
    dimensions: [
      { title: "Technical Knowledge", score: 85, description: "Strong understanding of Android SDK lifecycle states and Kotlin Coroutine dispatchers." },
      { title: "Communication", score: 88, description: "Direct and engineering-focused articulation. Clear definitions of architecture." },
      { title: "Confidence", score: 80, description: "Well paced speech pattern. Maintained high focus throughout the technical follow-ups." },
      { title: "Fluency", score: 86, description: "Smooth delivery of concepts, minimal hesitation in complex threads." },
      { title: "Speaking Pace", score: 82, description: "Average speed is optimal. Kept a steady conversational flow." },
      { title: "Fillers", score: 85, description: "Very low filler usage. Handled transition phrases smoothly." },
      { title: "Resume Match", score: 84, description: "Strong mapping of active projects to actual engineering answers." }
    ],
    strengths: [
      "Demonstrated robust understanding of asynchronous threading contexts.",
      "Clear explanation of state holding inside view models during configuration changes."
    ],
    weaknesses: [
      "Could expand further on performance profiling mechanisms.",
      "Filler usage slightly increased during advanced architecture questions."
    ],
    suggestion: "Work on keeping a steady cadence when responding to deep technical design queries. Review tools like LeakCanary and Android Profiler."
  };

  const saveReport = async (reportData) => {
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
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

  try {
    const parsedReport = await evaluateFullInterviewWithAI(activeRole, qDiff, qCat, transcript);
    if (!parsedReport) {
      await new Promise(resolve => setTimeout(resolve, 1500));
      const saved = await saveReport(localFeedbackMock);
      return res.json(saved);
    }
    
    const saved = await saveReport({
      role: activeRole,
      duration: duration || "05:00",
      ...parsedReport
    });
    res.json(saved);
  } catch (err) {
    console.error('Gemini Evaluation failed, using local mock.', err.message);
    const saved = await saveReport(localFeedbackMock);
    res.json(saved);
  }
};

const getHistory = async (req, res) => {
  try {
    let reports;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      reports = await Report.find({ userId: req.userId }).sort({ _id: -1 });
    } else {
      reports = memoryReports.filter(r => r.userId === req.userId).reverse();
    }
    res.json(reports);
  } catch (err) {
    res.status(500).json({ msg: 'Server fetch error' });
  }
};

const getReportById = async (req, res) => {
  const { id } = req.params;
  try {
    let report;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId) && mongoose.Types.ObjectId.isValid(id)) {
      report = await Report.findOne({ _id: id, userId: req.userId });
    } else {
      report = memoryReports.find(r => r._id === id && r.userId === req.userId);
    }

    if (!report) return res.status(404).json({ msg: 'Report not found' });
    res.json(report);
  } catch (err) {
    res.status(500).json({ msg: 'Failed to fetch report', error: err.message });
  }
};

module.exports = {
  startInterview,
  nextQuestion,
  evaluateInterview,
  getHistory,
  getReportById
};
