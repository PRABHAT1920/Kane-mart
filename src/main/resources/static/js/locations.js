async function loadWarehouses() {

    try {

        const response = await fetch("/api/warehouses/active");

        if (!response.ok) {
            throw new Error("Failed to load warehouses");
        }

        const warehouses = await response.json();

        const select = document.getElementById("warehouse");

        select.innerHTML =
            '<option value="">Select Warehouse</option>';

        warehouses.forEach(warehouse => {

            select.innerHTML += `
                <option value="${warehouse.id}">
                    ${warehouse.name} (${warehouse.code})
                </option>
            `;

        });

    } catch (error) {

        console.error(error);

        document.getElementById("message").innerText =
            "Unable to load warehouses.";
    }
}


async function loadLocations() {

    try {

        const response = await fetch("/api/locations");

        if (!response.ok) {
            throw new Error("Failed to load locations");
        }

        const locations = await response.json();

        const table = document.getElementById("locationTable");

        table.innerHTML = "";

        if (locations.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="6">
                        No locations found
                    </td>
                </tr>
            `;

            return;
        }

        locations.forEach(location => {

            const warehouseName =
                location.warehouse
                    ? location.warehouse.name
                    : "N/A";

            table.innerHTML += `
                <tr>
                    <td>${location.id}</td>
                    <td>${warehouseName}</td>
                    <td>${location.zone || ""}</td>
                    <td>${location.rack || ""}</td>
                    <td>${location.shelf || ""}</td>
                    <td>${location.bin || ""}</td>
                </tr>
            `;

        });

    } catch (error) {

        console.error(error);

        document.getElementById("message").innerText =
            "Unable to load locations.";
    }
}


async function addLocation() {

    const warehouseId =
        document.getElementById("warehouse").value;

    const zone =
        document.getElementById("zone").value.trim();

    const rack =
        document.getElementById("rack").value.trim();

    const shelf =
        document.getElementById("shelf").value.trim();

    const bin =
        document.getElementById("bin").value.trim();

    if (!warehouseId || !zone || !rack || !shelf || !bin) {

        alert("Please fill all fields.");

        return;
    }

    try {

        const params = new URLSearchParams();

        params.append("warehouseId", warehouseId);

        const response = await fetch(
            "/api/locations?" + params.toString(),
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    zone: zone,
                    rack: rack,
                    shelf: shelf,
                    bin: bin
                })
            }
        );

        if (!response.ok) {

            const errorText = await response.text();

            throw new Error(errorText);
        }

        alert("Location added successfully.");

        document.getElementById("zone").value = "";
        document.getElementById("rack").value = "";
        document.getElementById("shelf").value = "";
        document.getElementById("bin").value = "";

        loadLocations();

    } catch (error) {

        console.error(error);

        alert("Unable to add location.");
    }
}


document.addEventListener("DOMContentLoaded", () => {

    loadWarehouses();
    loadLocations();

});