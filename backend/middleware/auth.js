const jwt = require('jsonwebtoken');

// Shared blacklist — populated by /api/auth/logout
// Imported lazily so server.js initialises it first
let _getBlacklist = null;
const setBlacklistProvider = (fn) => { _getBlacklist = fn; };

module.exports = function (req, res, next) {
  // Get token from header
  const authHeader = req.header('Authorization');
  if (!authHeader) {
    return res.status(401).json({ msg: 'No token, authorization denied' });
  }

  // Token format: "Bearer <token>"
  const parts = authHeader.split(' ');
  if (parts.length !== 2 || parts[0] !== 'Bearer') {
    return res.status(401).json({ msg: 'Token format is invalid, Bearer expected' });
  }

  const token = parts[1];

  // Check if token has been invalidated via logout
  if (_getBlacklist && _getBlacklist().has(token)) {
    return res.status(401).json({ msg: 'Token has been invalidated. Please log in again.' });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'super_secret_interview_key_123');
    req.userId = decoded.userId;
    next();
  } catch (err) {
    res.status(401).json({ msg: 'Token is not valid' });
  }
};

module.exports.setBlacklistProvider = setBlacklistProvider;
