"use strict";


let stockByProductChart = null;
let inventoryDistributionChart = null;

async function fetchJson(url) {
    const response = await fetch(url, {
        headers: {
            "Accept": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error(
            `Request failed: ${url} (${response.status})`
        );
    }

    return response.json();
}


async function loadDashboardStats() {
    try {
        const stats = await fetchJson(
            "/api/dashboard/stats"
        );

        setText("totalProducts", stats.totalProducts ?? 0);
        setText("availableStock", stats.availableStock ?? 0);
        setText("pendingOrders", stats.pendingOrders ?? 0);
        setText("lowStockItems", stats.lowStockItems ?? 0);

    } catch (error) {
        console.error(
            "Unable to load dashboard statistics:",
            error
        );
    }
}

function setText(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.textContent = value;
    }
}


async function loadInventory() {
    const inventory = await fetchJson("/api/inventory");

    if (!Array.isArray(inventory)) {
        throw new Error(
            "The inventory API did not return a list."
        );
    }

    return inventory;
}


function calculateProductStock(inventory) {
    const productMap = new Map();

    inventory.forEach(item => {
        if (!item.product) {
            return;
        }

        const productId = item.product.id;
        const productName = item.product.name || "Unknown Product";
        const quantity = Number(item.quantity) || 0;

        if (!productMap.has(productId)) {
            productMap.set(productId, {
                id: productId,
                name: productName,
                quantity: 0
            });
        }

        productMap.get(productId).quantity += quantity;
    });

    return Array.from(productMap.values())
        .sort((a, b) => b.quantity - a.quantity);
}


function setChartMessage(id, message, isError = false) {
    const element = document.getElementById(id);

    if (!element) {
        return;
    }

    element.textContent = message;
    element.classList.toggle("error", isError);
}


function renderStockByProductChart(products) {
    const canvas = document.getElementById(
        "stockByProductChart"
    );

    if (!canvas) {
        return;
    }

    if (typeof Chart === "undefined") {
        setChartMessage(
            "stockChartMessage",
            "Chart.js could not load. Check your internet connection.",
            true
        );
        return;
    }

    if (stockByProductChart) {
        stockByProductChart.destroy();
    }

    if (products.length === 0) {
        setChartMessage(
            "stockChartMessage",
            "No inventory records are available yet."
        );
        return;
    }

    setChartMessage("stockChartMessage", "");

    stockByProductChart = new Chart(canvas, {
        type: "bar",

        data: {
            labels: products.map(product => product.name),

            datasets: [{
                label: "Available Units",

                data: products.map(product => product.quantity),

                backgroundColor: "rgba(37, 99, 235, 0.75)",

                borderColor: "rgba(37, 99, 235, 1)",

                borderWidth: 1,

                borderRadius: 6
            }]
        },

        options: {
            responsive: true,

            maintainAspectRatio: false,

            indexAxis: "x",

            plugins: {
                legend: {
                    display: false
                },

                tooltip: {
                    callbacks: {
                        label: context =>
                            `Stock: ${context.parsed.y} units`
                    }
                }
            },

            scales: {
                x: {
                    title: {
                        display: true,
                        text: "Product"
                    }
                },

                y: {
                    beginAtZero: true,

                    title: {
                        display: true,
                        text: "Quantity"
                    },

                    ticks: {
                        precision: 0
                    }
                }
            }
        }
    });
}

/* ==========================================
   INVENTORY DISTRIBUTION DOUGHNUT CHART
   ========================================== */

function renderInventoryDistributionChart(products) {
    const canvas = document.getElementById(
        "inventoryDistributionChart"
    );

    if (!canvas) {
        return;
    }

    if (typeof Chart === "undefined") {
        setChartMessage(
            "distributionChartMessage",
            "Chart.js could not load. Check your internet connection.",
            true
        );
        return;
    }

    if (inventoryDistributionChart) {
        inventoryDistributionChart.destroy();
    }

    const productsWithStock = products.filter(
        product => product.quantity > 0
    );

    if (productsWithStock.length === 0) {
        setChartMessage(
            "distributionChartMessage",
            "No positive stock quantities are available to display."
        );
        return;
    }

    setChartMessage("distributionChartMessage", "");

    inventoryDistributionChart = new Chart(canvas, {
        type: "doughnut",

        data: {
            labels: productsWithStock.map(
                product => product.name
            ),

            datasets: [{
                label: "Inventory",

                data: productsWithStock.map(
                    product => product.quantity
                ),

                backgroundColor: [
                    "#2563eb",
                    "#16a34a",
                    "#f59e0b",
                    "#9333ea",
                    "#0891b2",
                    "#e11d48",
                    "#64748b",
                    "#84cc16"
                ],

                borderWidth: 2,

                borderColor: "#ffffff"
            }]
        },

        options: {
            responsive: true,

            maintainAspectRatio: false,

            plugins: {
                legend: {
                    position: "bottom"
                },

                tooltip: {
                    callbacks: {
                        label: context => {
                            const values =
                                context.dataset.data;

                            const total = values.reduce(
                                (sum, value) => sum + value,
                                0
                            );

                            const quantity = context.parsed;

                            const percentage = total > 0
                                ? (quantity / total * 100).toFixed(1)
                                : "0.0";

                            return `${context.label}: ${quantity} units (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

/* ==========================================
   LOAD BOTH INVENTORY CHARTS
   ========================================== */

async function loadInventoryCharts() {
    try {
        setChartMessage(
            "stockChartMessage",
            "Loading inventory data..."
        );

        setChartMessage(
            "distributionChartMessage",
            "Loading inventory data..."
        );

        const inventory = await loadInventory();

        const products = calculateProductStock(inventory);

        renderStockByProductChart(products);

        renderInventoryDistributionChart(products);

    } catch (error) {
        console.error(
            "Unable to load inventory charts:",
            error
        );

        setChartMessage(
            "stockChartMessage",
            "Unable to load stock data. Check the inventory API.",
            true
        );

        setChartMessage(
            "distributionChartMessage",
            "Unable to load inventory data.",
            true
        );
    }
}

/* ==========================================
   RULE-BASED REORDER RECOMMENDATIONS
   ========================================== */

async function loadAiReorders() {
    const message = document.getElementById(
        "aiReorderMessage"
    );

    const tableBody = document.getElementById(
        "aiReorderTableBody"
    );

    if (!message || !tableBody) {
        return;
    }

    message.textContent = "Loading reorder recommendations...";
    tableBody.replaceChildren();

    try {
        const inventory = await loadInventory();

        /*
         * Aggregate inventory by product.
         * Reorder thresholds are taken from the product's
         * reorderLevel field when available.
         */

        const productMap = new Map();

        inventory.forEach(item => {
            if (!item.product) {
                return;
            }

            const product = item.product;
            const productId = product.id;

            if (!productMap.has(productId)) {
                productMap.set(productId, {
                    id: productId,
                    name: product.name || "Unknown Product",
                    sku: product.sku || "-",
                    currentStock: 0,
                    reorderLevel: Number(product.reorderLevel) || 0
                });
            }

            productMap.get(productId).currentStock +=
                Number(item.quantity) || 0;
        });

        const recommendations = Array.from(
            productMap.values()
        ).map(product => {
            /*
             * Simple rule-based estimate only.
             * Predicted demand is not generated by an AI model.
             */

            const predictedDemand = Math.ceil(
                product.reorderLevel * 1.5
            );

            const recommendedStock = Math.max(
                product.reorderLevel,
                predictedDemand
            );

            const reorderQuantity = Math.max(
                0,
                recommendedStock - product.currentStock
            );

            let status = "Stock Available";

            if (product.currentStock <= product.reorderLevel) {
                status = "Reorder Required";
            } else if (
                product.currentStock <= predictedDemand
            ) {
                status = "Monitor Stock";
            }

            return {
                ...product,
                predictedDemand,
                recommendedStock,
                reorderQuantity,
                status
            };
        });

        recommendations.sort((a, b) =>
            a.currentStock - b.currentStock
        );

        if (recommendations.length === 0) {
            message.textContent =
                "No inventory records are available for recommendations.";
            return;
        }

        recommendations.forEach(product => {
            const row = document.createElement("tr");

            const values = [
                product.name,
                product.sku,
                product.currentStock,
                product.predictedDemand,
                product.reorderLevel,
                product.recommendedStock,
                product.reorderQuantity,
                product.status
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = String(value);
                row.appendChild(cell);
            });

            tableBody.appendChild(row);
        });

        message.textContent =
            "Rule-based stock recommendations loaded. Predicted demand is an illustrative estimate, not a machine-learning forecast.";

    } catch (error) {
        console.error(
            "Unable to load reorder recommendations:",
            error
        );

        message.textContent =
            "Unable to load recommendations. Check the inventory API.";
    }
}

/* ==========================================
   LOGOUT BUTTON
   ========================================== */

function setupLogout() {
    const logoutButton = document.getElementById(
        "logoutBtn"
    );

    if (!logoutButton) {
        return;
    }

    logoutButton.addEventListener("click", async () => {
        try {
            await fetch("/api/auth/logout", {
                method: "POST"
            });
        } catch (error) {
            console.error(
                "Logout request failed:",
                error
            );
        }

        window.location.href = "/login";
    });
}

/* ==========================================
   INITIALIZE DASHBOARD
   ========================================== */

document.addEventListener("DOMContentLoaded", async () => {
    setupLogout();

    await Promise.all([
        loadDashboardStats(),
        loadInventoryCharts(),
        loadAiReorders()
    ]);
});