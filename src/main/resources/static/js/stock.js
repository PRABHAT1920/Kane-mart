const message = document.getElementById("message");

const transactionTableBody =
    document.getElementById("transactionTableBody");


async function loadProducts() {

    const response =
        await fetch("/api/products/active");

    const products =
        await response.json();

    const selects = [
        "stockInProduct",
        "stockOutProduct",
        "transferProduct"
    ];

    selects.forEach(id => {

        const select =
            document.getElementById(id);

        select.innerHTML =
            '<option value="">Select Product</option>';

        products.forEach(product => {

            const option =
                document.createElement("option");

            option.value = product.id;

            option.textContent =
                `${product.name} (${product.sku})`;

            select.appendChild(option);
        });
    });
}


async function loadLocations() {

    const response =
        await fetch("/api/locations");

    const locations =
        await response.json();

    const selects = [
        "stockInLocation",
        "stockOutLocation",
        "fromLocation",
        "toLocation"
    ];

    selects.forEach(id => {

        const select =
            document.getElementById(id);

        select.innerHTML =
            '<option value="">Select Location</option>';

        locations.forEach(location => {

            const option =
                document.createElement("option");

            option.value = location.id;

            option.textContent =
                `${location.zone} / ${location.rack} / ${location.shelf} / ${location.bin}`;

            select.appendChild(option);
        });
    });
}


async function loadTransactions() {

    const response =
        await fetch("/api/stock/transactions");

    const transactions =
        await response.json();

    transactionTableBody.innerHTML = "";

    transactions.forEach(transaction => {

        const row =
            document.createElement("tr");

        row.innerHTML = `
            <td>${transaction.id}</td>
            <td>${transaction.product.name}</td>
            <td>
                ${transaction.location.zone}
                /
                ${transaction.location.rack}
                /
                ${transaction.location.shelf}
                /
                ${transaction.location.bin}
            </td>
            <td>${transaction.quantity}</td>
            <td>${transaction.transactionType}</td>
            <td>${transaction.transactionDate}</td>
            <td>${transaction.reference || ""}</td>
        `;

        transactionTableBody.appendChild(row);
    });
}


document.getElementById("stockInForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const productId =
            document.getElementById("stockInProduct").value;

        const locationId =
            document.getElementById("stockInLocation").value;

        const quantity =
            document.getElementById("stockInQuantity").value;

        const reference =
            document.getElementById("stockInReference").value;

        try {

            const response =
                await fetch(
                    `/api/stock/in?productId=${productId}&locationId=${locationId}&quantity=${quantity}&reference=${encodeURIComponent(reference)}`,
                    {
                        method: "POST"
                    }
                );

            if (!response.ok) {
                throw new Error("Stock In failed");
            }

            message.textContent =
                "Stock In successful.";

            event.target.reset();

            await loadTransactions();

        } catch (error) {

            console.error(error);

            message.textContent =
                "Stock In failed.";
        }
    });


document.getElementById("stockOutForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const productId =
            document.getElementById("stockOutProduct").value;

        const locationId =
            document.getElementById("stockOutLocation").value;

        const quantity =
            document.getElementById("stockOutQuantity").value;

        const reference =
            document.getElementById("stockOutReference").value;

        try {

            const response =
                await fetch(
                    `/api/stock/out?productId=${productId}&locationId=${locationId}&quantity=${quantity}&reference=${encodeURIComponent(reference)}`,
                    {
                        method: "POST"
                    }
                );

            if (!response.ok) {
                throw new Error("Stock Out failed");
            }

            message.textContent =
                "Stock Out successful.";

            event.target.reset();

            await loadTransactions();

        } catch (error) {

            console.error(error);

            message.textContent =
                "Stock Out failed.";
        }
    });


document.getElementById("transferForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const productId =
            document.getElementById("transferProduct").value;

        const fromLocationId =
            document.getElementById("fromLocation").value;

        const toLocationId =
            document.getElementById("toLocation").value;

        const quantity =
            document.getElementById("transferQuantity").value;

        const reference =
            document.getElementById("transferReference").value;

        try {

            const response =
                await fetch(
                    `/api/stock/transfer?productId=${productId}&fromLocationId=${fromLocationId}&toLocationId=${toLocationId}&quantity=${quantity}&reference=${encodeURIComponent(reference)}`,
                    {
                        method: "POST"
                    }
                );

            if (!response.ok) {
                throw new Error("Transfer failed");
            }

            message.textContent =
                "Stock transfer successful.";

            event.target.reset();

            await loadTransactions();

        } catch (error) {

            console.error(error);

            message.textContent =
                "Stock transfer failed.";
        }
    });


async function initializePage() {

    try {

        await loadProducts();

        await loadLocations();

        await loadTransactions();

    } catch (error) {

        console.error(error);

        message.textContent =
            "Unable to load stock data.";
    }
}


initializePage();