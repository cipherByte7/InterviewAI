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
  category: {
    type: String,
    default: 'Technical'
  },
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

ReportSchema.set('toJSON', {
  virtuals: true,
  transform: (doc, ret) => {
    ret.id = ret._id.toString();
    delete ret.__v;
    return ret;
  }
});

ReportSchema.set('toObject', {
  virtuals: true,
  transform: (doc, ret) => {
    ret.id = ret._id.toString();
    delete ret.__v;
    return ret;
  }
});

module.exports = mongoose.model('Report', ReportSchema);
