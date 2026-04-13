document.addEventListener("DOMContentLoaded", () => {
    const role = (localStorage.getItem("role") || "").trim();
    const displayName = (localStorage.getItem("displayName") || "").trim();

    console.log("ROLE localStorage =", role);
    console.log("DISPLAY NAME localStorage =", displayName);

    const debugRole = document.getElementById("sidebar-debug-role");
    const debugName = document.getElementById("sidebar-debug-name");

    if (debugRole) {
        debugRole.textContent = "currentRole = " + (role || "NULL");
    }

    if (debugName) {
        debugName.textContent = "displayName = " + (displayName || "NULL");
    }

    document.querySelectorAll("[data-role]").forEach((el) => {
        const allowedRole = (el.getAttribute("data-role") || "").trim();

        if (role && allowedRole === role) {
            el.style.display = "";
        } else {
            el.style.display = "none";
        }
    });

    const roleLabelTop = document.getElementById("sidebar-role-label");
    if (roleLabelTop) {
        roleLabelTop.textContent = role || "ROLE";
    }

    const roleLabelBottom = document.getElementById("sidebar-role-text");
    if (roleLabelBottom) {
        roleLabelBottom.textContent = role || "ROLE";
    }

    const nameTarget = document.getElementById("sidebar-display-name");
    if (nameTarget) {
        nameTarget.textContent = displayName || "Utilisateur";
    }
});