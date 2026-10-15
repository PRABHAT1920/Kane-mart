async function loadEmployees() {
    try {
        const response = await fetch("/api/employees");

        if (!response.ok) {
            throw new Error("Failed to load employees");
        }

        const employees = await response.json();

        const table = document.getElementById("employeeTable");
        table.innerHTML = "";

        if (employees.length === 0) {
            table.innerHTML = `
                <tr>
                    <td colspan="5">No employees found</td>
                </tr>
            `;
            return;
        }

        employees.forEach(employee => {

            table.innerHTML += `
                <tr>
                    <td>${employee.id}</td>
                    <td>${employee.name}</td>
                    <td>${employee.mobile}</td>
                    <td>${employee.role}</td>
                    <td>${employee.active ? "ACTIVE" : "INACTIVE"}</td>
                </tr>
            `;
        });

    } catch (error) {
        console.error(error);

        document.getElementById("message").innerText =
            "Unable to load employees.";
    }
}


async function addEmployee() {

    const name = document.getElementById("name").value.trim();
    const mobile = document.getElementById("mobile").value.trim();
    const role = document.getElementById("role").value.trim();

    if (!name || !mobile || !role) {
        alert("Please fill all fields.");
        return;
    }

    try {

        const response = await fetch("/api/employees", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: name,
                mobile: mobile,
                role: role,
                active: true
            })
        });

        if (!response.ok) {
            throw new Error("Failed to add employee");
        }

        alert("Employee added successfully.");

        document.getElementById("name").value = "";
        document.getElementById("mobile").value = "";
        document.getElementById("role").value = "";

        loadEmployees();

    } catch (error) {
        console.error(error);
        alert("Unable to add employee.");
    }
}


document.addEventListener("DOMContentLoaded", loadEmployees);