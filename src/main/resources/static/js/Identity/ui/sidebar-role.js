document.addEventListener("DOMContentLoaded", () => {
    const role        = (localStorage.getItem("role")        || "").trim();
    const displayName = (localStorage.getItem("displayName") || "").trim();

    document.querySelectorAll("[data-role]").forEach(el => {
        const allowed = (el.getAttribute("data-role") || "").trim();
        el.style.display = (role && allowed === role) ? "" : "none";
    });

    const roleLabelTop    = document.getElementById("sidebar-role-label");
    const roleLabelBottom = document.getElementById("sidebar-role-text");
    const nameTarget      = document.getElementById("sidebar-display-name");

    if (roleLabelTop)    roleLabelTop.textContent    = role        || "ROLE";
    if (roleLabelBottom) roleLabelBottom.textContent = role        || "ROLE";
    if (nameTarget)      nameTarget.textContent      = displayName || "Utilisateur";
});
