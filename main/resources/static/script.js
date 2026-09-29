const API = "/api";

let flats = [];
let residents = [];
let approvals = [];

const pageTitles = {
    dashboard: "Dashboard",
    flats: "Flats",
    residents: "Residents",
    approvals: "Visitor Approvals",
    gate: "Gate Entry"
};

document.addEventListener("DOMContentLoaded", () => {
    setupNavigation();
    setupForms();
    setMinimumDateTimes();
    loadData();
});

function setupNavigation() {
    document.querySelectorAll(".nav-item").forEach(button => {
        button.addEventListener("click", () => {
            const section = button.dataset.section;

            document.querySelectorAll(".nav-item")
                .forEach(item => item.classList.remove("active"));

            document.querySelectorAll(".section")
                .forEach(item => item.classList.remove("active-section"));

            button.classList.add("active");

            document.getElementById(section)
                .classList.add("active-section");

            document.getElementById("pageTitle").textContent =
                pageTitles[section];

            if (section === "approvals") {
                loadApprovals();
            }
        });
    });
}

function setupForms() {
    document.getElementById("flatForm")
        .addEventListener("submit", createFlat);

    document.getElementById("residentForm")
        .addEventListener("submit", createResident);

    document.getElementById("approvalForm")
        .addEventListener("submit", createApproval);

    document.getElementById("otpForm")
        .addEventListener("submit", validateOtp);
}

async function loadData() {
    try {
        await Promise.all([
            loadFlats(),
            loadResidents()
        ]);

        await loadApprovals();

        updateDashboard();

        showMessage("Data refreshed.", "success");

    } catch (error) {
        showMessage(error.message, "error");
    }
}

async function loadFlats() {
    flats = await request("/flats");

    renderFlats();
    fillFlatSelects();
}

async function loadResidents() {
    residents = await request("/residents");

    renderResidents();
    fillResidentSelect();
}

async function loadApprovals() {
    if (!residents.length) {
        approvals = [];

        renderApprovals();
        updateDashboard();

        return;
    }

    const results = await Promise.all(
        residents.map(async resident => {

            const residentApprovals =
                await request(
                    `/approvals/resident/${resident.id}`
                ).catch(() => []);

            return residentApprovals.map(approval => ({
                ...approval,
                _residentId: resident.id
            }));
        })
    );

    const map = new Map();

    results.flat().forEach(approval => {
        map.set(approval.id, approval);
    });

    approvals = Array.from(map.values()).sort(
        (a, b) =>
            new Date(b.createdAt) -
            new Date(a.createdAt)
    );

    renderApprovals();
    updateDashboard();
}

function renderFlats() {
    const body = document.getElementById("flatsTable");

    if (!flats.length) {
        body.innerHTML =
            emptyRow(4, "No flats found.");

        return;
    }

    body.innerHTML = flats.map(flat => `
        <tr>
            <td>${flat.id}</td>
            <td>${escapeHtml(flat.flatNumber)}</td>
            <td>${escapeHtml(flat.block)}</td>
            <td>${flat.floor}</td>
        </tr>
    `).join("");
}

function renderResidents() {
    const body =
        document.getElementById("residentsTable");

    if (!residents.length) {
        body.innerHTML =
            emptyRow(5, "No residents found.");

        return;
    }

    body.innerHTML = residents.map(resident => `
        <tr>
            <td>${resident.id}</td>
            <td>${escapeHtml(resident.name)}</td>
            <td>${escapeHtml(resident.phone)}</td>
            <td>${escapeHtml(resident.email)}</td>
            <td>
                ${escapeHtml(
                    resident.flat?.flatNumber || "-"
                )}
            </td>
        </tr>
    `).join("");
}

