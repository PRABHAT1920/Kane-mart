const lookupForm = document.getElementById("lookupForm");
const verifyForm = document.getElementById("verifyForm");

const orderIdInput = document.getElementById("orderId");
const otpInput = document.getElementById("otp");

const orderCard = document.getElementById("orderCard");
const displayOrderId = document.getElementById("displayOrderId");
const displayStatus = document.getElementById("displayStatus");

const verifyButton = document.getElementById("verifyButton");
const message = document.getElementById("message");

function showMessage(text, type = "") {
    message.textContent = text;
    message.className = `message ${type}`.trim();
}

async function readResponse(response) {
    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(
            data.message || `Request failed: ${response.status}`
        );
    }

    return data;
}

// Find order by numeric database ID
lookupForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const orderId = orderIdInput.value.trim();

    if (!orderId) {
        showMessage("Please enter an order ID.", "error");
        return;
    }

    orderCard.hidden = true;
    showMessage("Searching for order...");

    try {
        const response = await fetch(
            `/api/delivery/${encodeURIComponent(orderId)}/status`
        );

        const data = await readResponse(response);

        displayOrderId.textContent = data.orderId;
        displayStatus.textContent = data.status;

        orderCard.hidden = false;
        otpInput.value = "";

        const delivered =
            String(data.status).toUpperCase() === "DELIVERED";

        verifyButton.disabled = delivered;

        if (delivered) {
            showMessage(
                "This order has already been delivered.",
                "success"
            );
        } else {
            showMessage(
                "Order found. Enter the customer's delivery OTP."
            );
        }

    } catch (error) {
        showMessage(error.message, "error");
    }
});

// Verify OTP and update delivery status
verifyForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const orderId = orderIdInput.value.trim();
    const otp = otpInput.value.trim();

    if (!orderId || !/^\d{4,8}$/.test(otp)) {
        showMessage(
            "Enter the order ID and a valid 4-8 digit OTP.",
            "error"
        );
        return;
    }

    verifyButton.disabled = true;
    showMessage("Verifying delivery OTP...");

    try {
        const response = await fetch(
            `/api/delivery/${encodeURIComponent(orderId)}/verify`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    otp: otp
                })
            }
        );

        const data = await readResponse(response);

        displayStatus.textContent = data.status || "DELIVERED";
        otpInput.value = "";

        showMessage(
            data.message || "Delivery verified successfully.",
            "success"
        );

    } catch (error) {
        showMessage(error.message, "error");
        verifyButton.disabled = false;
    }
});