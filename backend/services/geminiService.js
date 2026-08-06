require('dotenv').config();

let genAI = null;
if (process.env.GEMINI_API_KEY) {
  try {
    const { GoogleGenerativeAI } = require('@google/generative-ai');
    genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
    console.log('Gemini API Engine initialized.');
  } catch (e) {
    console.warn('Failed to initialize Gemini API. Using local mock generator instead.');
  }
} else {
  console.warn('No GEMINI_API_KEY configured in .env. Falling back to local simulated AI generator.');
}

/**
 * Parses resume text using Gemini AI.
 */
const parseResumeWithAI = async (resumeText) => {
  if (!genAI || !resumeText) return null;
  try {
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    const prompt = `Analyze this extracted resume text and return structured details strictly in JSON format. The JSON must match this structure:
    {
      "parsedRole": "Role name e.g. Senior Android Dev",
      "experienceYears": 3,
      "skills": ["Skill1", "Skill2", "Skill3"],
      "education": "Education e.g. B.Tech in CS",
      "projectsCount": 4
    }
    Resume text: ${resumeText}`;

    const result = await model.generateContent(prompt);
    const responseText = result.response.text();
    const cleanJson = responseText.replace(/```json|```/g, '').trim();
    return JSON.parse(cleanJson);
  } catch (err) {
    console.error('Gemini parseResumeWithAI service error:', err.message);
    return null;
  }
};

/**
 * Generates the FIRST adaptive question of the interview.
 */
const generateFirstQuestionWithAI = async (role, difficulty, category, parsedResume) => {
  if (!genAI) return null;
  try {
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    const prompt = `You are a professional mock interviewer for the role: ${role}.
    Difficulty Level: ${difficulty}
    Interview Type: ${category}
    Candidate Resume Details: ${JSON.stringify(parsedResume || {})}
    
    Generate the FIRST question of the interview. It should be tailored to their skills, experience, and the target role.
    
    Return the response strictly as a JSON object matching this format:
    {
      "question": "The first question text"
    }`;

    const result = await model.generateContent(prompt);
    const responseText = result.response.text();
    const cleanJson = responseText.replace(/```json|```/g, '').trim();
    return JSON.parse(cleanJson);
  } catch (err) {
    console.error('Gemini generateFirstQuestionWithAI service error:', err.message);
    return null;
  }
};

/**
 * Generates the NEXT adaptive question of the interview based on conversation history.
 */
const generateNextQuestionWithAI = async (role, difficulty, category, conversationHistory, currentAnswer, parsedResume) => {
  if (!genAI) return null;
  try {
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    const prompt = `You are a professional mock interviewer for the role: ${role}.
    Difficulty Level: ${difficulty}
    Interview Type: ${category}
    Candidate Resume Details: ${JSON.stringify(parsedResume || {})}
    
    Conversation History so far:
    ${JSON.stringify(conversationHistory || [])}
    
    The candidate just answered the last question with:
    "${currentAnswer}"
    
    Synthesize the next natural follow-up question.
    - If the candidate mentioned specific projects, challenges, or architectural patterns, follow up directly on that for deeper technical details.
    - Do not make it a rigid list of questions. Make it feel like a real conversational interviewer.
    - Keep it focused on the target role and candidate level.
    
    Return the response strictly as a JSON object matching this format:
    {
      "nextQuestion": "The next adaptive question text",
      "isLastQuestion": false
    }`;

    const result = await model.generateContent(prompt);
    const responseText = result.response.text();
    const cleanJson = responseText.replace(/```json|```/g, '').trim();
    return JSON.parse(cleanJson);
  } catch (err) {
    console.error('Gemini generateNextQuestionWithAI service error:', err.message);
    return null;
  }
};

/**
 * Performs final full evaluation of the entire interview transcript.
 */
const evaluateFullInterviewWithAI = async (role, difficulty, category, conversationHistory) => {
  if (!genAI || !conversationHistory) return null;
  try {
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    const prompt = `Analyze this transcript of questions and answers from a mock interview and perform a comprehensive professional evaluation.
    Role: ${role}
    Difficulty: ${difficulty}
    Category: ${category}
    
    Transcript details:
    ${JSON.stringify(conversationHistory)}
    
    Evaluate the candidate and return the response strictly in JSON matching this structure:
    {
      "overallScore": 86,
      "dimensions": [
        { "title": "Technical Knowledge", "score": 85, "description": "Specific feedback detail" },
        { "title": "Communication", "score": 88, "description": "Specific feedback detail" },
        { "title": "Confidence", "score": 82, "description": "Specific feedback detail" },
        { "title": "Fluency", "score": 90, "description": "Specific feedback detail" },
        { "title": "Speaking Pace", "score": 80, "description": "Specific feedback detail" },
        { "title": "Fillers", "score": 85, "description": "Specific feedback detail on filler words used" },
        { "title": "Resume Match", "score": 84, "description": "Specific feedback detail" }
      ],
      "strengths": ["Strength detail 1", "Strength detail 2"],
      "weaknesses": ["Improvement detail 1", "Improvement detail 2"],
      "suggestion": "Actionable overall plan instructions..."
    }`;

    const result = await model.generateContent(prompt);
    const responseText = result.response.text();
    const cleanJson = responseText.replace(/```json|```/g, '').trim();
    return JSON.parse(cleanJson);
  } catch (err) {
    console.error('Gemini evaluateFullInterviewWithAI service error:', err.message);
    return null;
  }
};

module.exports = {
  parseResumeWithAI,
  generateFirstQuestionWithAI,
  generateNextQuestionWithAI,
  evaluateFullInterviewWithAI
};
