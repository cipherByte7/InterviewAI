const mongoose = require('mongoose');
const { PDFParse } = require('pdf-parse');  // v2.x exports a class, not a function
const User = require('../models/User');
const { getMongoStatus, memoryUsers } = require('../config/db');
const { parseResumeWithAI } = require('../services/aiService');

// Fallback data shown when AI parsing fails or PDF text is unreadable
const fallbackParsed = {
  parsedRole: 'Software Developer',
  experienceYears: 1,
  skills: ['JavaScript', 'React', 'Node.js', 'Git', 'REST APIs'],
  education: 'Bachelor of Technology',
  projectsCount: 2,
  isConfirmed: false
};

// Persists parsed resume to MongoDB or in-memory store
// Defined at module scope so it's accessible in both try and catch
const saveResumeToUser = async (userId, parsedData, resumeFileName, aiParsed = false) => {
  const payload = {
    parsedRole: parsedData.parsedRole || '',
    experienceYears: Number(parsedData.experienceYears) || 0,
    skills: Array.isArray(parsedData.skills) ? parsedData.skills : [],
    education: parsedData.education || '',
    projectsCount: Number(parsedData.projectsCount) || 0,
    uploadedResumeName: resumeFileName || 'resume.pdf',
    isConfirmed: false
  };

  const isMongoConnected = getMongoStatus();
  if (isMongoConnected && mongoose.Types.ObjectId.isValid(userId)) {
    await User.findByIdAndUpdate(userId, { parsedResume: payload });
    console.log(`✅ Saved to MongoDB for user ${userId}`);
  } else {
    const userIdx = memoryUsers.findIndex(u => u._id.toString() === userId);
    if (userIdx !== -1) {
      memoryUsers[userIdx].parsedResume = payload;
      console.log(`✅ Saved to in-memory store for user ${userId}`);
    }
  }
  // Return payload + aiParsed flag so client knows if it's real AI data
  return { ...payload, aiParsed };
};

const parseResume = async (req, res) => {
  let resumeText = req.body.resumeText || '';
  const resumeFileName = req.file ? req.file.originalname : 'resume.pdf';

  try {
    // ── Step 1: Extract text from PDF ──────────────────────────────
    if (req.file) {
      console.log(`\n📄 Resume upload: ${req.file.originalname} (${(req.file.size / 1024).toFixed(1)} KB)`);
      try {
        const parser = new PDFParse({ data: req.file.buffer, verbosity: 0 });
        await parser.load();
        const pdfResult = await parser.getText();

        console.log("PDF Result:", pdfResult);

resumeText = pdfResult?.text || "";

console.log(`📄 PDF text extracted: ${resumeText.length} characters`);
        if (resumeText.trim().length < 50) {
          console.warn('   ⚠ PDF has very little text — may be image-based (scanned). Gemini will use fallback.');
        }
      } catch (pdfErr) {
        console.error('   ❌ pdf-parse error:', pdfErr.message);
      }
    }

    // ── Step 2: Gemini AI Parsing ───────────────────────────────────
    let parsedData = null;
    if (resumeText.trim().length >= 50) {
      console.log('   🤖 Sending to AI for parsing...');
      parsedData = await parseResumeWithAI(resumeText);
    } else {
      console.warn('   ⚠ Skipping AI parsing — not enough text extracted from PDF');
    }
    
    // ── Step 3: Save to MongoDB ─────────────────────────────────────
    if (parsedData) {
      console.log('   ✅ AI parsing succeeded:', parsedData.parsedRole);
      const savedResume = await saveResumeToUser(req.userId, parsedData, resumeFileName, true);
      return res.json(savedResume);
    } else {
      console.warn('   ⚠ AI returned null — saving fallback resume data');
      const savedResume = await saveResumeToUser(req.userId, fallbackParsed, resumeFileName, false);
      return res.json(savedResume);
    }

  } catch (err) {
    console.error('❌ Resume pipeline error:', err.message);
    try {
      const savedResume = await saveResumeToUser(req.userId, fallbackParsed, resumeFileName, false);
      return res.json(savedResume);
    } catch (saveErr) {
      return res.status(500).json({ msg: 'Resume parsing server error', error: saveErr.message });
    }
  }
};

const confirmResume = async (req, res) => {
  try {
    let user;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      user = await User.findByIdAndUpdate(
        req.userId,
        { 'parsedResume.isConfirmed': true },
        { new: true }
      ).select('-password');
    } else {
      const userIdx = memoryUsers.findIndex(u => u._id.toString() === req.userId);
      if (userIdx !== -1) {
        if (memoryUsers[userIdx].parsedResume) {
          memoryUsers[userIdx].parsedResume.isConfirmed = true;
        }
        user = memoryUsers[userIdx];
      }
    }

    if (!user) return res.status(404).json({ msg: 'User not found' });

    res.json({
      msg: 'Resume confirmed successfully',
      parsedResume: user.parsedResume
    });
  } catch (err) {
    res.status(500).json({ msg: 'Failed to confirm resume', error: err.message });
  }
};

const deleteResume = async (req, res) => {
  const emptyResume = {
    parsedRole: '',
    experienceYears: 0,
    skills: [],
    education: '',
    projectsCount: 0,
    uploadedResumeName: '',
    isConfirmed: false
  };

  try {
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      await User.findByIdAndUpdate(req.userId, { parsedResume: emptyResume });
    } else {
      const userIdx = memoryUsers.findIndex(u => u._id.toString() === req.userId);
      if (userIdx !== -1) {
        memoryUsers[userIdx].parsedResume = emptyResume;
      }
    }

    res.json({ msg: 'Resume removed successfully' });
  } catch (err) {
    res.status(500).json({ msg: 'Failed to remove resume', error: err.message });
  }
};

module.exports = {
  parseResume,
  confirmResume,
  deleteResume
};
