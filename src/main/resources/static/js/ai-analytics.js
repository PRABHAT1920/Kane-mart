// =====================================================
// KANEMART SMART INVENTORY AI — ANALYTICS DASHBOARD
// =====================================================

let demandChart = null;
let salesTrendChart = null;

const productSelect = document.getElementById("productSelect");
const forecastDays = document.getElementById("forecastDays");
const statusMessage = document.getElementById("statusMessage");

// =====================================================
// INITIALIZATION
// =====================================================

document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById("analyseButton")
        .addEventListener("click", analyseDemand);

    document.getElementById("refreshButton")
        .addEventListener("click", analyseDemand);

    productSelect.addEventListener("change", analyseDemand);
    forecastDays.addEventListener("change", analyseDemand);

    await loadProducts();
});

// =====================================================
// STATUS MESSAGE
// =====================================================

function setStatus(message) {
    if (statusMessage) {
        statusMessage.textContent = message;
    }
}

// =====================================================
// API HELPER
// =====================================================

async function fetchJson(url) {
    const response = await fetch(url);

    if (!response.ok) {
        throw new Error(
            `Request failed (${response.status}): ${url}`
        );
    }

    return response.json();
}

// =====================================================
// LOAD PRODUCTS
// =====================================================

async function loadProducts() {
    try {
        setStatus("Loading products...");

        const products = await fetchJson("/api/products");

        productSelect.innerHTML = "";

        if (!Array.isArray(products) || products.length === 0) {
            productSelect.innerHTML =
                '<option value="">No products available</option>';

            setStatus("Add a product before running AI analysis.");
            return;
        }

        products.forEach(product => {
            const option = document.createElement("option");

            option.value = product.id;
            option.textContent =
                `${product.name} (${product.sku || "No SKU"})`;

            productSelect.appendChild(option);
        });

        await analyseDemand();

    } catch (error) {
        console.error("Product loading error:", error);

        setStatus(
            "Unable to load products. Check the backend and browser console."
        );
    }
}

// =====================================================
// ANALYSE DEMAND
// =====================================================

async function analyseDemand() {
    const productId = productSelect.value;
    const days = Number(forecastDays.value);

    if (!productId) {
        setStatus("Select a product to analyse.");
        return;
    }

    if (![7, 15, 30].includes(days)) {
        setStatus("Select a valid forecast period.");
        return;
    }

    setStatus("Analysing sales history and forecasting demand...");

    try {
        const [products, historyResponse, forecastResponse] =
            await Promise.all([
                fetchJson("/api/products"),
                fetchJson(`/api/ai/demand-history/${productId}`),
                fetchJson(
                    `/api/ai/forecast/${productId}?days=${days}`
                )
            ]);

        const product = products.find(
            item => String(item.id) === String(productId)
        );

        if (!product) {
            throw new Error("Selected product was not found.");
        }

        const history = normalizeHistory(historyResponse);

        const predictedTotal = normalizeForecast(forecastResponse);

        const averageDailyDemand = history.length > 0
            ? history.reduce(
                (sum, item) => sum + item.quantity,
                0
            ) / history.length
            : 0;

        // Update summary cards.
        renderSummary(
            product,
            averageDailyDemand,
            predictedTotal
        );

        // Render demand forecast graph.
        renderDemandChart(
            history,
            averageDailyDemand,
            days
        );

        // Update inventory recommendation.
        renderRecommendation(
            Number(product.quantity || 0),
            Number(product.reorderLevel || 0),
            predictedTotal,
            days,
            averageDailyDemand
        );

        setStatus(
            `Analysis completed for ${product.name}. ` +
            `Forecast period: ${days} days.`
        );

    } catch (error) {
        console.error("AI analysis error:", error);

        setStatus(
            "Demand analysis failed. Check the forecast API, " +
            "demand-history API, and browser console."
        );
    }

    // Load sales analytics separately so that a sales API
    // failure does not prevent the demand forecast from working.
    await loadSalesIntelligence(productId);
}

// =====================================================
// NORMALIZE HISTORICAL SALES
// =====================================================

function normalizeHistory(response) {
    let rows = [];

    if (Array.isArray(response)) {
        rows = response.map(item => ({
            date: item.date || item.day,
            quantity: Number(
                item.quantity ??
                item.demand ??
                item.sales ??
                0
            )
        }));

    } else if (
        response &&
        typeof response === "object"
    ) {
        rows = Object.entries(response).map(
            ([date, quantity]) => ({
                date,
                quantity: Number(quantity)
            })
        );
    }

    return rows
        .filter(item =>
            item.date &&
            Number.isFinite(item.quantity)
        )
        .sort((a, b) =>
            a.date.localeCompare(b.date)
        );
}

// =====================================================
// NORMALIZE FORECAST RESPONSE
// =====================================================

function normalizeForecast(response) {
    if (typeof response === "number") {
        return Math.max(0, response);
    }

    if (response && typeof response === "object") {
        const value =
            response.predictedDemand ??
            response.forecastDemand ??
            response.forecast ??
            response.demand;

        if (
            value !== undefined &&
            Number.isFinite(Number(value))
        ) {
            return Math.max(0, Number(value));
        }
    }

    throw new Error(
        "Unexpected forecast API response. Inspect /api/ai/forecast."
    );
}