function renderApprovals() {
    const body =
        document.getElementById("approvalsTable");

    if (!approvals.length) {
        body.innerHTML =
            emptyRow(7, "No visitor approvals found.");

        return;
    }

    body.innerHTML = approvals.map(approval => {

        const resident =
            residents.find(
                item => item.id === findResidentId(approval)
            );

        const canRevoke =
            approval.status === "PENDING";

        return `
            <tr>

                <td>
                    ${escapeHtml(approval.visitorName)}
                </td>

                <td>
                    ${escapeHtml(resident?.name || "-")}
                </td>

                <td>
                    ${formatDate(approval.visitDateTime)}
                </td>

                <td>
                    ${formatDate(approval.entryTime)}
                </td>

                <td>
                    ${escapeHtml(approval.purpose)}
                </td>

                <td>
                    <span class="status status-${approval.status}">
                        ${approval.status}
                    </span>
                </td>

                <td>

                    <button
                        class="action-btn"
                        onclick="showOtp(${approval.id})">
                        OTP
                    </button>

                    ${
                        canRevoke
                            ? `
                                <button
                                    class="action-btn"
                                    onclick="revokeApproval(${approval.id})">
                                    Revoke
                                </button>
                              `
                            : ""
                    }

                </td>

            </tr>
        `;
    }).join("");
}

function findResidentId(approval) {
    return approval._residentId || null;
}

function updateDashboard() {
    document.getElementById("totalFlats").textContent =
        flats.length;

    document.getElementById("totalResidents").textContent =
        residents.length;

    document.getElementById("pendingApprovals").textContent =
        approvals.filter(
            item => item.status === "PENDING"
        ).length;

    const body =
        document.getElementById("dashboardApprovals");

    const recent =
        approvals.slice(0, 8);

    if (!recent.length) {
        body.innerHTML =
            emptyRow(
                4,
                "No visitor approvals found."
            );

        return;
    }

    body.innerHTML = recent.map(approval => `
        <tr>

            <td>
                ${escapeHtml(approval.visitorName)}
            </td>

            <td>
                ${escapeHtml(approval.purpose)}
            </td>

            <td>
                ${formatDate(approval.visitDateTime)}
            </td>

            <td>
                ${formatDate(approval.entryTime)}
            </td>

            <td>
                <span class="status status-${approval.status}">
                    ${approval.status}
                </span>
            </td>

        </tr>
    `).join("");
}

