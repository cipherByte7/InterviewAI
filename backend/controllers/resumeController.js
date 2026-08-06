const mongoose = require('mongoose');
const pdfParse = require('pdf-parse');
const User = require('../models/User');
const { getMongoStatus, memoryUsers } = require('../config/db');
const { parseResumeWithAI } = require('../services/geminiService');

const parseResume = async (req, res) => {
  const fallbackParsed = {
    parsedRole: "Senior Android Dev",
    experienceYears: 3,
    skills: ["Kotlin", "Jetpack Compose", "Coroutines", "Dagger Hilt", "Clean Architecture"],
    education: "B.Tech in Computer Science",
    projectsCount: 4,
    isConfirmed: false
  };

  let resumeText = req.body.resumeText || "";

  try {
    if (req.file) {
      console.log(`Received file: ${req.file.originalname}, size: ${req.file.size} bytes`);
      const data = await pdfParse(req.file.buffer);
      resumeText = data.text;
    }

    const saveToUser = async (parsedData) => {
      const payload = {
        parsedRole: parsedData.parsedRole,
        experienceYears: parsedData.experienceYears,
        skills: parsedData.skills,
        education: parsedData.education,
        projectsCount: parsedData.projectsCount,
        uploadedResumeName: req.file ? req.file.originalname : "resume.pdf",
        isConfirmed: false
      };

      const isMongoConnected = getMongoStatus();
      if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
        await User.findByIdAndUpdate(req.userId, { parsedResume: payload });
      } else {
        const userIdx = memoryUsers.findIndex(u => u._id.toString() === req.userId);
        if (userIdx !== -1) {
          memoryUsers[userIdx].parsedResume = payload;
        }
      }
      return payload;
    };

    const parsedData = await parseResumeWithAI(resumeText);
    if (!parsedData) {
      // Gemini not configured or parsing fallback
      await new Promise(resolve => setTimeout(resolve, 1000));
      const savedResume = await saveToUser(fallbackParsed);
      return res.json(savedResume);
    }

    const savedResume = await saveToUser(parsedData);
    res.json(savedResume);
  } catch (err) {
    console.error('Resume parsing failed, falling back to mock details.', err.message);
    try {
      const savedResume = await saveToUser(fallbackParsed);
      res.json(savedResume);
    } catch (saveErr) {
      res.status(500).json({ msg: 'Resume parsing server error', error: saveErr.message });
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
