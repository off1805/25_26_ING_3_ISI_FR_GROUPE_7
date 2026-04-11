import { UeApi } from "../Ue/infrastructure/UeApi.js";
import { CreateUeUC } from "../Ue/application/CreateUeUC.js";
import { GlobalErrorHandler } from "../common/GlobalErrorHandler.js";
import { GlobalEventNotifier } from "../common/GlobalEventNotifier.js";
import { FiliereUseCase } from "../academicStructure/application/FiliereUseCase.js";
import { FiliereApi } from "../academicStructure/infrastructure/FiliereApi.js";
import { SpecialiteApi } from "../academicStructure/infrastructure/SpecialiteApi.js";
const ueApi = new UeApi();
const createUeUC = new CreateUeUC(ueApi);

let allClasses = [];
let allSpecialites = [];
let allNiveaux = [];
let currentLevelId = null;
let currentSpecialtyId = null;
let subjectsBySpecialty = {}; // Cache: { specialtyId: [subjects] }

export const APSubjectsController = {
    init: () => {
        const data = window.KEMO_DATA || {};
        allNiveaux = data.niveaux || [];
        allSpecialites = data.specialites || [];
        allClasses = data.classes || [];

        initLevelTabs();
        initSearchAndFilters();
        initFormHandlers();
        const container= document.querySelector("#specialty-tabs");
           container.addEventListener('click', (event) => {
            const target= event.target.closest(".specialty-tab");
            handleSpecialtyChange(parseInt(target.getAttribute('data-specialty-id')));
        });

       // Select first level if available
        if (allNiveaux.length > 0) {
            const firstLevelId = allNiveaux[0].id;
            handleLevelChange(firstLevelId);
        }

    }
};

function initLevelTabs() {
    console.log("initLevelTabs");
    const levelBtns = document.querySelectorAll('.level-filter-btn');
    levelBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const levelId = parseInt(btn.getAttribute('data-level-id'));
            handleLevelChange(levelId);
        });
    });
}

function handleLevelChange(levelId) {
    console.log("handleLevelChange");
    currentLevelId = levelId;
    
    // Update UI for level tabs
    document.querySelectorAll('.level-filter-btn').forEach(btn => {
        const id = parseInt(btn.getAttribute('data-level-id'));
        if (id === levelId) {
            btn.classList.add('active');
            btn.setAttribute('aria-selected', 'true');
        } else {
            btn.classList.remove('active');
            btn.setAttribute('aria-selected', 'false');
        }
    });

    renderSpecialtyTabs(levelId);
}

