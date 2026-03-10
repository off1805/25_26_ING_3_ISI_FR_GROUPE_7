



export function showInfoModal(title, description) {
  console.log("in the mouth");
  // Créer un élément div parent
  const toastNode = document.createElement("div");
  toastNode.className = "flex gap-3 p-4";


  // Injecter le HTML dans ce node
  toastNode.innerHTML = `

    <svg class="shrink-0 size-4 text-teal-500 mt-0.5" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
      <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
    </svg>
    <div class="grow">
      <p id="hs-toast-stack-toggle-update-label" class="text-sm text-layer-foreground">
        ${description}.
      </p>
    </div>
     

    <div class="ms-auto">
      <button type="button" class="inline-flex shrink-0 justify-center items-center size-5 rounded-lg text-layer-foreground opacity-50 hover:opacity-100 focus:outline-hidden focus:opacity-100" aria-label="Close" data-hs-remove-element="#dismiss-toast">
        <span class="sr-only">Close</span>
        <svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
      </button>
    </div>


`;

  // Ajouter le listener pour le bouton Close
  const closeBtn = toastNode.querySelector("button");


  const toast = Toastify({

    node: toastNode,
    className: "hs-toastify-on:opacity-100 fixed bottom-2 end-2 opacity-0 z-90 transition-all duration-300 w-80 h-fit bg-layer text-sm text-layer-foreground border border-layer-line rounded-xl shadow-lg [&>.toast-close]:hidden",
    duration: 3000,
    gravity: "bottom",
    position: "right",
    close: false,
    escapeMarkup: false
  });

  toast.showToast();
  if (closeBtn) {
    closeBtn.addEventListener("click", () => {
      if (toast.toastElement) {
        toast.toastElement.remove();
      }
    });
  }
  console.log("end end")




}