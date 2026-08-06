const express = require('express');
const multer = require('multer');
const router = express.Router();
const auth = require('../middleware/auth');
const resumeController = require('../controllers/resumeController');

const upload = multer({ storage: multer.memoryStorage() });

router.post('/parse', auth, upload.single('file'), resumeController.parseResume);
router.put('/confirm', auth, resumeController.confirmResume);
router.delete('/', auth, resumeController.deleteResume);

module.exports = router;