// =====================================================
// UPDATE SUMMARY CARDS
// =====================================================

function renderSummary(
    product,
    averageDailyDemand,
    predictedTotal
) {
    document.getElementById("productName").textContent =
        product.name;

    document.getElementById("currentStock").textContent =
        Number(product.quantity || 0).toLocaleString();

    document.getElementById("averageDemand").textContent =
        averageDailyDemand.toFixed(2);

    document.getElementById("predictedDemand").textContent =
        Math.ceil(predictedTotal).toLocaleString();
}

// =====================================================
// DEMAND FORECAST GRAPH
// =====================================================

function renderDemandChart(
    history,
    averageDailyDemand,
    days
) {
    if (typeof Chart === "undefined") {
        throw new Error(
            "Chart.js is unavailable. Check the CDN connection."
        );
    }

    const canvas = document.getElementById("demandChart");

    if (!canvas) {
        throw new Error(
            'The HTML canvas with id="demandChart" was not found.'
        );
    }

    const labels = history.map(item => item.date);
    const actualValues = history.map(item => item.quantity);

    const lastHistoryDate = history.length > 0
        ? new Date(
            history[history.length - 1].date + "T12:00:00"
        )
        : new Date();

    const forecastLabels = [];
    const forecastValues = [];

    for (let i = 1; i <= days; i++) {
        const nextDate = new Date(lastHistoryDate);

        nextDate.setDate(nextDate.getDate() + i);

        forecastLabels.push(
            nextDate.toISOString().slice(0, 10)
        );

        forecastValues.push(
            Number(averageDailyDemand.toFixed(2))
        );
    }

    const combinedLabels = [
        ...labels,
        ...forecastLabels
    ];

    const actualSeries = [
        ...actualValues,
        ...Array(days).fill(null)
    ];

    const forecastSeries = [
        ...Array(Math.max(0, labels.length - 1)).fill(null),

        ...(labels.length > 0
            ? [actualValues[actualValues.length - 1]]
            : []),

        ...forecastValues
    ];

    if (demandChart) {
        demandChart.destroy();
    }

    demandChart = new Chart(
        canvas.getContext("2d"),
        {
            type: "line",

            data: {
                labels: combinedLabels,

                datasets: [
                    {
                        label: "Historical Daily Sales",
                        data: actualSeries,
                        borderColor: "#2563eb",
                        backgroundColor:
                            "rgba(37, 99, 235, 0.12)",
                        borderWidth: 3,
                        pointRadius: 3,
                        tension: 0.25,
                        spanGaps: false
                    },
                    {
                        label: "Estimated Daily Demand",
                        data: forecastSeries,
                        borderColor: "#9333ea",
                        backgroundColor:
                            "rgba(147, 51, 234, 0.10)",
                        borderDash: [7, 5],
                        borderWidth: 3,
                        pointRadius: 2,
                        tension: 0.2,
                        spanGaps: false
                    }
                ]
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,

                interaction: {
                    intersect: false,
                    mode: "index"
                },

                plugins: {
                    legend: {
                        position: "bottom"
                    },

                    tooltip: {
                        callbacks: {
                            label: context => {
                                if (context.parsed.y === null) {
                                    return "";
                                }

                                return (
                                    `${context.dataset.label}: ` +
                                    `${context.parsed.y} units`
                                );
                            }
                        }
                    }
                },

                scales: {
                    x: {
                        title: {
                            display: true,
                            text: "Date"
                        }
                    },

                    y: {
                        beginAtZero: true,

                        title: {
                            display: true,
                            text: "Units sold / estimated demand"
                        }
                    }
                }
            }
        }
    );
}

// =====================================================
// INVENTORY DECISION SUPPORT
// =====================================================

function renderRecommendation(
    currentStock,
    reorderLevel,
    predictedTotal,
    days,
    averageDailyDemand
) {
    const statusElement =
        document.getElementById("recommendationStatus");

    const detailsElement =
        document.getElementById("recommendationDetails");

    statusElement.className = "recommendation-status";

    const estimatedDailyDemand = predictedTotal / days;

    const estimatedDaysRemaining =
        estimatedDailyDemand > 0
            ? currentStock / estimatedDailyDemand
            : null;

    if (currentStock <= reorderLevel) {
        statusElement.textContent =
            "URGENT: REORDER REVIEW";

        statusElement.classList.add("urgent");

        detailsElement.textContent =
            `Current stock is ${currentStock} units, at or below ` +
            `the configured reorder level of ${reorderLevel}. ` +
            "Review replenishment promptly.";

    } else if (
        estimatedDaysRemaining !== null &&
        estimatedDaysRemaining < days
    ) {
        statusElement.textContent = "STOCK RISK";

        statusElement.classList.add("warning");

        detailsElement.textContent =
            `At the estimated daily demand of ` +
            `${estimatedDailyDemand.toFixed(2)} units, current stock ` +
            `may last approximately ` +
            `${estimatedDaysRemaining.toFixed(1)} days. ` +
            `The selected forecast period is ${days} days. ` +
            "Review supplier lead time and replenishment needs.";

    } else {
        statusElement.textContent =
            "STOCK APPEARS SUFFICIENT";

        statusElement.classList.add("good");

        detailsElement.textContent =
            `Current stock is ${currentStock} units. ` +
            `Estimated demand over ${days} days is ` +
            `${Math.ceil(predictedTotal)} units. ` +
            "This comparison does not yet account for supplier " +
            "lead time, safety stock, or reserved inventory.";
    }

    if (averageDailyDemand === 0) {
        detailsElement.textContent +=
            " No historical sales were found, so the forecast may " +
            "be zero or unreliable. Record sales before relying " +
            "on this prediction.";
    }
}

