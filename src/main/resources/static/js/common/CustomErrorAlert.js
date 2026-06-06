
export async function customErrorAlert(title, message) {
  return new Promise((resolve) => {
    const modal = document.createElement('div');
    modal.id = "hs-custom-error-alert";
    modal.role = "dialog";
    modal.tabIndex = "-1";
    modal.setAttribute("aria-labelledby", "hs-custom-error-alert-label");

    modal.className = "hs-overlay hidden size-full fixed top-0 start-0 z-80 overflow-x-hidden overflow-y-auto flex justify-center items-center";
    modal.innerHTML = `
      <div class="hs-overlay-open:mt-7 hs-overlay-open:opacity-100 hs-overlay-open:duration-500 mt-0 opacity-0 ease-out transition-all sm:max-w-lg sm:w-full m-3 sm:mx-auto">
        <div class="relative w-full max-h-full flex flex-col bg-overlay border border-red-200 dark:border-red-900/50 rounded-xl pointer-events-auto shadow-xl">

          <div class="absolute top-2 end-2">
            <button id="error-close-btn" type="button"
              class="size-8 inline-flex justify-center items-center gap-x-2 rounded-full border border-transparent bg-surface text-surface-foreground hover:bg-surface-hover focus:outline-hidden focus:bg-surface-focus disabled:opacity-50 disabled:pointer-events-none">
              <span class="sr-only">Fermer</span>
              <svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 6 6 18"/><path d="m6 6 12 12"/>
              </svg>
            </button>
          </div>

          <div class="p-4 sm:p-8 py-6 overflow-y-auto">

            <div class="flex items-start gap-4 mb-4">
              <div class="size-10 rounded-full bg-red-100 dark:bg-red-950 flex items-center justify-center shrink-0">
                <svg class="size-5 text-red-600 dark:text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                  fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="12" y1="8" x2="12" y2="12"/>
                  <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
              </div>
              <div class="min-w-0 flex-1 pt-1.5">
                <h3 id="hs-custom-error-alert-label" class="text-xl font-semibold text-red-600 dark:text-red-400">
                  ${title}
                </h3>
              </div>
            </div>

            <p class="text-muted-foreground-1 pl-14">
              ${message}
            </p>

            <div class="mt-6 flex justify-end">
              <button id="error-ok-btn" type="button"
                class="py-2 px-4 inline-flex items-center gap-x-2 text-sm font-medium rounded-lg bg-red-600 border border-red-700 text-white hover:bg-red-700 focus:outline-hidden focus:bg-red-700 disabled:opacity-50 disabled:pointer-events-none">
                Fermer
              </button>
            </div>

          </div>
        </div>
      </div>
    `;

    document.body.appendChild(modal);
    window.HSStaticMethods.autoInit();
    window.HSOverlay.open(modal);

    function cleanup() {
      window.HSOverlay.close(modal);
      modal.remove();
      resolve();
    }

    modal.querySelector('#error-ok-btn').onclick = cleanup;
    modal.querySelector('#error-close-btn').onclick = cleanup;
  });
}
