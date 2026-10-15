document.addEventListener("DOMContentLoaded", function () {

    // Get mobile number from session
    const mobile =
        sessionStorage.getItem("loginMobile");


    // If mobile doesn't exist,
    // send user back to login
    if (!mobile) {

        console.error(
            "Mobile number not found."
        );

        window.location.href =
            "/login";

        return;
    }


    // Display mobile number
    const mobileDisplay =
        document.getElementById("mobileDisplay");


    if (mobileDisplay) {

        mobileDisplay.textContent =
            "OTP generated for: " + mobile;

    }


    // OTP form
    const otpForm =
        document.getElementById("otpForm");


    if (otpForm) {

        otpForm.addEventListener(
            "submit",
            verifyOtp
        );

    }


    // Resend button
    const resendBtn =
        document.getElementById("resendBtn");


    if (resendBtn) {

        resendBtn.addEventListener(
            "click",
            resendOtp
        );

    }

});


/* =========================================
   VERIFY OTP
   ========================================= */

async function verifyOtp(event) {

    event.preventDefault();


    const otpInput =
        document.getElementById("otp");


    const verifyBtn =
        document.getElementById("verifyBtn");


    const mobile =
        sessionStorage.getItem("loginMobile");


    const otp =
        otpInput.value.trim();


    // Validate OTP
    if (!/^[0-9]{6}$/.test(otp)) {

        showMessage(
            "Please enter a valid 6-digit OTP.",
            "error"
        );

        return;
    }


    if (!mobile) {

        showMessage(
            "Mobile number not found. Please login again.",
            "error"
        );

        return;
    }


    // Disable button
    verifyBtn.disabled = true;

    verifyBtn.textContent =
        "Verifying...";


    try {

        const response =
            await fetch(
                "/api/auth/verify-otp",
                {

                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        mobile: mobile,

                        otp: otp

                    })

                }
            );


        const data =
            await response.json();


        console.log(
            "OTP Verification Response:",
            data
        );


        if (!response.ok || !data.success) {

            throw new Error(
                data.message ||
                "Invalid or expired OTP."
            );

        }


        showMessage(
            "OTP verified successfully!",
            "success"
        );


        // Save employee information
        if (data.employeeId) {

            sessionStorage.setItem(
                "employeeId",
                data.employeeId
            );

        }


        if (data.employeeName) {

            sessionStorage.setItem(
                "employeeName",
                data.employeeName
            );

        }


        if (data.role) {

            sessionStorage.setItem(
                "employeeRole",
                data.role
            );

        }


        // Remove temporary mobile
        sessionStorage.removeItem(
            "loginMobile"
        );


        // Go to dashboard
        setTimeout(function () {

            window.location.href =
                "/dashboard";

        }, 800);


    } catch (error) {

        console.error(
            "OTP verification error:",
            error
        );


        showMessage(
            error.message ||
            "Unable to verify OTP.",
            "error"
        );


        verifyBtn.disabled = false;

        verifyBtn.textContent =
            "Verify OTP";

    }

}


/* =========================================
   RESEND OTP
   ========================================= */

async function resendOtp() {

    const mobile =
        sessionStorage.getItem("loginMobile");


    const resendBtn =
        document.getElementById("resendBtn");


    if (!mobile) {

        showMessage(
            "Mobile number not found. Please login again.",
            "error"
        );

        return;
    }


    resendBtn.disabled = true;

    resendBtn.textContent =
        "Generating...";


    try {

        const response =
            await fetch(
                "/api/auth/send-otp",
                {

                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        mobile: mobile

                    })

                }
            );


        const data =
            await response.json();


        console.log(
            "Resend OTP Response:",
            data
        );


        if (!response.ok || !data.success) {

            throw new Error(
                data.message ||
                "Unable to generate new OTP."
            );

        }


        // Show OTP only in browser console
        console.log(
            "========================================"
        );

        console.log(
            "          KANEMART NEW OTP"
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


        showMessage(
            "New OTP generated. Check the browser console.",
            "success"
        );


    } catch (error) {

        console.error(
            "Resend OTP error:",
            error
        );


        showMessage(
            error.message ||
            "Unable to generate OTP.",
            "error"
        );


    } finally {

        resendBtn.disabled = false;

        resendBtn.textContent =
            "Resend OTP";

    }

}


/* =========================================
   MESSAGE
   ========================================= */

function showMessage(text, type) {

    const message =
        document.getElementById("message");


    if (!message) {
        return;
    }


    message.textContent =
        text;


    message.className =
        "message " + type;


    message.style.display =
        "block";

}