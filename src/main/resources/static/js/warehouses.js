async function loadWarehouses() {

    try {

        const response = await fetch("/api/warehouses");

        if (!response.ok) {
            throw new Error("Failed to load warehouses");
        }

        const warehouses = await response.json();

        const table = document.getElementById("warehouseTable");

        table.innerHTML = "";

        if (warehouses.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="5">
                        No warehouses found
                    </td>
                </tr>
            `;

            return;
        }

        warehouses.forEach(warehouse => {

            table.innerHTML += `
                <tr>
                    <td>${warehouse.id}</td>
                    <td>${warehouse.name}</td>
                    <td>${warehouse.code}</td>
                    <td>${warehouse.address || ""}</td>
                    <td>
                        ${warehouse.active ? "ACTIVE" : "INACTIVE"}
                    </td>
                </tr>
            `;

        });

    } catch (error) {

        console.error(error);

        document.getElementById("message").innerText =
            "Unable to load warehouses.";
    }
}


async function addWarehouse() {

    const name =
        document.getElementById("name").value.trim();

    const code =
        document.getElementById("code").value.trim();

    const address =
        document.getElementById("address").value.trim();

    if (!name || !code || !address) {

        alert("Please fill all fields.");

        return;
    }

    try {

        const response = await fetch("/api/warehouses", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                name: name,
                code: code,
                address: address,
                active: true
            })
        });

        if (!response.ok) {

            const errorText = await response.text();

            throw new Error(errorText);
        }

        alert("Warehouse added successfully.");

        document.getElementById("name").value = "";
        document.getElementById("code").value = "";
        document.getElementById("address").value = "";

        loadWarehouses();

    } catch (error) {

        console.error(error);

        alert("Unable to add warehouse.");
    }
}


document.addEventListener(
    "DOMContentLoaded",
    loadWarehouses
);