async function renderSpecialtyTabs(levelId) {
    console.log("renderSpecialiteTabs");
    const container = document.getElementById('specialty-tabs');

   // const filteredSpecs = allSpecialites.filter(s => s.niveauId === levelId);
    const spe= new SpecialiteApi();
    const response=await spe.getByNiveauId(levelId);
    console.log(response);
    const filteredSpecs= Array.from( response);
    
    //recupererles 
    console.log("level id: "+levelId);
    console.log(allSpecialites);
    console.log(filteredSpecs);

    if (filteredSpecs.length === 0) {
        container.innerHTML = '<span class="py-4 text-sm text-muted-foreground-2 italic">Aucune spécialité pour ce niveau.</span>';
        document.getElementById('tbody-subjects').innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-12 text-center text-muted-foreground-2">
                    <p>Aucune spécialité trouvée pour ce niveau.</p>
                </td>
            </tr>
        `;
        return;
    }
    if(filteredSpecs.length===1){
        container.parentElement.classList.add("hidden");
    }else{
        container.parentElement.classList.remove("hidden");
    }

    container.innerHTML = filteredSpecs.map((spec, index) => `
        <button type="button" 
            data-specialty-id="${spec.id}"
            class="hs-tab-active:bg-primary/20 specialty-tab  hs-tab-active:text-primary px-5 py-1.5 text-xs font-semibold rounded-full text-muted-foreground-2 hover:bg-layer/60 transition-all 
            ${index === 0 ? 'active border-primary' : 'border-transparent text-muted-foreground-2 hover:text-primary'}"
            role="tab"
            data-hs-tab= "panel-ue-specialite-${spec.code}"
            aria-controls= "panel-ue-specialite-${spec.code}"
            >
            ${spec.code}
        </button>
         
    `).join('');

    

  
    

    // Select first specialty by default
    handleSpecialtyChange(filteredSpecs[0].id);
}

async function handleSpecialtyChange(specialtyId) {
    currentSpecialtyId = specialtyId;
    console.log("specialite id"+currentLevelId);

    // Update UI for specialty tabs
    document.querySelectorAll('.specialty-tab').forEach(tab => {
        const id = parseInt(tab.getAttribute('data-specialty-id'));
        if (id === specialtyId) {
            tab.classList.add('active', 'border-primary', 'text-primary');
            tab.classList.remove('border-transparent', 'text-muted-foreground-2');
        } else {
            tab.classList.remove('active', 'border-primary', 'text-primary');
            tab.classList.add('border-transparent', 'text-muted-foreground-2');
        }
    });

    // Update modal hidden field
    const modalSpecId = document.getElementById('modal-specialite-id');
    if (modalSpecId) modalSpecId.value = specialtyId;

    // Show loading state
    const tbody = document.getElementById('tbody-subjects');
    tbody.innerHTML = `
        <tr>
            <td colspan="5" class="px-6 py-12 text-center">
                <div class="animate-spin inline-block size-6 border-[3px] border-current border-t-transparent text-primary rounded-full" role="status" aria-label="loading">
                    <span class="sr-only">Chargement...</span>
                </div>
            </td>
        </tr>
    `;

    try {
        if (!subjectsBySpecialty[specialtyId]) {
            const response = await ueApi.searchUes(`?specialiteId=${specialtyId}&deleted=false`);
            console.log(response);
            subjectsBySpecialty[specialtyId] = response?.content || response || [];
        }
        renderSubjectsTable();
    } catch (error) {
        console.error("Error loading subjects:", error);
        tbody.innerHTML = `<tr><td colspan="5" class="px-6 py-12 text-center text-red-500">Erreur lors du chargement des matières.</td></tr>`;
    }
}

function renderSubjectsTable() {
    console.log("first");
    const tbody = document.getElementById('tbody-subjects');
    const subjects = subjectsBySpecialty[currentSpecialtyId] || [];
   
    const searchTerm = document.getElementById('subject-search').value.toLowerCase();
    const semesterFilter = document.getElementById('semester-filter').value;

    const filtered = subjects.filter(s => {
        const matchesSearch = s.libelle.toLowerCase().includes(searchTerm) || s.code.toLowerCase().includes(searchTerm);
        const matchesSemester = semesterFilter === 'all' || s.semestre?.toString() === semesterFilter;
        return matchesSearch && matchesSemester;
    });

    if (filtered.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-12 text-center text-muted-foreground-2">
                    <p>${searchTerm || semesterFilter !== 'all' ? 'Aucune matière ne correspond aux filtres.' : 'Aucune matière pour cette spécialité.'}</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filtered.map(ue => `
        <tr class="hover:bg-muted/40 transition-colors group">
            <td class="px-6 py-4">
                <div class="flex items-center gap-3 min-w-0">
                    <div class="size-9 rounded-xl flex items-center justify-center shrink-0" 
                         style="background-color: ${ue.couleur || '#7c3aed'}1a; color: ${ue.couleur || '#7c3aed'};">
                        <i class="bi bi-journal-text text-lg"></i>
                    </div>
                    <div>
                        <p class="text-sm font-bold text-layer-foreground truncate">${ue.libelle}</p>
                        <p class="text-[10px] text-muted-foreground-2 uppercase tracking-tight">${ue.description || 'Pas de description'}</p>
                    </div>
                </div>
            </td>
            <td class="px-6 py-4">
                <span class="text-xs inline-flex items-center px-2 py-1 rounded-lg bg-muted text-layer-foreground font-mono font-bold border border-layer-line">
                    ${ue.code}
                </span>
            </td>
            <td class="px-6 py-4 text-center">
                <span class="text-sm font-bold text-layer-foreground">${ue.credit}</span>
            </td>
            <td class="px-6 py-4 text-center">
                <span class="text-sm font-medium text-muted-foreground-2">${ue.volumeHoraireTotal}h</span>
            </td>
            <td class="px-6 py-4 text-right">
                <div class="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button type="button" class="size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-muted hover:text-primary transition-colors">
                        <i class="bi bi-pencil-square text-sm"></i>
                    </button>
                    <button type="button" class="size-8 flex items-center justify-center rounded-lg text-muted-foreground-2 hover:bg-red-500/10 hover:text-red-500 transition-colors">
                        <i class="bi bi-trash text-sm"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function initSearchAndFilters() {
    document.getElementById('subject-search').addEventListener('input', renderSubjectsTable);
    document.getElementById('semester-filter').addEventListener('change', renderSubjectsTable);
}

function initFormHandlers() {
    const form = document.getElementById('addUeForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const submitBtn = document.getElementById('btn-create-ue');
            const fd = new FormData(form);
            
            const payload = {
                libelle: fd.get('libelle'),
                code: fd.get('code'),
                credit: parseInt(fd.get('credit')),
                volumeHoraireTotal: parseInt(fd.get('volumeHoraireTotal')),
                description: fd.get('description'),
                couleur: fd.get('couleur'),
                specialiteId: parseInt(fd.get('specialiteId')),
                semestre: parseInt(fd.get('semestre')),
                enseignantIds: [] // Can be extended if needed
            };

            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="animate-spin inline-block size-4 border-[2px] border-current border-t-transparent text-white rounded-full mr-2"></span> Création...';
            }

            try {
                await createUeUC.execute(payload);
                if (window.HSOverlay) window.HSOverlay.close("#hs-modal-add-ue");
                GlobalEventNotifier.eventWellDone("UE créée avec succès !");
                
                // Clear cache and reload
                delete subjectsBySpecialty[currentSpecialtyId];
                handleSpecialtyChange(currentSpecialtyId);
                form.reset();
            } catch (err) {
                GlobalErrorHandler.handle(err);
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = "Créer l'UE";
                }
            }
        });
    }
}

// Auto-init on load
document.addEventListener('DOMContentLoaded', () => {
    APSubjectsController.init();
});
