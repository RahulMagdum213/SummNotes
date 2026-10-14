const summarizeBtn = document.getElementById("summarizeBtn");
const copyBtn = document.getElementById("copyBtn");
const clearBtn = document.getElementById("clearBtn");

const status = document.getElementById("status");
const statusIcon = document.getElementById("statusIcon");

const result = document.getElementById("result");
const resultCard = document.getElementById("resultCard");
const loading = document.getElementById("loading");


summarizeBtn.addEventListener("click", async () => {

    try {

        summarizeBtn.disabled = true;

        resultCard.classList.add("hidden");
        loading.classList.remove("hidden");

        statusIcon.textContent = "🔍";
        status.textContent = "Getting selected text...";


        const [tab] = await chrome.tabs.query({
            active: true,
            currentWindow: true
        });


        const response = await chrome.scripting.executeScript({

            target: {
                tabId: tab.id
            },

            func: () => {
                return window.getSelection().toString().trim();
            }

        });


        const selectedText = response[0].result;


        if (!selectedText) {

            loading.classList.add("hidden");

            statusIcon.textContent = "⚠️";
            status.textContent = "Please select some text first.";

            summarizeBtn.disabled = false;

            return;
        }


        statusIcon.textContent = "🤖";
        status.textContent = "Sending text to Gemini...";


        const apiResponse = await fetch(
            "https://summnotes.onrender.com/api/summarize",
            {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    text: selectedText
                })

            }
        );


        if (!apiResponse.ok) {
    const errorText = await apiResponse.text();
    console.error("Backend error:", apiResponse.status, errorText);
    throw new Error("Backend request failed: " + apiResponse.status);
}


        const data = await apiResponse.json();


        result.textContent = data.summary;

        resultCard.classList.remove("hidden");

        statusIcon.textContent = "✅";
        status.textContent = "Summary generated successfully.";

    }

    catch (error) {

        console.error(error);

        statusIcon.textContent = "❌";
        status.textContent = "Something went wrong.";

    }

    finally {

        loading.classList.add("hidden");
        summarizeBtn.disabled = false;

    }

});


copyBtn.addEventListener("click", async () => {

    const summary = result.textContent;

    if (!summary) {
        return;
    }

    await navigator.clipboard.writeText(summary);

    copyBtn.textContent = "✅ Copied!";

    setTimeout(() => {

        copyBtn.textContent = "📋 Copy";

    }, 1500);

});


clearBtn.addEventListener("click", () => {

    result.textContent = "";

    resultCard.classList.add("hidden");

    statusIcon.textContent = "📝";
    status.textContent = "Select some text on a webpage.";

});