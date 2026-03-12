export function userRowTable(userEmail, userRole, userStatus, userId, userRoleClass, userStatusClass) {
    const row = document.createElement("tr");
    row.setAttribute("data-user-id", userId);
    row.className = "hover:bg-muted/20 transition-colors group";

    const content = `
                                       

                                            <td class="  px-6  py-2">


                                                <span class="text-sm  text-layer-foreground">
                                                    ${userEmail}
                                                </span>


                                            </td>
                                            <!-- Rôle -->
                                            <td class="  px-6  py-2">
                                                <span
                                                    class="inline-flex items-center  px-6 py-1 rounded-full font-medium text-xs ${userRoleClass}">
                                                    ${userRole}
                                                    </span>
                                            </td>
                                            <!--Statut-->
                                            <td class="  px-6  py-2">
                                                <span
                                                    class="inline-flex items-center  px-6 py-1 rounded-full font-semibold text-xs ${userStatusClass}">
                                                    ${userStatus}
                                                    </span>
                                            </td>
                                            <!-- User ID -->
                                            <td class="  px-6  py-2 hidden lg:table-cell">
                                                <span class="inline-flex items-center text-foreground text-xs  ">
                                                    ${userId}
                                                    </span>
                                            </td>
                                            <!-- Actions -->
                                            <td class="  px-6  py-2 text-right">
                                                <div class="hs-dropdown relative inline-flex">
                                                    <button type="button"
                                                        class="hs-dropdown-toggle size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-layer-foreground transition-colors focus:outline-none"
                                                        aria-haspopup="menu" aria-expanded="false">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                                                            viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                            stroke-width="1.5" stroke-linecap="round"
                                                            stroke-linejoin="round"
                                                            class="icon icon-tabler icons-tabler-outline icon-tabler-dots">
                                                            <path stroke="none" d="M0 0h24v24H0z" fill="none" />
                                                            <path d="M4 12a1 1 0 1 0 2 0a1 1 0 1 0 -2 0" />
                                                            <path d="M11 12a1 1 0 1 0 2 0a1 1 0 1 0 -2 0" />
                                                            <path d="M18 12a1 1 0 1 0 2 0a1 1 0 1 0 -2 0" />
                                                        </svg>
                                                    </button>
                                                    <div class="hs-dropdown-menu transition-[opacity,margin] duration-200 hs-dropdown-open:opacity-100 opacity-0 hidden min-w-36 bg-dropdown border border-card-line shadow-lg rounded-xl z-20 p-1"
                                                        role="menu">
                                                        <button type="button"
                                                            class="flex items-center  w-full text-sm   rounded-lg transition-colors flex items-center gap-x-3 py-2 px-3  text-dropdown-item-foreground hover:bg-dropdown-item-hover focus:outline-hidden focus:bg-dropdown-item-focus">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="14"
                                                                height="14" viewBox="0 0 24 24" fill="none"
                                                                stroke="currentColor" stroke-width="2"
                                                                stroke-linecap="round" stroke-linejoin="round">
                                                                <path
                                                                    d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                                                                <path
                                                                    d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                                                            </svg>
                                                            Modifier
                                                        </button>
                                                        <button type="button"
                                                            class="btn-delete-user flex items-center  w-full text-sm   rounded-lg transition-colors flex items-center gap-x-3 py-2 px-3  text-dropdown-item-foreground hover:bg-dropdown-item-hover focus:outline-hidden focus:bg-dropdown-item-focus"
                                                            data-user-id="${userId}">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="14"
                                                                height="14" viewBox="0 0 24 24" fill="none"
                                                                stroke="currentColor" stroke-width="2"
                                                                stroke-linecap="round" stroke-linejoin="round">
                                                                <polyline points="3 6 5 6 21 6" />
                                                                <path
                                                                    d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                                                            </svg>
                                                            Supprimer
                                                        </button>
                                                    </div>
                                                </div>
                                            </td>
                                       
    `;

    row.innerHTML = content;
    console.log("ui create well.");
    return row;
}