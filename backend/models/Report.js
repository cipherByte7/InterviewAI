const mongoose = require('mongoose');

const DimensionSchema = new mongoose.Schema({
  title: String,
  score: Number,
  description: String
});

const ReportSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  role: String,
  date: {
    type: String,
    default: () => new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' })
  },
  overallScore: Number,
  duration: String,
  dimensions: [DimensionSchema],
  strengths: [String],
  weaknesses: [String],
  suggestion: String
});

module.exports = mongoose.model('Report', ReportSchema);
