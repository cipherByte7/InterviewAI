const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true,
    unique: true
  },
  password: {
    type: String,
    required: true
  },
  targetRole: {
    type: String,
    default: 'Android Developer'
  },
  parsedResume: {
    parsedRole: { type: String, default: '' },
    experienceYears: { type: Number, default: 0 },
    skills: { type: [String], default: [] },
    education: { type: String, default: '' },
    projectsCount: { type: Number, default: 0 },
    uploadedResumeName: { type: String, default: '' },
    isConfirmed: { type: Boolean, default: false }
  },
  date: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('User', UserSchema);
