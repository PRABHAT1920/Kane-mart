document.addEventListener("DOMContentLoaded", () => {
    loadProducts();

    const productForm = document.getElementById("productForm");

    if (productForm) {
        productForm.addEventListener("submit", addProduct);
    }
});


// ===============================
// Load Products
// ===============================

async function loadProducts() {

    try {

        const response = await fetch("/api/products");

        if (!response.ok) {
            throw new Error("Failed to load products");
        }

        const products = await response.json();

        displayProducts(products);

    } catch (error) {

        console.error("Error loading products:", error);

        showMessage(
            "Unable to load products",
            true
        );
    }
}


// ===============================
// Add Product
// ===============================

async function addProduct(event) {

    event.preventDefault();

    const name =
        document.getElementById("name").value.trim();

    const sku =
        document.getElementById("sku").value.trim();

    const price =
        parseFloat(document.getElementById("price").value);

    const quantity =
        parseInt(document.getElementById("quantity").value);

    const reorderLevel =
        parseInt(
            document.getElementById("reorderLevel").value
        );


    // Basic validation

    if (!name || !sku) {

        showMessage(
            "Product name and SKU are required",
            true
        );

        return;
    }

    if (isNaN(price) || price < 0) {

        showMessage(
            "Enter a valid price",
            true
        );

        return;
    }

    if (isNaN(quantity) || quantity < 0) {

        showMessage(
            "Enter a valid quantity",
            true
        );

        return;
    }

    if (isNaN(reorderLevel) || reorderLevel < 0) {

        showMessage(
            "Enter a valid reorder level",
            true
        );

        return;
    }


    const product = {

        name: name,
        sku: sku,
        price: price,
        quantity: quantity,
        reorderLevel: reorderLevel,
        active: true
    };


    try {

        const response = await fetch(
            "/api/products",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(product)
            }
        );


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(errorText);
        }


        const savedProduct =
            await response.json();

        console.log(
            "Product saved:",
            savedProduct
        );


        showMessage(
            "Product added successfully",
            false
        );


        document
            .getElementById("productForm")
            .reset();


        await loadProducts();


    } catch (error) {

        console.error(
            "Error adding product:",
            error
        );

        showMessage(
            "Unable to add product. SKU may already exist.",
            true
        );
    }
}


// ===============================
// Display Products
// ===============================

function displayProducts(products) {

    const tableBody =
        document.getElementById("productTableBody");

    if (!tableBody) {
        return;
    }


    tableBody.innerHTML = "";


    if (products.length === 0) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="7">
                    No products found
                </td>
            </tr>
        `;

        return;
    }


    products.forEach(product => {

        const row =
            document.createElement("tr");


        const status =
            product.quantity <= product.reorderLevel
                ? "Low Stock"
                : "Available";


        row.innerHTML = `

            <td>${product.id}</td>

            <td>${product.name}</td>

            <td>${product.sku}</td>

            <td>₹${product.price}</td>

            <td>${product.quantity}</td>

            <td>${product.reorderLevel}</td>

            <td>

                <span class="status">
                    ${status}
                </span>

                <button
                    class="delete-btn"
                    onclick="deleteProduct(${product.id})">
                    Delete
                </button>

            </td>
        `;


        tableBody.appendChild(row);

    });
}


// ===============================
// Delete Product
// ===============================

async function deleteProduct(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this product?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await fetch(
                `/api/products/${id}`,
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            throw new Error(
                "Failed to delete product"
            );
        }


        showMessage(
            "Product deleted successfully",
            false
        );


        await loadProducts();


    } catch (error) {

        console.error(
            "Error deleting product:",
            error
        );

        showMessage(
            "Unable to delete product",
            true
        );
    }
}


// ===============================
// Message
// ===============================

function showMessage(message, isError) {

    const messageElement =
        document.getElementById("message");


    if (!messageElement) {
        return;
    }


    messageElement.textContent =
        message;


    messageElement.style.color =
        isError ? "red" : "green";


    setTimeout(() => {

        messageElement.textContent = "";

    }, 3000);
}