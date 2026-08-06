const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const authController = require('../controllers/authController');

router.post('/register', authController.register);
router.post('/login', authController.login);
router.get('/user', auth, authController.getUser);
router.post('/logout', auth, authController.logout);
router.post('/refresh', auth, authController.refreshToken);

module.exports = router;
