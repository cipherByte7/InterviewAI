const express = require('express');
const multer = require('multer');
const router = express.Router();
const auth = require('../middleware/auth');
const resumeController = require('../controllers/resumeController');
const { getMongoStatus } = require('../config/db');

const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 20 * 1024 * 1024 } // 20MB max
});

// Diagnostic endpoint — check pipeline health without uploading anything
router.get('/status', auth, async (req, res) => {
  const mongoOk = getMongoStatus();

  let geminiOk = false;
  let geminiError = null;
  try {
    const { GoogleGenerativeAI } = require('@google/generative-ai');
    if (process.env.GEMINI_API_KEY) {
      const ai = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
      const model = ai.getGenerativeModel({ model: 'gemini-2.0-flash' });
      await model.generateContent('Reply: OK');
      geminiOk = true;
    } else {
      geminiError = 'GEMINI_API_KEY not set in .env';
    }
  } catch (e) {
    geminiError = e.message.includes('429') ? 'Quota exhausted — get a new key at https://aistudio.google.com/' : e.message.substring(0, 120);
  }

  let pdfParseOk = false;
  try {
    const { PDFParse } = require('pdf-parse');
    pdfParseOk = typeof PDFParse === 'function';
  } catch (_) {}

  res.json({
    pipeline: {
      mongodb: mongoOk ? '✅ Connected' : '❌ Not connected',
      gemini:  geminiOk ? '✅ Working' : `❌ ${geminiError}`,
      pdfParse: pdfParseOk ? '✅ Loaded' : '❌ Module error'
    },
    overallReady: mongoOk && geminiOk && pdfParseOk
  });
});

router.post('/parse', auth, upload.single('file'), resumeController.parseResume);
router.put('/confirm', auth, resumeController.confirmResume);
router.delete('/', auth, resumeController.deleteResume);

module.exports = router;

