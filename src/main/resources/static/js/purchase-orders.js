const supplierSelect = document.getElementById("supplierId");
const productSelect = document.getElementById("productId");
const purchaseOrderForm =
    document.getElementById("purchaseOrderForm");

const purchaseOrderTableBody =
    document.getElementById("purchaseOrderTableBody");

const message =
    document.getElementById("message");


async function loadSuppliers() {

    const response =
        await fetch("/api/suppliers");

    if (!response.ok) {
        throw new Error("Failed to load suppliers");
    }

    const suppliers =
        await response.json();

    supplierSelect.innerHTML =
        '<option value="">Select Supplier</option>';

    suppliers.forEach(supplier => {

        const option =
            document.createElement("option");

        option.value = supplier.id;

        option.textContent =
            supplier.name;

        supplierSelect.appendChild(option);
    });
}


async function loadProducts() {

    const response =
        await fetch("/api/products/active");

    if (!response.ok) {
        throw new Error("Failed to load products");
    }

    const products =
        await response.json();

    productSelect.innerHTML =
        '<option value="">Select Product</option>';

    products.forEach(product => {

        const option =
            document.createElement("option");

        option.value = product.id;

        option.textContent =
            `${product.name} (${product.sku}) - ₹${product.price}`;

        productSelect.appendChild(option);
    });
}


async function loadPurchaseOrders() {

    const response =
        await fetch("/api/purchase-orders");

    if (!response.ok) {
        throw new Error("Failed to load purchase orders");
    }

    const orders =
        await response.json();

    purchaseOrderTableBody.innerHTML = "";

    orders.forEach(order => {

        const row =
            document.createElement("tr");

        row.innerHTML = `
            <td>${order.id}</td>
            <td>${order.purchaseOrderNumber}</td>
            <td>${order.supplier.name}</td>
            <td>₹${order.totalAmount}</td>
            <td>${order.status}</td>
            <td>${order.orderDate}</td>
        `;

        purchaseOrderTableBody.appendChild(row);
    });
}


purchaseOrderForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const supplierId =
            supplierSelect.value;

        const productId =
            productSelect.value;

        const quantity =
            document.getElementById("quantity").value;

        try {

            const response =
                await fetch(
                    `/api/purchase-orders?supplierId=${supplierId}&productId=${productId}&quantity=${quantity}`,
                    {
                        method: "POST"
                    }
                );

            if (!response.ok) {

                const error =
                    await response.text();

                throw new Error(error);
            }

            message.textContent =
                "Purchase order created successfully.";

            purchaseOrderForm.reset();

            await loadPurchaseOrders();

        } catch (error) {

            console.error(error);

            message.textContent =
                "Failed to create purchase order.";
        }
    }
);


async function initializePage() {

    try {

        await loadSuppliers();

        await loadProducts();

        await loadPurchaseOrders();

    } catch (error) {

        console.error(error);

        message.textContent =
            "Unable to load purchase order data.";
    }
}


initializePage();