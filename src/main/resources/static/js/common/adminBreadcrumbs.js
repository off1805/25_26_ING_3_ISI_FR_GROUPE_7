// Admin breadcrumbs:
// - Navbar: "Admin > <active sidebar item>"
// - Page: "Admin > <full trail>" from data-admin-page-bc, fallback to sidebar
//
// data-admin-page-bc format (optional):
//   "Label:/href|Label without href|Another:/href"
// Notes:
// - Use '|' as separator, ':' to split label/href (first ':' only).
// - Keep labels free of '|' to avoid parsing issues.

function normalizePath(pathname) {
  if (!pathname) return "/";
  return pathname.length > 1 ? pathname.replace(/\/+$/, "") : "/";
}

function parseBreadcrumbString(raw) {
  if (!raw) return [];
  return raw
    .split("|")
    .map((seg) => seg.trim())
    .filter(Boolean)
    .map((seg) => {
      const idx = seg.indexOf(":");
      if (idx === -1) return { label: seg };
      const label = seg.slice(0, idx).trim();
      const href = seg.slice(idx + 1).trim();
      return href ? { label, href } : { label };
    })
    .filter((it) => it.label);
}

function getSidebarActiveItem() {
  const sidebar = document.querySelector("[data-admin-sidebar]");
  if (!sidebar) return null;

  let activeLink = sidebar.querySelector('a[aria-current="page"]');
  if (!activeLink) {
    // Fallback: match current path by href.
    const currentPath = normalizePath(window.location.pathname);
    const links = Array.from(sidebar.querySelectorAll("a[href]"));
    activeLink =
      links.find((a) => normalizePath(new URL(a.getAttribute("href"), window.location.origin).pathname) === currentPath) ||
      null;
  }
  if (!activeLink) return null;

  const label =
    activeLink.getAttribute("data-admin-bc-label") ||
    (activeLink.querySelector("span:last-child") ? activeLink.querySelector("span:last-child").textContent : "") ||
    activeLink.textContent ||
    "";

  const href = activeLink.getAttribute("href") || "";
  return { label: label.trim(), href: href.trim() || undefined };
}

function escapeHtml(str) {
  return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function iconHome() {
  return `
    <svg class="shrink-0" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
      <path stroke="none" d="M0 0h24v24H0z" fill="none" />
      <path d="M5 12l-2 0l9 -9l9 9l-2 0" />
      <path d="M5 12v7a2 2 0 0 0 2 2h10a2 2 0 0 0 2 -2v-7" />
      <path d="M9 21v-6a2 2 0 0 1 2 -2h2a2 2 0 0 1 2 2v6" />
    </svg>
  `;
}

function iconChevron() {
  return `
    <svg class="shrink-0" xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M9 6l6 6l-6 6" />
    </svg>
  `;
}

function renderNavbarBreadcrumb(container, items) {
  if (!container) return;

  const all = [{ label: "Admin" }, ...items.filter((it) => it && it.label)];
  if (all.length === 1) {
    container.innerHTML = `${iconHome()}<span class="font-medium text-layer-foreground">Admin</span>`;
    return;
  }

  const parts = [];
  parts.push(iconHome());

  all.forEach((item, index) => {
    const isLast = index === all.length - 1;
    if (index > 0) parts.push(iconChevron());

    const label = escapeHtml(item.label);
    if (!isLast && item.href) {
      const href = escapeHtml(item.href);
      parts.push(
        `<a href="${href}" class="break-words hover:text-primary transition-colors text-muted-foreground">${label}</a>`
      );
    } else {
      parts.push(
        `<span class="break-words ${isLast ? "font-medium text-layer-foreground" : "text-muted-foreground"}">${label}</span>`
      );
    }
  });

  container.innerHTML = parts.join("");
}

function renderPageBreadcrumb(container, items) {
  if (!container) return;
  const all = items.filter((it) => it && it.label);
  if (!all.length) {
    container.innerHTML = "";
    return;
  }

  const parts = [];
  all.forEach((item, index) => {
    const isLast = index === all.length - 1;
    if (index > 0) parts.push(iconChevron());

    const label = escapeHtml(item.label);
    if (!isLast && item.href) {
      const href = escapeHtml(item.href);
      parts.push(
        `<a href="${href}" class="break-words hover:text-primary transition-colors text-muted-foreground">${label}</a>`
      );
    } else {
      parts.push(
        `<span class="break-words ${isLast ? "font-medium text-layer-foreground" : "text-muted-foreground"}">${label}</span>`
      );
    }
  });

  container.innerHTML = parts.join("");
}

function fillBreadcrumbs() {
  const topLevel = getSidebarActiveItem();

  const navbarContainer = document.querySelector('[data-admin-breadcrumb-target="navbar"]');
  const pageContainer = document.querySelector('[data-admin-breadcrumb-target="page"]');

  const rawPage = document.body.getAttribute("data-admin-page-bc");
  const parsedPage = parseBreadcrumbString(rawPage);

  const navbarItems = topLevel ? [{ label: topLevel.label, href: topLevel.href }] : [];
  renderNavbarBreadcrumb(navbarContainer, navbarItems);

  // Page breadcrumb should start where navbar ends:
  // - it begins at the top-level label (the navbar last item),
  // - then continues with deeper items (if provided).
  let pageItems = parsedPage.length ? parsedPage : navbarItems;
  if (topLevel) {
    if (!pageItems.length) {
      pageItems = [{ label: topLevel.label, href: topLevel.href }];
    } else {
      const first = pageItems[0];
      if (first && first.label === topLevel.label) {
        // Ensure the first item is linkable like the sidebar.
        if (!first.href && topLevel.href) {
          pageItems = [{ label: topLevel.label, href: topLevel.href }, ...pageItems.slice(1)];
        }
      } else {
        // If the template didn't include the top-level segment, prepend it.
        pageItems = [{ label: topLevel.label, href: topLevel.href }, ...pageItems];
      }
    }
  }
  renderPageBreadcrumb(pageContainer, pageItems);
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", fillBreadcrumbs);
} else {
  fillBreadcrumbs();
}
