require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { connectDB } = require('./config/db');

// Import routes
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const resumeRoutes = require('./routes/resumeRoutes');
const interviewRoutes = require('./routes/interviewRoutes');

const app = express();

// Increase JSON body limit for large payloads (conversation histories)
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));
app.use(cors());

// Connect database with graceful fallback
connectDB();

// Wire up modular routes
app.use('/api/auth', authRoutes);
app.use('/api/user', userRoutes);
app.use('/api/resume', resumeRoutes);
app.use('/api/interview', interviewRoutes);

// Global error handler — prevents unhandled errors from crashing the process
app.use((err, req, res, next) => {
  console.error('Unhandled server error:', err.message);
  if (err.code === 'LIMIT_FILE_SIZE') {
    return res.status(413).json({ msg: 'File too large. Maximum resume size is 20MB.' });
  }
  res.status(500).json({ msg: 'Internal server error', error: err.message });
});

// Start Server
const PORT = process.env.PORT || 5000;
const server = app.listen(PORT, () => console.log(`InterviewAI Server launched on port ${PORT}`));

server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(`\n❌ Port ${PORT} is already in use.`);
    console.error(`   Another instance of the server may still be running.`);
    console.error(`   Run this command to kill it, then try again:`);
    console.error(`   npx kill-port ${PORT}\n`);
    process.exit(1);
  } else {
    throw err;
  }
});

