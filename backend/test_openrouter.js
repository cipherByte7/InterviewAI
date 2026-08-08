require("dotenv").config();

const { askAI } = require("./services/aiService");

(async () => {

    const response = await askAI("Reply with only OpenRouter Works");

    console.log(response);

})();