async function createFlat(event) {
    event.preventDefault();

    const data = {
        flatNumber:
            document.getElementById("flatNumber")
                .value.trim(),

        block:
            document.getElementById("flatBlock")
                .value.trim(),

        floor:
            Number(
                document.getElementById("flatFloor")
                    .value
            )
    };

    try {

        await request("/flats", {
            method: "POST",
            body: data
        });

        event.target.reset();

        await loadFlats();

        updateDashboard();

        showMessage(
            "Flat added successfully.",
            "success"
        );

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

async function createResident(event) {
    event.preventDefault();

    const data = {
        name:
            document.getElementById("residentName")
                .value.trim(),

        phone:
            document.getElementById("residentPhone")
                .value.trim(),

        email:
            document.getElementById("residentEmail")
                .value.trim(),

        flatId:
            Number(
                document.getElementById("residentFlat")
                    .value
            )
    };

    try {

        await request("/residents", {
            method: "POST",
            body: data
        });

        event.target.reset();

        await loadResidents();

        updateDashboard();

        showMessage(
            "Resident added successfully.",
            "success"
        );

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

async function createApproval(event) {
    event.preventDefault();

    const visit =
        document.getElementById("visitDateTime")
            .value;

    const exit =
        document.getElementById("expectedExitTime")
            .value;

    if (new Date(exit) <= new Date(visit)) {

        showMessage(
            "Expected exit time must be after the visit time.",
            "error"
        );

        return;
    }

    const data = {
        residentId:
            Number(
                document.getElementById("approvalResident")
                    .value
            ),

        visitorName:
            document.getElementById("visitorName")
                .value.trim(),

        visitorPhone:
            document.getElementById("visitorPhone")
                .value.trim(),

        visitDateTime: visit,

        expectedExitTime: exit,

        purpose:
            document.getElementById("purpose")
                .value.trim()
    };

    try {

        const approval =
            await request("/approvals", {
                method: "POST",
                body: data
            });

        event.target.reset();

        setMinimumDateTimes();

        await loadApprovals();

        updateDashboard();

        showMessage(
            `Approval created successfully. Approval ID: ${approval.id}`,
            "success"
        );

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

async function revokeApproval(id) {

    if (!confirm("Revoke this visitor approval?")) {
        return;
    }

    try {

        await request(
            `/approvals/${id}/revoke`,
            {
                method: "PUT"
            }
        );

        await loadApprovals();

        showMessage(
            "Visitor approval revoked.",
            "success"
        );

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

async function showOtp(approvalId) {

    try {

        const pass =
            await request(
                `/otp/approval/${approvalId}`
            );

        alert(
            `OTP: ${pass.otp}\n` +
            `Expires: ${formatDate(pass.expiresAt)}\n` +
            `Used: ${pass.used ? "Yes" : "No"}`
        );

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

async function validateOtp(event) {
    event.preventDefault();

    const otp =
        document.getElementById("otpInput")
            .value.trim();

    const result =
        document.getElementById("entryResult");

    try {

        const response =
            await request("/otp/validate", {
                method: "POST",
                body: {
                    otp
                }
            });

        result.className =
            "entry-result success";

        result.innerHTML = `
            <strong>Entry Allowed</strong><br>
            Visitor:
            ${escapeHtml(response.visitorName)}
            <br>
            Resident:
            ${escapeHtml(response.residentName)}
            <br>
            Flat:
            ${escapeHtml(response.flatNumber)}
            <br>
            Entry Time:
            ${formatDate(response.entryTime)}
        `;

        result.classList.remove("hidden");

        event.target.reset();

        await loadApprovals();

    } catch (error) {

        result.className =
            "entry-result error";

        result.textContent =
            error.message;

        result.classList.remove("hidden");
    }
}

function fillFlatSelects() {

    const select =
        document.getElementById("residentFlat");

    select.innerHTML =
        '<option value="">Select flat</option>' +

        flats.map(flat => `
            <option value="${flat.id}">
                ${escapeHtml(flat.flatNumber)}
                - Block
                ${escapeHtml(flat.block)}
            </option>
        `).join("");
}

function fillResidentSelect() {

    const select =
        document.getElementById("approvalResident");

    select.innerHTML =
        '<option value="">Select resident</option>' +

        residents.map(resident => `
            <option value="${resident.id}">
                ${escapeHtml(resident.name)}
                -
                ${escapeHtml(
                    resident.flat?.flatNumber ||
                    "No flat"
                )}
            </option>
        `).join("");
}

function setMinimumDateTimes() {

    const now = new Date();

    now.setMinutes(
        now.getMinutes() -
        now.getTimezoneOffset()
    );

    const value =
        now.toISOString().slice(0, 16);

    document.getElementById("visitDateTime").min =
        value;

    document.getElementById("expectedExitTime").min =
        value;
}

async function request(path, options = {}) {

    const config = {
        method: options.method || "GET",

        headers: {
            "Content-Type": "application/json"
        }
    };

    if (options.body !== undefined) {
        config.body =
            JSON.stringify(options.body);
    }

    const response =
        await fetch(API + path, config);

    const text =
        await response.text();

    let data = {};

    try {

        data =
            text ? JSON.parse(text) : {};

    } catch {

        data = {
            message: text
        };
    }

    if (!response.ok) {

        throw new Error(
            getErrorMessage(
                data,
                response.status
            )
        );
    }

    return data;
}

function getErrorMessage(data, status) {

    if (data.message) {

        if (data.fields) {

            const fields =
                Object.entries(data.fields)
                    .map(
                        ([field, message]) =>
                            `${field}: ${message}`
                    )
                    .join("; ");

            return `${data.message} - ${fields}`;
        }

        return data.message;
    }

    return `Request failed with status ${status}.`;
}

function showMessage(message, type) {

    const element =
        document.getElementById("message");

    element.textContent =
        message;

    element.className =
        `message ${type}`;

    clearTimeout(showMessage.timer);

    showMessage.timer =
        setTimeout(() => {
            element.classList.add("hidden");
        }, 4000);
}

function formatDate(value) {

    if (!value) {
        return "-";
    }

    const date =
        new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString();
}

function emptyRow(columns, message) {

    return `
        <tr>
            <td colspan="${columns}" class="empty">
                ${message}
            </td>
        </tr>
    `;
}

function escapeHtml(value) {

    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}