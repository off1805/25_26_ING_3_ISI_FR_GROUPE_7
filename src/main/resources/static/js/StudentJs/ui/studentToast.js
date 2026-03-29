/**
 * studentToast.js
 * Affiche un toast Toastify avec un type : 'success' | 'error' | 'warning'
 */

const CONFIG = {
    success: { icon: '✓', classes: 'bg-green-50 border border-green-200 text-green-700' },
    error:   { icon: '✕', classes: 'bg-red-50 border border-red-200 text-red-700' },
    warning: { icon: '⚠', classes: 'bg-yellow-50 border border-yellow-200 text-yellow-700' },
};

export function showToast(message, type = 'success') {
    const cfg = CONFIG[type] || CONFIG.success;

    const node = document.createElement('div');
    node.className = `flex items-start gap-3 p-3 pr-4`;
    node.innerHTML = `
        <span class="shrink-0 text-base leading-none mt-0.5">${cfg.icon}</span>
        <p class="text-xs font-medium leading-snug flex-1">${message}</p>
    `;

    Toastify({
        node,
        className: `fixed bottom-4 end-4 z-[9999] rounded-xl shadow-md w-80 opacity-0 transition-all duration-300 hs-toastify-on:opacity-100 ${cfg.classes}`,
        duration: 4500,
        gravity: 'bottom',
        position: 'right',
        close: false,
        escapeMarkup: false,
    }).showToast();
}
