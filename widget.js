(function () {
  if (window.__AI_WIDGET_LOADED__) return;
  window.__AI_WIDGET_LOADED__ = true;

  const script = document.currentScript;

  const CONFIG = {
    clientId: script.getAttribute("data-client-id"),
    theme: script.getAttribute("data-theme") || "purple",
    position: script.getAttribute("data-position") || "bottom-right",
    apiUrl: "https://api.kamu.ai/api/agent"
  };

  if (!CONFIG.clientId) {
    console.error("AI Widget: data-client-id is required");
    return;
  }

  const THEME_COLOR = {
    purple: "#6d28d9",
    blue: "#2563eb",
    green: "#16a34a"
  }[CONFIG.theme] || "#6d28d9";

  const POS_STYLE = CONFIG.position === "bottom-left"
    ? "left:20px; bottom:20px;"
    : "right:20px; bottom:20px;";

  const container = document.createElement("div");
  container.innerHTML = `
    <style>
      #ai-widget {
        position: fixed;
        ${POS_STYLE}
        width: 320px;
        font-family: Arial, sans-serif;
        border-radius: 12px;
        box-shadow: 0 10px 30px rgba(0,0,0,.15);
        background: #fff;
        z-index: 99999;
        overflow: hidden;
      }
      #ai-header {
        background: ${THEME_COLOR};
        color: #fff;
        padding: 12px;
        font-weight: bold;
      }
      #ai-body {
        height: 260px;
        padding: 10px;
        overflow-y: auto;
        font-size: 14px;
      }
      #ai-input {
        display: flex;
        border-top: 1px solid #eee;
      }
      #ai-input input {
        flex: 1;
        border: none;
        padding: 10px;
        outline: none;
      }
      #ai-input button {
        background: ${THEME_COLOR};
        color: #fff;
        border: none;
        padding: 0 14px;
        cursor: pointer;
      }
      .ai-msg { margin-bottom: 6px; }
      .ai-user { font-weight: bold; }
      .ai-bot { font-weight: bold; color: ${THEME_COLOR}; }
    </style>

    <div id="ai-widget">
      <div id="ai-header">AI Assistant</div>
      <div id="ai-body">
        <div class="ai-msg"><span class="ai-bot">AI:</span> Halo 👋 Ada yang bisa dibantu?</div>
      </div>
      <div id="ai-input">
        <input placeholder="Tulis pertanyaan..." />
        <button>➤</button>
      </div>
    </div>
  `;

  document.body.appendChild(container);

  const body = container.querySelector("#ai-body");
  const input = container.querySelector("input");
  const button = container.querySelector("button");

  function addMessage(sender, text, css) {
    const div = document.createElement("div");
    div.className = "ai-msg";
    div.innerHTML = `<span class="${css}">${sender}:</span> ${text}`;
    body.appendChild(div);
    body.scrollTop = body.scrollHeight;
  }

  async function sendMessage() {
    const text = input.value.trim();
    if (!text) return;

    input.value = "";
    addMessage("Anda", text, "ai-user");

    try {
      const res = await fetch(CONFIG.apiUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          clientId: CONFIG.clientId,
          message: text
        })
      });

      const data = await res.json();
      addMessage("AI", data.reply || "Maaf, belum ada jawaban.", "ai-bot");

    } catch (e) {
      addMessage("AI", "Terjadi kesalahan koneksi.", "ai-bot");
    }
  }

  button.onclick = sendMessage;
  input.addEventListener("keypress", e => {
    if (e.key === "Enter") sendMessage();
  });
})();


{/* <script
  src="https://cdn.kamu.ai/widget.js"
  data-client-id="konveksi-a"
  data-theme="purple"
  data-position="bottom-right">
</script> */}
