

export async function customAlert(title, message, primaryChoice, secondaryChoice) {
  return new Promise((resolve) => {
    const modal = document.createElement('div');
    modal.id = "hs-custom-alert";
    modal.role = "dialog";
    modal.tabIndex = "-1";
    modal.setAttribute("aria-labelledby", "hs-custom-alert-label");


    modal.className = "hs-overlay hidden size-full fixed top-0 start-0 z-80 overflow-x-hidden overflow-y-auto flex justify-center items-center";
    modal.innerHTML = `
      
      <div class="hs-overlay-open:mt-7 hs-overlay-open:opacity-100 hs-overlay-open:duration-500 mt-0 opacity-0 ease-out transition-all sm:max-w-lg sm:w-full m-3 sm:mx-auto">
        <div class="relative w-full max-h-full flex flex-col bg-overlay border border-overlay-line rounded-xl pointer-events-auto shadow-xl">
          <div class="absolute top-2 end-2">
            <button id="close-btn" type="button" class="size-8 inline-flex justify-center items-center gap-x-2 rounded-full border border-transparent bg-surface text-surface-foreground hover:bg-surface-hover focus:outline-hidden focus:bg-surface-focus disabled:opacity-50 disabled:pointer-events-none" >
              <span class="sr-only">Close</span>
              <svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
            </button>
          </div>

          <div class="p-4 sm:p-8 py-4  overflow-y-auto">

          <h3 id="hs-custom-alert-label" class="mb-2 text-xl font-semibold text-foreground">
            ${title}
          </h3>
          <p class="text-muted-foreground-1">
          ${message}
          </p>

        <div class="mt-6 flex justify-end gap-x-4">
          <button id="cancel-btn"  type="button" class="py-2 px-3 inline-flex items-center gap-x-2 text-sm font-medium rounded-lg bg-layer border border-layer-line text-layer-foreground shadow-2xs hover:bg-layer-hover disabled:opacity-50 disabled:pointer-events-none focus:outline-hidden focus:bg-layer-focus">
            ${secondaryChoice}
          </button>
          <button id="confirm-btn" type="button" class="py-2 px-3 inline-flex items-center gap-x-2 text-sm font-medium rounded-lg bg-primary border border-primary-line text-primary-foreground hover:bg-primary-hover focus:outline-hidden focus:bg-primary-focus disabled:opacity-50 disabled:pointer-events-none">
            ${primaryChoice}
          </button>
        </div>
      </div>
    </div>
  </div>
  `;

    document.body.appendChild(modal);
    window.HSStaticMethods.autoInit();

    window.HSOverlay.open(modal);


    modal.querySelector('#confirm-btn').onclick = () => {
      cleanup();
      resolve(true);
    };

    modal.querySelector('#cancel-btn').onclick = () => {
      cleanup();
      resolve(false);
    };

    modal.querySelector('#close-btn').onclick = () => {
      cleanup();
      resolve(false);
    };

    function cleanup() {
      window.HSOverlay.close(modal);
      modal.remove();
    }

  })

}