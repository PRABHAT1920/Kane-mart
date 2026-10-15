async function loadProducts() {

    try {

        const response =
            await fetch("/api/products/active");

        if (!response.ok) {
            throw new Error("Failed to load products");
        }

        const products = await response.json();

        const select =
            document.getElementById("product");

        select.innerHTML =
            '<option value="">Select Product</option>';

        products.forEach(product => {

            select.innerHTML += `
                <option value="${product.id}">
                    ${product.name} (${product.sku})
                </option>
            `;

        });

    } catch (error) {

        console.error(error);

    }
}


async function loadLocations() {

    try {

        const response =
            await fetch("/api/locations");

        if (!response.ok) {
            throw new Error("Failed to load locations");
        }

        const locations = await response.json();

        const select =
            document.getElementById("location");

        select.innerHTML =
            '<option value="">Select Location</option>';

        locations.forEach(location => {

            const warehouse =
                location.warehouse
                    ? location.warehouse.name
                    : "Warehouse";

            select.innerHTML += `
                <option value="${location.id}">
                    ${warehouse} -
                    ${location.zone} /
                    ${location.rack} /
                    ${location.shelf} /
                    ${location.bin}
                </option>
            `;

        });

    } catch (error) {

        console.error(error);

    }
}


async function loadInventory() {

    try {

        const response =
            await fetch("/api/inventory");

        if (!response.ok) {
            throw new Error("Failed to load inventory");
        }

        const inventory =
            await response.json();

        displayInventory(inventory);

    } catch (error) {

        console.error(error);

        document.getElementById("message").innerText =
            "Unable to load inventory.";
    }
}


function displayInventory(inventory) {

    const table =
        document.getElementById("inventoryTable");

    table.innerHTML = "";

    if (inventory.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="7">
                    No inventory found
                </td>
            </tr>
        `;

        return;
    }

    inventory.forEach(item => {

        const product =
            item.product || {};

        const location =
            item.location || {};

        const warehouse =
            location.warehouse
                ? location.warehouse.name
                : "N/A";

        const locationText =
            `${warehouse} - ${location.zone || ""}
             / ${location.rack || ""}
             / ${location.shelf || ""}
             / ${location.bin || ""}`;

        const status =
            item.quantity <= item.minimumStock
                ? "LOW STOCK"
                : "AVAILABLE";

        table.innerHTML += `
            <tr>
                <td>${item.id}</td>

                <td>${product.name || "N/A"}</td>

                <td>${product.sku || "N/A"}</td>

                <td>${locationText}</td>

                <td>${item.quantity}</td>

                <td>${item.minimumStock}</td>

                <td>${status}</td>
            </tr>
        `;
    });
}


async function addInventory() {

    const productId =
        document.getElementById("product").value;

    const locationId =
        document.getElementById("location").value;

    const quantity =
        document.getElementById("quantity").value;

    const minimumStock =
        document.getElementById("minimumStock").value;

    if (
        !productId ||
        !locationId ||
        quantity === "" ||
        minimumStock === ""
    ) {

        alert("Please fill all fields.");

        return;
    }

    try {

        const url =
            `/api/inventory?productId=${productId}` +
            `&locationId=${locationId}` +
            `&quantity=${quantity}` +
            `&minimumStock=${minimumStock}`;

        const response =
            await fetch(url, {
                method: "POST"
            });

        if (!response.ok) {

            const error =
                await response.text();

            throw new Error(error);
        }

        alert("Inventory added successfully.");

        document.getElementById("quantity").value = "";
        document.getElementById("minimumStock").value = "";

        loadInventory();

    } catch (error) {

        console.error(error);

        alert("Unable to add inventory.");
    }
}


async function loadLowStock() {

    try {

        const response =
            await fetch("/api/inventory/low-stock");

        if (!response.ok) {
            throw new Error("Failed to load low stock");
        }

        const inventory =
            await response.json();

        displayInventory(inventory);

    } catch (error) {

        console.error(error);

        document.getElementById("message").innerText =
            "Unable to load low-stock inventory.";
    }
}


document.addEventListener("DOMContentLoaded", () => {

    loadProducts();
    loadLocations();
    loadInventory();

});