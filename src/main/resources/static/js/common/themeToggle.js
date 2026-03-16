const STORAGE_KEY = "kemoschool.theme";

function getCurrentTheme() {
  return document.documentElement.classList.contains("dark") ? "dark" : "light";
}

function setTheme(theme) {
  const isDark = theme === "dark";
  document.documentElement.classList.toggle("dark", isDark);
  try {
    localStorage.setItem(STORAGE_KEY, isDark ? "dark" : "light");
  } catch {
    // Ignore storage errors (private mode, disabled storage, etc.)
  }
  syncToggleButtons();
}

function syncToggleButtons() {
  const theme = getCurrentTheme();
  const isDark = theme === "dark";

  document.querySelectorAll("[data-theme-toggle]").forEach((btn) => {
    const sun = btn.querySelector('[data-theme-icon="sun"]');
    const moon = btn.querySelector('[data-theme-icon="moon"]');
    const label = btn.querySelector("[data-theme-toggle-label]");

    if (sun) sun.classList.toggle("hidden", !isDark);
    if (moon) moon.classList.toggle("hidden", isDark);
    if (label) label.textContent = isDark ? "Passer en theme clair" : "Passer en theme sombre";
  });
}

function initThemeFromStorage() {
  let stored = null;
  try {
    stored = localStorage.getItem(STORAGE_KEY);
  } catch {
    stored = null;
  }
  if (stored === "dark" || stored === "light") {
    setTheme(stored);
  } else {
    // Keep server default, just sync icons.
    syncToggleButtons();
  }
}

function bindThemeToggles() {
  document.querySelectorAll("[data-theme-toggle]").forEach((btn) => {
    if (btn.__themeToggleBound) return;
    btn.__themeToggleBound = true;
    btn.addEventListener("click", () => {
      setTheme(getCurrentTheme() === "dark" ? "light" : "dark");
    });
  });
}

function init() {
  initThemeFromStorage();
  bindThemeToggles();
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", init);
} else {
  init();
}

