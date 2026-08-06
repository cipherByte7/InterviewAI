const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');
const User = require('../models/User');
const Report = require('../models/Report');
const { getMongoStatus, memoryUsers, memoryReports } = require('../config/db');

// In-memory token blacklist (cleared on server restart; for production use Redis)
const tokenBlacklist = new Set();

const register = async (req, res) => {
  const { name, email, password, targetRole } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({ msg: 'Please enter all required fields' });
  }

  try {
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    let user;
    const isMongoConnected = getMongoStatus();
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

    const token = jwt.sign(
      { userId: user._id.toString() },
      process.env.JWT_SECRET || 'super_secret_interview_key_123',
      { expiresIn: '7d' }
    );
    res.json({
      token,
      user: { id: user._id.toString(), name: user.name, email: user.email, targetRole: user.targetRole }
    });
  } catch (err) {
    res.status(500).json({ msg: 'Server registration error', error: err.message });
  }
};

const login = async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ msg: 'Please enter all fields' });
  }

  try {
    let user;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected) {
      user = await User.findOne({ email });
    } else {
      user = memoryUsers.find(u => u.email === email);
    }

    if (!user) return res.status(400).json({ msg: 'User does not exist' });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(400).json({ msg: 'Invalid credentials' });

    const token = jwt.sign(
      { userId: user._id.toString() },
      process.env.JWT_SECRET || 'super_secret_interview_key_123',
      { expiresIn: '7d' }
    );
    res.json({
      token,
      user: { id: user._id.toString(), name: user.name, email: user.email, targetRole: user.targetRole }
    });
  } catch (err) {
    res.status(500).json({ msg: 'Server login error', error: err.message });
  }
};

const getUser = async (req, res) => {
  try {
    let user;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      user = await User.findById(req.userId).select('-password');
    } else {
      const match = memoryUsers.find(u => u._id.toString() === req.userId);
      if (match) {
        user = match;
      }
    }

    if (!user) return res.status(404).json({ msg: 'User not found' });

    // Dynamic stats aggregation
    let reports;
    if (isMongoConnected) {
      reports = await Report.find({ userId: req.userId });
    } else {
      reports = memoryReports.filter(r => r.userId === req.userId);
    }

    const totalSessions = reports.length;
    const averageScore = totalSessions > 0 
      ? Math.round(reports.reduce((acc, curr) => acc + (curr.overallScore || 0), 0) / totalSessions) 
      : 0;

    let totalSeconds = 0;
    reports.forEach(r => {
      const parts = (r.duration || "00:00").split(":");
      totalSeconds += (parseInt(parts[0]) || 0) * 60 + (parseInt(parts[1]) || 0);
    });
    const totalHours = parseFloat((totalSeconds / 3600).toFixed(1));

    res.json({
      id: user._id ? user._id.toString() : user._id || user.id,
      name: user.name,
      email: user.email,
      targetRole: user.targetRole,
      parsedResume: user.parsedResume || null,
      stats: {
        totalSessions,
        averageScore,
        totalHours
      }
    });
  } catch (err) {
    res.status(500).json({ msg: 'Server fetch error', error: err.message });
  }
};

const updateProfile = async (req, res) => {
  const { targetRole } = req.body;
  try {
    let user;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      user = await User.findByIdAndUpdate(
        req.userId,
        { targetRole },
        { new: true }
      ).select('-password');
    } else {
      const userIdx = memoryUsers.findIndex(u => u._id.toString() === req.userId);
      if (userIdx !== -1) {
        memoryUsers[userIdx].targetRole = targetRole;
        user = memoryUsers[userIdx];
      }
    }

    if (!user) return res.status(404).json({ msg: "User not found" });
    
    res.json({
      id: user._id ? user._id.toString() : user._id || user.id,
      name: user.name,
      email: user.email,
      targetRole: user.targetRole,
      parsedResume: user.parsedResume || null
    });
  } catch (err) {
    res.status(500).json({ msg: "Profile update failed", error: err.message });
  }
};

const logout = (req, res) => {
  try {
    const authHeader = req.header('Authorization') || req.header('x-auth-token');
    const token = authHeader && authHeader.startsWith('Bearer ')
      ? authHeader.replace('Bearer ', '')
      : authHeader;

    if (token) tokenBlacklist.add(token);
    res.json({ msg: 'Logged out successfully' });
  } catch (err) {
    res.status(500).json({ msg: 'Logout failed', error: err.message });
  }
};

const refreshToken = async (req, res) => {
  try {
    let user;
    const isMongoConnected = getMongoStatus();
    if (isMongoConnected && mongoose.Types.ObjectId.isValid(req.userId)) {
      user = await User.findById(req.userId).select('-password');
    } else {
      user = memoryUsers.find(u => u._id.toString() === req.userId);
    }

    if (!user) return res.status(404).json({ msg: 'User not found' });

    // Issue a fresh 7-day token
    const newToken = jwt.sign(
      { userId: user._id.toString() },
      process.env.JWT_SECRET || 'super_secret_interview_key_123',
      { expiresIn: '7d' }
    );

    res.json({
      token: newToken,
      user: {
        id: user._id.toString(),
        name: user.name,
        email: user.email,
        targetRole: user.targetRole
      }
    });
  } catch (err) {
    res.status(500).json({ msg: 'Token refresh failed', error: err.message });
  }
};

const authMiddleware = require('../middleware/auth');
authMiddleware.setBlacklistProvider(() => tokenBlacklist);

module.exports = {
  register,
  login,
  getUser,
  updateProfile,
  logout,
  refreshToken,
  tokenBlacklist
};
