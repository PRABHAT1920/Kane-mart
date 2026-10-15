const supplierForm = document.getElementById("supplierForm");
const supplierTableBody = document.getElementById("supplierTableBody");
const message = document.getElementById("message");

async function loadSuppliers() {

    try {

        const response = await fetch("/api/suppliers");

        if (!response.ok) {
            throw new Error("Failed to load suppliers");
        }

        const suppliers = await response.json();

        supplierTableBody.innerHTML = "";

        suppliers.forEach(supplier => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${supplier.id}</td>
                <td>${supplier.name}</td>
                <td>${supplier.contact}</td>
                <td>${supplier.email}</td>
                <td>${supplier.address}</td>
                <td>${supplier.active ? "Active" : "Inactive"}</td>
            `;

            supplierTableBody.appendChild(row);
        });

    } catch (error) {

        console.error(error);

        message.textContent =
            "Unable to load suppliers.";
    }
}


supplierForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const supplier = {

        name: document.getElementById("name").value,

        contact: document.getElementById("contact").value,

        email: document.getElementById("email").value,

        address: document.getElementById("address").value,

        active: true
    };

    try {

        const response = await fetch("/api/suppliers", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(supplier)
        });

        if (!response.ok) {

            const errorText = await response.text();

            throw new Error(errorText);
        }

        message.textContent =
            "Supplier added successfully.";

        supplierForm.reset();

        loadSuppliers();

    } catch (error) {

        console.error(error);

        message.textContent =
            "Failed to add supplier.";
    }
});


loadSuppliers();