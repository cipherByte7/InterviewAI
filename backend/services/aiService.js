require("dotenv").config();
const OpenAI = require("openai");

const client = new OpenAI({
    apiKey: (process.env.OPENROUTER_API_KEY || "").trim(),
    baseURL: "https://openrouter.ai/api/v1",
});

const MODEL = process.env.AI_MODEL;

// ==============================
// Generic AI Call
// ==============================

async function askAI(prompt) {
    const completion = await client.chat.completions.create({
        model: MODEL,
        messages: [
            {
                role: "system",
                content:
                    "You are an expert technical interviewer and recruiter. Always follow the user's instructions exactly. Return ONLY JSON when requested."
            },
            {
                role: "user",
                content: prompt
            }
        ],
        temperature: 0.3
    });

    return completion.choices[0].message.content.trim();
}

// ==============================
// JSON Extractor
// ==============================

function extractJSON(text) {

    if (!text) return null;

    // ```json ... ```
    const block = text.match(/```(?:json)?([\s\S]*?)```/i);
    if (block) {
        try {
            return JSON.parse(block[1].trim());
        } catch {}
    }

    // {...}
    const obj = text.match(/\{[\s\S]*\}/);
    if (obj) {
        try {
            return JSON.parse(obj[0]);
        } catch {}
    }

    try {
        return JSON.parse(text.trim());
    } catch {}

    return null;
}

// ==============================
// Resume Parser
// ==============================

async function parseResumeWithAI(resumeText) {

    const prompt = `
You are an expert ATS Resume Parser.

Extract information from the following resume.

IMPORTANT:

If the candidate is a fresher,
infer the role from projects and skills.

Never leave parsedRole empty.

Return ONLY JSON.

{
    "parsedRole":"",
    "experienceYears":0,
    "skills":[],
    "education":"",
    "projectsCount":0
}

Resume:

${resumeText}
`;

    try {

        const response = await askAI(prompt);

        console.log("\n========== AI RAW RESPONSE ==========");
        console.log(response);
        console.log("=====================================\n");

        const parsed = extractJSON(response);

        console.log("Parsed JSON:");
        console.log(parsed);

        if (!parsed)
            return null;

        return {
            parsedRole:
                parsed.parsedRole ||
                "Software Developer",

            experienceYears:
                Number(parsed.experienceYears) || 0,

            skills:
                Array.isArray(parsed.skills)
                    ? parsed.skills.slice(0, 15)
                    : [],

            education:
                parsed.education || "",

            projectsCount:
                Number(parsed.projectsCount) || 0
        };

    } catch (err) {

        console.log(err.message);
        return null;
    }
}

// ==============================
// First Interview Question
// ==============================

async function generateFirstQuestionWithAI(
    role,
    difficulty,
    category,
    parsedResume
) {

    const prompt = `
You are a senior software engineering interviewer.

Candidate Resume:

${JSON.stringify(parsedResume, null, 2)}

Role:
${role}

Difficulty:
${difficulty}

Interview Type:
${category}

Rules:

- Study the resume carefully.
- Ask about projects whenever possible.
- Do NOT ask generic questions.
- Ask only ONE question.
- Do not greet the candidate.
- Do not explain anything.

Return ONLY JSON.

{
    "question":"..."
}
`;

    try {

        const response = await askAI(prompt);

        console.log(response);

        return extractJSON(response);

    } catch (err) {

        console.log(err.message);
        return null;
    }
}

// ==============================
// Next Interview Question
// ==============================

async function generateNextQuestionWithAI(
    role,
    difficulty,
    category,
    conversationHistory,
    currentAnswer,
    parsedResume
) {

    const prompt = `
You are conducting a technical interview.

Candidate Resume:

${JSON.stringify(parsedResume, null, 2)}

Role:
${role}

Difficulty:
${difficulty}

Interview Type:
${category}

Conversation History:

${JSON.stringify(conversationHistory, null, 2)}

Candidate's Latest Answer:

${currentAnswer}

Rules:

- Never repeat previous questions.
- Ask a follow-up if appropriate.
- If answer is weak, dig deeper.
- If answer is strong, increase difficulty.
- Ask ONE question only.

Return ONLY JSON.

{
    "nextQuestion":"...",
    "isLastQuestion":false
}
`;

    try {

        const response = await askAI(prompt);

        console.log(response);

        return extractJSON(response);

    } catch (err) {

        console.log(err.message);
        return null;
    }
}

// ==============================
// Final Interview Evaluation
// ==============================

async function evaluateFullInterviewWithAI(
    role,
    difficulty,
    category,
    conversationHistory
) {
const prompt = `
You are a Senior Software Engineering Interviewer with over 15 years of experience interviewing candidates at companies such as Google, Microsoft, Amazon, Meta and Atlassian.

Your task is to evaluate the candidate fairly and realistically.

Role:
${role}

Difficulty:
${difficulty}

Interview Type:
${category}

Interview Transcript:

${JSON.stringify(conversationHistory, null, 2)}

Scoring Guidelines:

- 90-100 = Outstanding performance. Candidate demonstrates excellent technical knowledge, confidence, and communication.
- 80-89 = Strong performance. Candidate is interview-ready with only minor improvements needed.
- 70-79 = Good performance. Candidate understands most concepts but has a few knowledge gaps.
- 60-69 = Average performance. Candidate has basic understanding but needs more practice.
- 40-59 = Weak performance. Candidate struggles to explain concepts clearly.
- 0-39 = Very poor performance. Candidate provides incorrect, irrelevant, or almost no answers.

Important Rules:

- Be fair and balanced.
- Do NOT intentionally give low scores.
- Reward partially correct answers.
- Ignore minor grammar mistakes.
- Focus on technical understanding.
- If the candidate explains concepts correctly, score generously.
- Most average candidates should receive between 65 and 80.
- Strong candidates should receive between 80 and 90.
- Scores below 40 should only be used when the candidate barely answers.

Generate:

- Overall Score
- Technical Knowledge
- Communication
- Confidence
- Fluency
- Speaking Pace
- Fillers
- Resume Match

Also provide:

- 3 strengths
- 3 weaknesses
- One detailed improvement suggestion.

Return ONLY JSON in this exact format:

{
  "overallScore":0,
  "dimensions":[
    {
      "title":"Technical Knowledge",
      "score":0,
      "description":""
    },
    {
      "title":"Communication",
      "score":0,
      "description":""
    },
    {
      "title":"Confidence",
      "score":0,
      "description":""
    },
    {
      "title":"Fluency",
      "score":0,
      "description":""
    },
    {
      "title":"Speaking Pace",
      "score":0,
      "description":""
    },
    {
      "title":"Fillers",
      "score":0,
      "description":""
    },
    {
      "title":"Resume Match",
      "score":0,
      "description":""
    }
  ],
  "strengths":[],
  "weaknesses":[],
  "suggestion":""
}
`;

    try {

        const response = await askAI(prompt);

        console.log(response);

        return extractJSON(response);

    } catch (err) {

        console.log(err.message);
        return null;
    }
}

module.exports = {
    askAI,
    parseResumeWithAI,
    generateFirstQuestionWithAI,
    generateNextQuestionWithAI,
    evaluateFullInterviewWithAI
};