document.addEventListener("DOMContentLoaded", function () {

    const loginForm = document.getElementById("loginForm");

    if (!loginForm) {
        console.error("Login form not found.");
        return;
    }


    loginForm.addEventListener("submit", async function (event) {

        event.preventDefault();


        const mobileInput =
            document.getElementById("mobile");

        const button =
            document.getElementById("sendOtpBtn");


        const mobile =
            mobileInput.value.trim();


        // Validate mobile number
        if (!/^[0-9]{10}$/.test(mobile)) {

            showMessage(
                "Please enter a valid 10-digit mobile number.",
                "error"
            );

            mobileInput.focus();

            return;
        }


        // Disable button
        button.disabled = true;

        button.textContent =
            "Generating OTP...";


        showMessage(
            "Generating OTP...",
            "info"
        );


        try {

            const response =
                await fetch("/api/auth/send-otp", {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        mobile: mobile
                    })

                });


            console.log(
                "OTP API Status:",
                response.status
            );


            const data =
                await response.json();


            console.log(
                "OTP API Response:",
                data
            );


            if (!response.ok || !data.success) {

                throw new Error(
                    data.message ||
                    "Unable to generate OTP."
                );

            }


            // Save mobile number
            sessionStorage.setItem(
                "loginMobile",
                mobile
            );


            // Development OTP
            // OTP is NOT displayed on webpage
            if (data.otp) {

                console.log(
                    "========================================"
                );

                console.log(
                    "       KANEMART DEVELOPMENT OTP"
                );

                console.log(
                    "========================================"
                );

                console.log(
                    "Mobile : " + mobile
                );

                console.log(
                    "OTP    : " + data.otp
                );

                console.log(
                    "========================================"
                );

            }


            showMessage(
                "OTP generated successfully. Check the browser console.",
                "success"
            );


            // Go to OTP page
            setTimeout(function () {

                window.location.href =
                    "/otp";

            }, 800);


        } catch (error) {

            console.error(
                "OTP generation error:",
                error
            );


            showMessage(
                error.message ||
                "Unable to connect to server.",
                "error"
            );


            button.disabled = false;

            button.textContent =
                "Send OTP";

        }

    });

});


function showMessage(text, type) {

    const message =
        document.getElementById("message");


    if (!message) {
        return;
    }


    message.textContent = text;


    message.className =
        "message " + type;


    message.style.display =
        "block";

}