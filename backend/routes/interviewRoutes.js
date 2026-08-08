const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const interviewController = require('../controllers/interviewController');

router.post('/start', auth, interviewController.startInterview);
router.post('/next-question', auth, interviewController.nextQuestion);
router.post('/evaluate', auth, interviewController.evaluateInterview);
router.get('/history', auth, interviewController.getHistory);
router.get('/report/:id', auth, interviewController.getReportById);
router.delete('/report/:id', auth, interviewController.deleteReport);

module.exports = router;
