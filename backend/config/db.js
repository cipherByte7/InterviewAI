require('dotenv').config();
const mongoose = require('mongoose');

let isMongoConnected = false;
const memoryUsers = [];
const memoryReports = [];

const connectDB = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/interview_ai');
    console.log('MongoDB Connected successfully.');
    isMongoConnected = true;
  } catch (err) {
    console.warn('MongoDB connection failed. Falling back to In-Memory DB cache.');
    console.warn(err.message);
    isMongoConnected = false;
  }
};

const getMongoStatus = () => isMongoConnected;

module.exports = {
  connectDB,
  getMongoStatus,
  memoryUsers,
  memoryReports
};