// =====================================================
// SALES INTELLIGENCE
// =====================================================

async function loadSalesIntelligence(productId) {
    const statusElement =
        document.getElementById("salesStatusMessage");

    // The Sales Intelligence section must exist in the HTML.
    if (!statusElement) {
        console.warn(
            "Sales Intelligence section is missing from the HTML."
        );
        return;
    }

    if (!productId) {
        statusElement.textContent =
            "Select a product to load sales intelligence.";
        return;
    }

    try {
        statusElement.textContent =
            "Loading sales intelligence...";

        const data = await fetchJson(
            `/api/ai/sales/trend/${productId}`
        );

        // Update sales summary cards.
        const totalUnitsElement =
            document.getElementById("totalUnitsSold");

        const averageElement =
            document.getElementById("averageDailySales");

        const daysElement =
            document.getElementById("daysWithSales");

        const trendElement =
            document.getElementById("salesTrend");

        if (totalUnitsElement) {
            totalUnitsElement.textContent =
                Number(data.totalUnitsSold ?? 0).toLocaleString();
        }

        if (averageElement) {
            averageElement.textContent =
                Number(data.averageDailySales ?? 0).toFixed(2);
        }

        if (daysElement) {
            daysElement.textContent =
                Number(data.daysWithSales ?? 0).toLocaleString();
        }

        if (trendElement) {
            trendElement.textContent =
                data.trend ?? "INSUFFICIENT_DATA";
        }

        // Extract the daily sales map.
        const dailySales =
            data.dailySales &&
            typeof data.dailySales === "object"
                ? data.dailySales
                : {};

        const dates = Object.keys(dailySales).sort();

        const quantities = dates.map(date =>
            Number(dailySales[date])
        );

        const readableDates = dates.map(date => {
            const parsedDate =
                new Date(`${date}T12:00:00`);

            if (Number.isNaN(parsedDate.getTime())) {
                return date;
            }

            return parsedDate.toLocaleDateString(
                "en-IN",
                {
                    day: "numeric",
                    month: "short"
                }
            );
        });

        // Find the graph canvas.
        const canvas =
            document.getElementById("salesTrendChart");

        if (!canvas) {
            statusElement.textContent =
                "Sales data loaded, but the sales graph canvas is missing.";
            return;
        }

        if (typeof Chart === "undefined") {
            throw new Error(
                "Chart.js did not load. Check the CDN and internet connection."
            );
        }

        // Destroy the previous graph before rendering another.
        if (salesTrendChart) {
            salesTrendChart.destroy();
            salesTrendChart = null;
        }

        // Render Sales Trend Graph.
        salesTrendChart = new Chart(
            canvas.getContext("2d"),
            {
                type: "line",

                data: {
                    labels: readableDates,

                    datasets: [
                        {
                            label: "Units Sold",
                            data: quantities,
                            borderColor: "#0f766e",
                            backgroundColor:
                                "rgba(15, 118, 110, 0.12)",
                            borderWidth: 3,
                            pointRadius: 5,
                            pointHoverRadius: 7,
                            tension: 0.3,
                            fill: true
                        }
                    ]
                },

                options: {
                    responsive: true,
                    maintainAspectRatio: false,

                    interaction: {
                        intersect: false,
                        mode: "index"
                    },

                    plugins: {
                        legend: {
                            position: "bottom"
                        },

                        tooltip: {
                            callbacks: {
                                label: context =>
                                    `Units sold: ${context.parsed.y}`
                            }
                        }
                    },

                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: "Sales Date"
                            }
                        },

                        y: {
                            beginAtZero: true,

                            ticks: {
                                precision: 0
                            },

                            title: {
                                display: true,
                                text: "Units Sold"
                            }
                        }
                    }
                }
            }
        );

        statusElement.textContent = dates.length > 0
            ? `Sales analysis loaded successfully. ` +
              `${dates.length} dates contain recorded sales.`
            : "No recorded sales are available for this product yet.";

    } catch (error) {
        console.error(
            "Sales intelligence error:",
            error
        );

        statusElement.textContent =
            "Unable to load sales intelligence. " +
            "Check the sales API and browser console.";
    }
}