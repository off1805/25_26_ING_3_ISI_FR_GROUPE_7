    // Ces valeurs sont injectées par TeacherSeance.html via window.*
    const HEURE_DEBUT_STR = window.HEURE_DEBUT_STR || '08:00';
    const HEURE_FIN_STR   = window.HEURE_FIN_STR   || '12:00';
    const SEANCE_ID       = window.SEANCE_ID        || null;
    const TOTAL_STUDENTS  = window.TOTAL_STUDENTS   || 0;

    // studentId → [status_h0, status_h1, status_h2, status_h3]
    // 'empty' | 'present' | 'late' | 'absent'
    const blockStatuses = {};

    // ── Init blocks ───────────────────────────────────────────────────
    function initBlocks() {
        document.querySelectorAll('.attendance-container').forEach(container => {
            const sid = container.dataset.studentId;
            blockStatuses[sid] = ['empty','empty','empty','empty'];
        });
    }

    // ── Heures ────────────────────────────────────────────────────────
    function getHours() {
        const [dh] = HEURE_DEBUT_STR.split(':').map(Number);
        const [fh] = HEURE_FIN_STR.split(':').map(Number);
        const count = Math.max(1, fh - dh);
        return Array.from({length: count}, (_, i) => dh + i);
    }

    function buildHoursLegend() {
        const hours = getHours();
        const legend = document.getElementById('hours-legend');
        const header = document.getElementById('col-hours-header');
        if (!legend || !header) return;

        // Légende
        legend.innerHTML = hours.map((h, i) => `
            <div class="flex items-center gap-1.5 shrink-0">
                <div class="size-5 rounded-md border-2 border-card-line bg-muted flex items-center justify-center text-[9px] font-bold text-muted-foreground-2">${i+1}</div>
                <span class="text-xs text-muted-foreground-2 font-medium">${h}h-${h+1}h</span>
            </div>
        `).join('');

        // Header colonnes
        header.innerHTML = hours.map((h, i) => `
            <div class="flex-1 text-center">
                <span class="text-[10px] font-bold text-muted-foreground-2">${h}h</span>
            </div>
        `).join('');
    }

    // ── Horloge ───────────────────────────────────────────────────────
    function updateClock() {
        const now = new Date();
        const el = document.getElementById('live-clock');
        if (el) el.textContent = String(now.getHours()).padStart(2,'0') + ':' + String(now.getMinutes()).padStart(2,'0');
    }
    updateClock(); setInterval(updateClock, 10000);

    // ── Barre progression ─────────────────────────────────────────────
    const segmentsContainer = document.getElementById('time-progress-segments');
if (segmentsContainer && segmentsContainer.children.length === 0) {
    for (let i = 0; i < 40; i++) {
        const seg = document.createElement('div');
        seg.className = 'w-full h-2.5 rounded-[5px] flex flex-col justify-center overflow-hidden bg-muted text-xs text-foreground-inverse text-center whitespace-nowrap transition duration-500';
        seg.setAttribute('role', 'progressbar');
        seg.setAttribute('aria-valuemin', '0');
        seg.setAttribute('aria-valuemax', '100');
        seg.dataset.index = i;
        segmentsContainer.appendChild(seg);
    }
}
function updateTimeBar() {
    const now = new Date();
    const [dh, dm] = HEURE_DEBUT_STR.split(':').map(Number);
    const [fh, fm] = HEURE_FIN_STR.split(':').map(Number);
    const start = dh * 60 + dm, end = fh * 60 + fm;
    const cur = now.getHours() * 60 + now.getMinutes();
    const pct = Math.min(100, Math.max(0, (cur - start) / (end - start) * 100));

    // Met à jour les segments
    const segments = document.querySelectorAll('#time-progress-segments > div');
    segments.forEach((seg, i) => {
        const threshold = (i + 1) * 2.5; // chaque segment = 10%
        if (pct >= threshold) {
            seg.classList.remove('bg-muted');
            seg.classList.add('bg-primary-500');
        } else {
            seg.classList.remove('bg-primary-500');
            seg.classList.add('bg-muted');
        }
        seg.setAttribute('aria-valuenow', Math.min(100, Math.max(0, pct - i * 40)));
    });

    // Affiche le check uniquement si terminé
    const check = document.getElementById('time-progress-check');
    if (check) check.style.display = pct >= 100 ? 'block' : 'none';

    // Temps écoulé
    const elapsed = Math.max(0, cur - start);
    const el = document.getElementById('time-elapsed');
    if (el) el.textContent = Math.floor(elapsed / 60) + 'h' + String(elapsed % 60).padStart(2, '0') + ' écoulées';
}

updateTimeBar();
setInterval(updateTimeBar, 60000);
    // ── Blocs horaires ────────────────────────────────────────────────
    const STATUS_CYCLE = ['empty','present','late','absent'];
    const STATUS_COLOR = { present:'#16a34a', late:'#d97706', absent:'#dc2626', empty:'transparent' };

    function setBlock(block, status) {
        if (!block) return;
        const sid   = block.dataset.studentId;
        const hour  = parseInt(block.dataset.hour);
        block.dataset.status = status;
        
        // Update internal input state
        const input = block.querySelector('input');
        if (input) {
            input.checked = (status !== 'empty');
        }

        if (!blockStatuses[sid]) blockStatuses[sid] = ['empty','empty','empty','empty'];
        blockStatuses[sid][hour] = status;
        updateCounts();
        updateRowIndicator(sid);
    }

    function updateRowIndicator(sid) {
        const row = document.querySelector(`.student-row[data-id="${sid}"]`);
        if (row) {
            const blocks = blockStatuses[sid] || [];
            const counts = { present:0, late:0, absent:0 };
            blocks.forEach(b => { if (b !== 'empty') counts[b]++; });
            const dominant = Object.entries(counts).sort((a,b)=>b[1]-a[1])[0];
            const ind = row.querySelector('.status-indicator');
            if (ind) ind.style.background = dominant[1] > 0 ? STATUS_COLOR[dominant[0]] : 'transparent';
        }
        _renderLeftBlocks(sid);
    }

    // Construit le HTML des badges horaires pour un étudiant (partagé sidebar + drawer).
    function _buildBlocksHtml(sid) {
        if (_manuelModeActive) return _renderManuelBadge(_manuelPresence[sid]);
        const hours    = getHours();
        const statuses = blockStatuses[sid] || Array(hours.length).fill('empty');
        return hours.map((h, i) => {
            const st = statuses[i] || 'empty';
            let cls, label;
            if      (st === 'present') { cls = 'text-green-700 dark:text-green-400'; label = 'P'; }
            else if (st === 'late')    { cls = 'text-amber-700 dark:text-amber-400'; label = 'R'; }
            else if (st === 'absent')  { cls = 'text-red-700 dark:text-red-400';     label = 'A'; }
            else                       { cls = 'text-muted-foreground-2';             label = ' '; }
            return `<span title="${h}h–${h+1}h" class="inline-flex items-center justify-center w-7 h-7 text-[11px] font-semibold transition-colors duration-200 ${cls}">${label}</span>`;
        }).join('');
    }

    function _renderLeftBlocks(sid) {
        const el = document.getElementById(`left-blocks-${sid}`);
        if (el) el.innerHTML = _buildBlocksHtml(sid);
    }

    // Blocs vides à injecter lors du premier rendu (avant données réelles).
    function _initialPresenceBlocks() {
        return getHours().map(h =>
            `<span title="${h}h–${h+1}h" class="inline-flex items-center justify-center w-7 h-7   text-sm font-semibold text-muted-foreground-2"> </span>`
        ).join('');
    }

    let isMouseDown = false;
    let dragStatus = null;
    let targetStudentId = null;

function initBlockInteraction() {
    const labels = document.querySelectorAll('.attendance-label');

    let isMouseDown = false;
    let dragStatus = null;
    let lastClickTime = 0;
    let lastClickLabel = null;
    let pendingSingleClick = null;

    const DOUBLE_CLICK_DELAY = 250;

    labels.forEach(label => {
        label.addEventListener('click', e => e.preventDefault());

        label.addEventListener('mousedown', e => {
            isMouseDown = true;

            const now = Date.now();
            const isDoubleClick = (now - lastClickTime) < DOUBLE_CLICK_DELAY && lastClickLabel === label;

            if (isDoubleClick) {
                // Annule le single click en attente
                clearTimeout(pendingSingleClick);
                pendingSingleClick = null;
                lastClickTime = 0;
                lastClickLabel = null;

                // Applique absent
                const next = label.dataset.status === 'absent' ? 'empty' : 'absent';
                setBlock(label, next);
                dragStatus = next;
            } else {
                lastClickTime = now;
                lastClickLabel = label;
            }
        });

        label.addEventListener('mouseup', e => {
            if (!isMouseDown) return;

            const isSliding = dragStatus !== null;
            const isDoubleClickPending = lastClickLabel === null; // déjà traité

            if (!isSliding && !isDoubleClickPending) {
                pendingSingleClick = setTimeout(() => {
                    // Vérifie qu'aucun double clic n'est venu entre temps
                    if (lastClickLabel === label) {
                        const next = label.dataset.status === 'present' ? 'empty' : 'present';
                        setBlock(label, next);
                    }
                    pendingSingleClick = null;
                }, DOUBLE_CLICK_DELAY);
            }
        });

        label.addEventListener('mousemove', e => {
            if (!isMouseDown || dragStatus !== null || lastClickLabel === null) return;
            // Démarre le slide, annule le single click en attente
            clearTimeout(pendingSingleClick);
            pendingSingleClick = null;
            dragStatus = label.dataset.status === 'present' ? 'empty' : 'present';
            setBlock(label, dragStatus);
        });

        label.addEventListener('mouseenter', e => {
            if (isMouseDown && dragStatus !== null) {
                setBlock(label, dragStatus);
            }
        });
    });

    document.addEventListener('mouseup', () => {
        isMouseDown = false;
        dragStatus = null;
    });

    // Select All
    const selectAll = document.getElementById('select-all');
    if (selectAll) {
        const selectAllInput = selectAll.querySelector('input');
        selectAllInput.addEventListener('change', () => {
            document.querySelectorAll('.row-checkbox').forEach(cb => {
                cb.checked = selectAllInput.checked;
            });
        });
    }

    // Touch swipe
    document.querySelectorAll('.student-row').forEach(row => {
        let tx = 0, moved = false;
        row.addEventListener('touchstart', e => { tx = e.touches[0].clientX; moved = false; });
        row.addEventListener('touchmove', e => {
            if (Math.abs(e.touches[0].clientX - tx) > 20) moved = true;
        });
        row.addEventListener('touchend', e => {
            if (!moved) return;
            const dx = e.changedTouches[0].clientX - tx;
            const status = dx > 40 ? 'present' : dx < -40 ? 'absent' : null;
            if (!status) return;
            row.querySelector('.attendance-container')
               ?.querySelectorAll('.attendance-label')
               .forEach(b => setBlock(b, status));
        });
    });
}

    // ── Counts ───────────────────────────────────────────────────────
    function updateCounts() {
        let p=0, l=0, a=0, done=0;
        Object.values(blockStatuses).forEach(blocks => {
            if (!blocks.every(b => b === 'empty')) {
                done++;
                // Statut dominant pour cet étudiant
                const counts = { present:0, late:0, absent:0 };
                blocks.forEach(b => { if (b !== 'empty') counts[b]++; });
                const dom = Object.entries(counts).sort((a,b)=>b[1]-a[1])[0];
                if (dom[1] > 0) {
                    if (dom[0]==='present') p++;
                    else if (dom[0]==='late') l++;
                    else a++;
                }
            }
        });
        const elP = document.getElementById('count-present');
        if (elP) elP.textContent = p;
        const elL = document.getElementById('count-late');
        if (elL) elL.textContent = l;
        const elA = document.getElementById('count-absent');
        if (elA) elA.textContent = a;

        const cp2 = document.getElementById('count-present-2');
        if (cp2) cp2.textContent = p;
        const ca2 = document.getElementById('count-absent-2');
        if (ca2) ca2.textContent = a;

        const total = TOTAL_STUDENTS || Object.keys(blockStatuses).length;
        const elPT = document.getElementById('progress-text');
        if (elPT) elPT.textContent = done + ' / ' + total + ' étudiants traités';
        const pct = total > 0 ? done / total * 100 : 0;
        const elPB = document.getElementById('attendance-progress');
        if (elPB) elPB.style.width = pct + '%';
    }

    function markAllBlocks(status) {
        document.querySelectorAll('.attendance-container').forEach(container => {
            container.querySelectorAll('.attendance-label').forEach(b => setBlock(b, status));
        });
    }

    function resetAllBlocks() {
        document.querySelectorAll('.attendance-container').forEach(container => {
            container.querySelectorAll('.attendance-label').forEach(b => setBlock(b, 'empty'));
        });
    }

    // ── Method switch ─────────────────────────────────────────────────
    function switchMethod(method) {
        const btns = ['manuel', 'qr', 'pin'];
        btns.forEach(b => {
            const el = document.getElementById(`tab-btn-${b}`);
            if (el) {
                if (b === method) {
                    el.classList.add('bg-primary', 'text-white');
                    el.classList.remove('text-muted-foreground-2', 'hover:bg-card');
                } else {
                    el.classList.remove('bg-primary', 'text-white');
                    el.classList.add('text-muted-foreground-2', 'hover:bg-card');
                }
            }
        });

        const display = document.getElementById('active-method-display');
        if (!display) return;

        if (method === 'manuel') {
            display.innerHTML = `
                <div class="p-8 rounded-3xl bg-card border-2 border-dashed border-card-line flex flex-col items-center justify-center text-center gap-3">
                    <div class="size-12 rounded-2xl bg-muted flex items-center justify-center">
                        <i class="bi bi-hand-index-thumb text-muted-foreground-2 text-xl"></i>
                    </div>
                    <p class="text-xs text-muted-foreground-2 px-4 italic">Mode manuel actif. Cliquez sur les blocs horaires des étudiants pour marquer leur présence.</p>
                </div>
            `;
        } else {
            generateAttendanceCode(method);
        }
    }

    // ── Side Panel ────────────────────────────────────────────────────
    let panelTimerQR = null, panelTimerPIN = null;

    function openSidePanel(type) {
        switchMethod(type);
        // Optionally still open side panel if requested, but for now let's focus on right zone
        // const panel   = document.getElementById('side-panel');
        // const overlay = document.getElementById('side-panel-overlay');
        // ...
    }

    async function generateAttendanceCode(type) {
        const seanceId    = window.SEANCE_ID;
        const enseignantId = window.ENSEIGNANT_ID;
        if (!seanceId || !enseignantId) return;

        const display = document.getElementById('active-method-display');
        if (display) {
            display.innerHTML = `
                <div class="flex flex-col items-center justify-center p-6 bg-card border border-card-line rounded-3xl gap-4">
                    <div class="size-10 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
                    <p class="text-xs text-muted-foreground-2">Génération du code ${type.toUpperCase()}...</p>
                </div>
            `;
        }

        try {
            const { default: api } = await import('../common/ClientHttp.js');
            const code = await api.post('/api/attendance-codes', {
                seanceId: seanceId,
                enseignantId: enseignantId,
                type: type.toUpperCase(),
                heuresAMarquer: 2.0,
                dureeVieMinutes: type === 'qr' ? 5 : 10
            });

            if (display) {
                if (type === 'qr') {
                    display.innerHTML = `
                        <div class="flex flex-col items-center gap-4 p-5 bg-card border border-card-line rounded-3xl">
                            <div class="size-44 bg-white rounded-2xl border-4 border-primary/20 p-2 flex items-center justify-center shadow-inner" id="qr-code-zone"></div>
                            <div class="text-center">
                                <p class="text-[10px] text-muted-foreground-2 uppercase tracking-widest font-bold">Expire dans</p>
                                <p class="text-2xl font-black text-primary font-mono" id="qr-timer-zone">04:59</p>
                            </div>
                            <button onclick="regenerateCode('qr')" class="w-full py-2.5 text-xs font-bold rounded-xl bg-primary/10 text-primary hover:bg-primary/20 transition-all">Actualiser</button>
                        </div>
                    `;
                    const container = document.getElementById('qr-code-zone');
                    if (typeof QRCode !== 'undefined') {
                        new QRCode(container, { text: code.valeur, width: 160, height: 160, colorDark: '#7c3aed', colorLight: '#ffffff' });
                    } else {
                        container.innerHTML = `<p class="text-[10px] font-mono text-primary break-all text-center">${code.valeur}</p>`;
                    }
                    startCodeTimer('qr', code.dureeVieMinutes * 60, 'qr-timer-zone');
                } else {
                    display.innerHTML = `
                        <div class="flex flex-col items-center gap-4 p-5 bg-card border border-card-line rounded-3xl">
                            <div class="flex gap-2">
                                ${code.valeur.split('').map(digit => `<div class="size-12 bg-muted border border-card-line rounded-xl flex items-center justify-center text-2xl font-black text-primary font-mono">${digit}</div>`).join('')}
                            </div>
                            <div class="text-center">
                                <p class="text-[10px] text-muted-foreground-2 uppercase tracking-widest font-bold">Expire dans</p>
                                <p class="text-2xl font-black text-blue-500 font-mono" id="pin-timer-zone">09:59</p>
                            </div>
                            <div class="flex gap-2 w-full">
                                <button onclick="regenerateCode('pin')" class="flex-1 py-2.5 text-xs font-bold rounded-xl bg-muted text-layer-foreground hover:bg-muted/80">Régénérer</button>
                                <button onclick="copyVAL('${code.valeur}')" class="flex-1 py-2.5 text-xs font-bold rounded-xl bg-blue-500 text-white hover:bg-blue-600">Copier</button>
                            </div>
                        </div>
                    `;
                    startCodeTimer('pin', code.dureeVieMinutes * 60, 'pin-timer-zone');
                }
            }
        } catch (e) {
            console.error('Erreur génération code:', e);
            if (display) display.innerHTML = `<p class="text-xs text-red-500 p-4 text-center">Erreur lors de la génération du code.</p>`;
        }
    }

    function startCodeTimer(type, s, elementId) {
        const el = document.getElementById(elementId);
        if (!el) return;
        let rem = s;
        const t = setInterval(() => {
            rem--;
            if (rem <= 0) { clearInterval(t); el.textContent = '00:00'; return; }
            el.textContent = String(Math.floor(rem/60)).padStart(2,'0') + ':' + String(rem%60).padStart(2,'0');
        }, 1000);
    }
    
    window.copyVAL = (val) => {
        navigator.clipboard?.writeText(val).then(async () => {
            const { GlobalEventNotifier } = await import('../common/GlobalEventNotifier.js');
            GlobalEventNotifier.eventWellDone('Copié : ' + val);
        });
    };

    function closeSidePanel() {
        document.getElementById('side-panel').classList.remove('open');
        document.getElementById('side-panel-overlay').classList.remove('open');
    }

    function startPanelTimer(type, s) {
        const id = type === 'qr' ? 'qr-timer' : 'pin-timer';
        const el = document.getElementById(id); if (!el) return;
        if (type === 'qr' && panelTimerQR)  clearInterval(panelTimerQR);
        if (type === 'pin' && panelTimerPIN) clearInterval(panelTimerPIN);
        let rem = s;
        const t = setInterval(() => {
            rem--;
            if (rem <= 0) { clearInterval(t); el.textContent = '00:00'; return; }
            el.textContent = String(Math.floor(rem/60)).padStart(2,'0') + ':' + String(rem%60).padStart(2,'0');
        }, 1000);
        if (type === 'qr') panelTimerQR = t; else panelTimerPIN = t;
    }

    function regenerateCode(type) { generateAttendanceCode(type); }

    function copyPIN() {
        const digits = Array.from(document.querySelectorAll('.pin-digit'))
            .map(el => el.textContent)
            .join('');
        navigator.clipboard?.writeText(digits).then(() => {
            const btn = event.currentTarget || event.target;
            const orig = btn.textContent; btn.textContent = '✓ Copié !';
            setTimeout(() => btn.textContent = orig, 1500);
        });
    }

    // Simuler un scan (démo)
    function simulateScan(name, type) {
        const listId = type === 'qr' ? 'live-scan-list' : 'live-pin-list';
        const list = document.getElementById(listId);
        if (!list) return;
        list.querySelector('p')?.remove();
        const item = document.createElement('div');
        item.className = 'flex items-center gap-3 px-3 py-2 bg-green-500/8 border border-green-200 dark:border-green-500/20 rounded-xl scan-pulse';
        item.innerHTML = `
            <div class="size-7 rounded-lg bg-green-500/15 flex items-center justify-center shrink-0">
                <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="text-green-600"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
            <span class="text-xs font-semibold text-green-700 dark:text-green-400 flex-1">${name}</span>
            <span class="text-xs text-muted-foreground-2">${new Date().toLocaleTimeString('fr-FR',{hour:'2-digit',minute:'2-digit'})}</span>
        `;
        list.prepend(item);
    }

    // ── Fin séance ────────────────────────────────────────────────────
    function endSession() {
        let p=0, l=0, a=0, notDone=0;
        Object.values(blockStatuses).forEach(blocks => {
            if (blocks.every(b => b === 'empty')) { notDone++; return; }
            const counts = { present:0, late:0, absent:0 };
            blocks.forEach(b => { if (b!=='empty') counts[b]++; });
            const dom = Object.entries(counts).sort((a,b)=>b[1]-a[1])[0];
            if (dom[0]==='present') p++;
            else if (dom[0]==='late') l++;
            else a++;
        });
        const total = p+l+a+notDone;

        document.getElementById('final-present').textContent = p;
        document.getElementById('final-late').textContent    = l;
        document.getElementById('final-absent').textContent  = a;

        const rate = total > 0 ? Math.round(p/total*100) : 0;
        document.getElementById('final-rate').textContent = rate + '%';
        document.getElementById('final-rate-bar').style.width = rate + '%';

        const warn = document.getElementById('final-warning');
        const warnMsg = document.getElementById('final-warning-msg');
        if (warn) {
            warn.style.display = notDone > 0 ? 'flex' : 'none';
            if (warnMsg && notDone > 0) warnMsg.textContent = notDone + ' étudiant(s) sans statut seront marqués absents.';
        }

        HSOverlay.open('#modal-end-session');
    }

    // Fin de séance : clôture tous les appels encore en attente (isPresent=null) pour
    // que plus aucun créneau ne reste indéterminé, même ceux liés à des appels jamais
    // explicitement clôturés.
    async function confirmEnd() {
        if (_presenceListId) {
            try {
                const { default: api } = await import('../common/ClientHttp.js');
                await api.post(`/api/appels/presence-list/${_presenceListId}/close-all`);
            } catch (e) { console.error('Erreur clôture des appels en attente:', e); }
        }
        HSOverlay.close('#modal-end-session');
    }

    async function exportFiche() {
        const { GlobalEventNotifier } = await import('../common/GlobalEventNotifier.js');
        GlobalEventNotifier.eventWellDone('Fiche d\'appel exportée !');
    }

    // ── Search (ancien) ───────────────────────────────────────────────
    function initSearch() {
        const input = document.getElementById('student-search');
        if (!input) return;
        input.addEventListener('input', (e) => {
            const q = e.target.value.toLowerCase().trim();
            document.querySelectorAll('.student-row').forEach(row => {
                const name = row.querySelector('p')?.textContent.toLowerCase() || '';
                const mat  = row.querySelector('.text-muted-foreground-2')?.textContent.toLowerCase() || '';
                row.classList.toggle('hidden', !name.includes(q) && !mat.includes(q));
            });
        });
    }

    // ── Init ──────────────────────────────────────────────────────────
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof HSStaticMethods !== 'undefined') HSStaticMethods.autoInit();
        initBlocks();
        buildHoursLegend();
        initBlockInteraction();
        initSearch();
        initManuelSearch();
        initLeftSearch();
        _initManuelClickDelegation();

        if (window.CLASSE_ID) {
            _loadStudents(window.CLASSE_ID);
        } else {
            // Aucune séance active : affiche un état vide dans le panel latéral
            const leftList = document.getElementById('left-student-list');
            if (leftList) {
                leftList.innerHTML = `
                    <div class="px-5 py-12 text-center text-muted-foreground-2">
                        <p class="text-xs">Aucune séance active.</p>
                        <p class="text-[10px] mt-1">La liste apparaîtra ici lors d'un cours.</p>
                    </div>`;
            }
        }
    });

    // ── Chargement dynamique des étudiants ────────────────────────────

    const _AVATAR_COLORS = [
        'bg-primary/10 text-primary',
        'bg-blue-500/10 text-blue-500',
        'bg-green-500/10 text-green-600',
        'bg-orange-500/10 text-orange-500',
        'bg-indigo-500/10 text-indigo-500',
        'bg-teal-500/10 text-teal-600',
    ];

    const _CHECKBOX_SVG = "data:image/svg+xml,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%2024%2024%22%20fill%3D%22none%22%20stroke%3D%22white%22%20stroke-width%3D%223.5%22%3E%3Cpath%20d%3D%22M20%206%209%2017l-5-5%22%2F%3E%3C%2Fsvg%3E";

    async function _loadStudents(classeId) {
        const container = document.getElementById('manuel-student-list');
        const leftList  = document.getElementById('left-student-list');
        if (!container) return;

        try {
            const { default: api } = await import('../common/ClientHttp.js');
            const page = await api.get(`/api/students/classes/${classeId}?size=500`);
            const students = (page && page.content) ? page.content : [];

            window.TOTAL_STUDENTS = students.length;
            _updateCountTotal(students.length);

            if (students.length === 0) {
                const emptyMsg = `<div class="px-6 py-10 text-center text-muted-foreground-2 text-sm">Aucun étudiant inscrit à cette séance.</div>`;
                container.innerHTML = emptyMsg;
                if (leftList) leftList.innerHTML = emptyMsg;
                return;
            }

            // ── Rendu dans le modal d'appel manuel ──────────────────────
            container.innerHTML = students.map((s, i) => {
                const initial  = (s.nom || '?').charAt(0).toUpperCase();
                const color    = _AVATAR_COLORS[i % _AVATAR_COLORS.length];
                const fullName = `${s.nom || ''} ${s.prenom || ''}`.trim().toLowerCase();
                return `
                <div class="manuel-student-row flex items-center gap-4 py-3.5 hover:bg-muted/10 transition-colors"
                     data-id="${s.userId}" data-name="${fullName}">
                    <div class="size-16 rounded-xl flex items-center justify-center text-xs font-bold shrink-0 ${color}">${initial}</div>
                    <div class="flex-1 min-w-0">
                        <p class="font-semibold text-layer-foreground">${s.nom || ''} ${s.prenom || ''}</p>
                        <p class="text-sm text-muted-foreground-2">${s.matricule || ''}</p>
                    </div>
                    <div class="size-2 rounded-full bg-muted transition-all duration-200 manuel-status-dot shrink-0"></div>
                    <label class="relative cursor-pointer shrink-0">
                        <input type="checkbox" class="peer sr-only manuel-checkbox"
                               data-student-id="${s.userId}" onchange="onManuelCheck(this)" />
                        <div class="size-6 rounded-lg border-2 border-layer-line bg-layer
                                    peer-checked:bg-primary peer-checked:border-primary
                                    peer-checked:bg-[url('${_CHECKBOX_SVG}')]
                                    peer-checked:bg-center peer-checked:bg-no-repeat peer-checked:bg-[length:70%_70%]
                                    hover:border-primary/60 transition-all duration-150"></div>
                    </label>
                </div>`;
            }).join('');

            // ── Rendu dans le panel latéral (#left-student-list) ────────
            if (leftList) {
                leftList.innerHTML = students.map((s, i) => {
                    const initial  = (s.nom || '?').charAt(0).toUpperCase();
                    const color    = _AVATAR_COLORS[i % _AVATAR_COLORS.length];
                    const fullName = `${s.nom || ''} ${s.prenom || ''}`.trim().toLowerCase();
                    return `
                    <div class="left-student-row flex justify-between items-center gap-3  py-3 hover:bg-muted/10 transition-colors cursor-default"
                         data-id="${s.userId}" data-name="${fullName}">
                         <div class="flex gap-2 items-center">
                            <div class="size-9 rounded-full flex items-center justify-center text-xs font-bold shrink-0 ${color}">${initial}</div>
                            <div class="flex-1 min-w-0">
                                <p class="text-sm font-semibold text-layer-foreground truncate">${s.nom || ''} ${s.prenom || ''}</p>
                                <p class="text-sm text-muted-foreground-2 truncate">${s.matricule || ''}</p>
                           
                            </div>
                         </div>
                       
                        <div class="flex gap-1 mt-1.5 px-2 divide-x rounded-xl border border-layer-line" id="left-blocks-${s.userId}">
                                ${_initialPresenceBlocks()}
                        </div>
                    </div>`;
                }).join('');
                initLeftSearch();
            }

            // Initialise blockStatuses pour tous les étudiants chargés.
            const numSlots = getHours().length;
            students.forEach(s => {
                if (!blockStatuses[s.userId]) {
                    blockStatuses[s.userId] = Array(numSlots).fill('empty');
                }
            });

            _updateManuelProgress();
            initManuelSearch();
            _initManuelClickDelegation();
            _startPresencePolling();
            _initAppelStartFromExistingAppels();

        } catch (e) {
            console.error('Erreur chargement étudiants:', e);
            const errMsg = `<div class="px-6 py-10 text-center text-red-500 text-sm">Erreur lors du chargement des étudiants.</div>`;
            if (container) container.innerHTML = errMsg;
            if (leftList)  leftList.innerHTML  = errMsg;
        }
    }

    function _updateCountTotal(count) {
        const elTotal = document.getElementById('count-total');
        if (elTotal) elTotal.textContent = count;

        const elCard = document.getElementById('seance-student-count');
        if (elCard) elCard.textContent = count + ' étudiant' + (count > 1 ? 's' : '');

        const elText = document.getElementById('manuel-count-text');
        if (elText) elText.textContent = `0 / ${count} marqué(s)`;

        const elLeft = document.getElementById('left-student-count');
        if (elLeft) elLeft.textContent = count;

        // Badge sur le bouton mobile
        const elBadge = document.getElementById('drawer-count-badge');
        if (elBadge) elBadge.textContent = count;
    }

    function initLeftSearch() {
        const input = document.getElementById('left-search');
        if (!input || input.dataset.initialized) return;
        input.dataset.initialized = 'true';
        input.addEventListener('input', e => {
            const q = e.target.value.toLowerCase().trim();
            document.querySelectorAll('.left-student-row').forEach(row => {
                row.classList.toggle('hidden', q.length > 0 && !(row.dataset.name || '').toLowerCase().includes(q));
            });
        });
    }

    // ══════════════════════════════════════════════════════════════════
    // ORCHESTRATION — Démarrage appel (nouveau flow modal)
    // ══════════════════════════════════════════════════════════════════

    let _attendanceType  = 'manuel';
    let _attendanceHours = 2;

    // IDs de la session d'appel en cours (persistés côté backend)
    let _presenceListId = window.PRESENCE_LIST_ID || null;
    let _appelId        = null;

    // Pointeur de progression dans la séance : heure de début du prochain appel.
    // Initialisé sur l'heure de début de la séance ; avancé après chaque appel créé.
    let _nextAppelStart = window.HEURE_DEBUT_STR || '08:00';

    // ── Utilitaires horaires ─────────────────────────────────────────────
    function _parseTimeStr(hhmm) {
        const [h, m] = hhmm.split(':').map(Number);
        return h * 60 + (m || 0);
    }
    function _fmtTime(totalMinutes) {
        return String(Math.floor(totalMinutes / 60)).padStart(2, '0') + ':' + String(totalMinutes % 60).padStart(2, '0');
    }
    function _remainingHours() {
        const start = _parseTimeStr(_nextAppelStart);
        const end   = _parseTimeStr(window.HEURE_FIN_STR || '12:00');
        return Math.max(0, Math.floor((end - start) / 60));
    }

    // Calcule la heureFin du prochain appel selon les heures choisies.
    function _computeHeureFin(type, hours) {
        return _fmtTime(_parseTimeStr(_nextAppelStart) + hours * 60);
    }

    // Retourne les indices de slots horaires compris entre debutStr et finStr ("HH:mm").
    function _computeSlotIndices(debutStr, finStr) {
        const hours = getHours();
        const dh = _parseTimeStr(debutStr);
        const fh = _parseTimeStr(finStr);
        return hours.map((h, i) => (h * 60 >= dh && h * 60 < fh) ? i : -1).filter(i => i >= 0);
    }

    // Synchronise _nextAppelStart avec les appels déjà enregistrés (cas page rechargée).
    async function _initAppelStartFromExistingAppels() {
        const listId = _presenceListId || window.PRESENCE_LIST_ID;
        if (!listId) return;
        try {
            const { default: api } = await import('../common/ClientHttp.js');
            const appels = await api.get(`/api/appels/presence-list/${listId}`);
            if (!appels || appels.length === 0) return;
            const maxFin = appels.reduce((max, a) => {
                const fin = (a.heureFin || '00:00').substring(0, 5);
                return fin > max ? fin : max;
            }, '00:00');
            if (maxFin > (window.HEURE_DEBUT_STR || '00:00')) {
                _nextAppelStart = maxFin;
            }
        } catch(e) { /* silencieux */ }
    }

    // Crée la PresenceList si elle n'existe pas encore.
    async function _ensurePresenceList() {
        if (_presenceListId) return;
        if (!window.SEANCE_ID || !window.ENSEIGNANT_ID) return;
        const { default: api } = await import('../common/ClientHttp.js');
        const pl = await api.post('/api/presences', {
            seanceId:     window.SEANCE_ID,
            classeId:     window.CLASSE_ID  || null,
            ueId:         window.COURS_ID   || null,
            enseignantId: window.ENSEIGNANT_ID,
            date:         new Date().toISOString().split('T')[0]
        });
        _presenceListId         = pl.id;
        window.PRESENCE_LIST_ID = pl.id;
        _connectWebSocket(pl.id);
    }

    // Crée un Appel (session de pointage) pour la PresenceList courante.
    // heureDebut/heureFin doivent être dans la plage de la séance (validé côté serveur).
    // Pour QR/PIN l'AttendanceCode est généré côté serveur et renvoyé dans AppelResponseDTO.
    async function _createAppel(typeAppel, dureeVieMinutes, heureDebut, heureFin) {
        if (!_presenceListId || !window.SEANCE_ID || !window.ENSEIGNANT_ID) return null;
        const debut = heureDebut || (_nextAppelStart + ':00');
        const fin   = heureFin   || (_computeHeureFin(typeAppel, _attendanceHours) + ':00');
        const etudiantIds = Object.keys(blockStatuses).map(Number).filter(Boolean);
        const { default: api } = await import('../common/ClientHttp.js');
        return await api.post('/api/appels', {
            presenceListId:  _presenceListId,
            seanceId:        window.SEANCE_ID,
            enseignantId:    window.ENSEIGNANT_ID,
            typeAppel:       typeAppel,
            heureDebut:      debut,
            heureFin:        fin,
            dureeVieMinutes: dureeVieMinutes || 0,
            etudiantIds:     etudiantIds.length > 0 ? etudiantIds : null
        });
    }

    // Clôture l'appel : le backend passe les InfoPresenceRow isPresent=null à false.
    async function _closeAppel() {
        if (!_appelId) return;
        try {
            const { default: api } = await import('../common/ClientHttp.js');
            await api.post(`/api/appels/${_appelId}/close`);
        } catch(e) { console.error('Erreur clôture appel:', e); }
    }

    // ── Appel Manuel inline (sans modal) ─────────────────────────────────

    let _manuelPresence   = {};    // { [studentId]: 'present' | 'absent' | null }
    let _manuelModeActive = false;
    let _currentAppelSlots = [];   // indices de slots horaires couverts par l'appel actif

    // Active le mode appel manuel : rend la liste gauche interactive.
    function _activateManuelMode() {
        _manuelModeActive = true;
        _manuelPresence   = {};

        document.querySelectorAll('.left-student-row').forEach(row => {
            _manuelPresence[row.dataset.id] = null;
            row.classList.remove('cursor-default');
            row.classList.add('cursor-pointer');
        });

        // Sur mobile, ouvrir automatiquement le drawer pour permettre l'appel
        if (window.innerWidth < 768) openClassDrawer();

        // Bascule les badges vers le mode toggle P/A
        Object.keys(_manuelPresence).forEach(sid => {
            const el = document.getElementById(`left-blocks-${sid}`);
            if (el) el.innerHTML = _renderManuelBadge(null);
        });

        // Bandeau indicatif au-dessus de la liste gauche
        const list = document.getElementById('left-student-list');
        if (list?.parentElement && !document.getElementById('manuel-mode-banner')) {
            const banner = document.createElement('div');
            banner.id = 'manuel-mode-banner';
            banner.className = 'px-3 py-2 bg-primary/5 border-b border-primary/20 flex items-center justify-between gap-2 sticky top-0 z-10 backdrop-blur-sm';
            banner.innerHTML = `
                <div class="flex items-center gap-2">
                    <div class="size-2 rounded-full bg-primary animate-pulse"></div>
                    <span class="text-xs font-semibold text-primary">Appel en cours — cliquez pour P/A</span>
                </div>
                <span class="text-xs font-bold text-primary" id="manuel-inline-progress">0 / ${Object.keys(_manuelPresence).length}</span>`;
            list.parentElement.insertBefore(banner, list);
        }

        // Remplace le bouton "Démarrer" par "Terminer"
        const startBtn = document.getElementById('btn-start-appel');
        if (startBtn) startBtn.classList.add('hidden');

        if (!document.getElementById('btn-end-manuel')) {
            const endBtn = document.createElement('button');
            endBtn.type = 'button';
            endBtn.id   = 'btn-end-manuel';
            endBtn.className = 'group w-full flex items-center justify-center gap-2 rounded-xl bg-green-600 px-4 py-2.5 text-sm font-semibold text-white shadow-lg shadow-green-600/20 transition-all hover:bg-green-700 active:scale-[0.98]';
            endBtn.onclick = confirmManuelAttendance;
            endBtn.innerHTML = `
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                Terminer l'appel`;
            startBtn?.parentElement?.insertBefore(endBtn, startBtn.nextSibling);
        }

        // Remplace le contenu des tabs par le résumé en cours
        const tabs = document.getElementById('attendance-tabs-container');
        if (tabs) tabs.classList.add('hidden');

        if (!document.getElementById('manuel-summary-zone')) {
            const total = Object.keys(_manuelPresence).length;
            const zone = document.createElement('div');
            zone.id = 'manuel-summary-zone';
            zone.className = 'mt-3 p-5 rounded-2xl bg-card border border-card-line flex flex-col gap-3';
            zone.innerHTML = `
                <div class="flex items-center justify-between gap-2 text-green-600">
                    <div class="flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                        <p class="text-sm font-semibold">Appel manuel actif</p>
                    </div>
                    <span class="text-xs font-mono font-bold bg-green-100 dark:bg-green-500/20 text-green-700 dark:text-green-400 px-2 py-0.5 rounded-lg">${_fmtTime(_parseTimeStr(_nextAppelStart) - _attendanceHours * 60)} → ${_nextAppelStart}</span>
                </div>
                <div class="flex gap-3">
                    <div class="flex-1 text-center py-2 rounded-xl bg-green-50 dark:bg-green-500/10 border border-green-200 dark:border-green-500/20">
                        <p class="text-xl font-black text-green-700 dark:text-green-400" id="manuel-count-p">0</p>
                        <p class="text-[10px] font-bold text-green-600/70 uppercase tracking-wide mt-0.5">Présents</p>
                    </div>
                    <div class="flex-1 text-center py-2 rounded-xl bg-red-50 dark:bg-red-500/10 border border-red-200 dark:border-red-500/20">
                        <p class="text-xl font-black text-red-600 dark:text-red-400" id="manuel-count-a">0</p>
                        <p class="text-[10px] font-bold text-red-600/70 uppercase tracking-wide mt-0.5">Absents</p>
                    </div>
                    <div class="flex-1 text-center py-2 rounded-xl bg-muted border border-card-line">
                        <p class="text-xl font-black text-muted-foreground" id="manuel-count-nd">${total}</p>
                        <p class="text-[10px] font-bold text-muted-foreground-2 uppercase tracking-wide mt-0.5">Non traités</p>
                    </div>
                </div>
                <p class="text-xs text-muted-foreground-2 text-center italic">Cliquez sur un étudiant pour basculer P ↔ A</p>`;
            const endBtn = document.getElementById('btn-end-manuel');
            endBtn?.parentElement?.insertBefore(zone, endBtn.nextSibling);
        }
    }

    // Désactive le mode manuel et restaure l'UI normale.
    function _deactivateManuelMode() {
        _manuelModeActive = false;

        document.getElementById('manuel-mode-banner')?.remove();
        document.getElementById('btn-end-manuel')?.remove();
        document.getElementById('manuel-summary-zone')?.remove();

        document.getElementById('btn-start-appel')?.classList.remove('hidden');
        document.getElementById('attendance-tabs-container')?.classList.remove('hidden');

        document.querySelectorAll('.left-student-row').forEach(row => {
            row.classList.add('cursor-default');
            row.classList.remove('cursor-pointer', 'bg-green-50/50', 'dark:bg-green-500/5');
        });

        // Sur mobile, refermer le drawer après confirmation
        if (window.innerWidth < 768) closeClassDrawer();

        // Synchronise blockStatuses uniquement sur les slots couverts par l'appel actif.
        // (évite d'écraser des créneaux d'autres appels ou de marquer des heures non couvertes)
        Object.entries(_manuelPresence).forEach(([sid, status]) => {
            if (blockStatuses[sid] && _currentAppelSlots.length > 0) {
                const fill = status === 'present' ? 'present' : status === 'absent' ? 'absent' : 'empty';
                _currentAppelSlots.forEach(i => { blockStatuses[sid][i] = fill; });
            }
            _renderLeftBlocks(sid);
        });
        _currentAppelSlots = [];
    }

    function _toggleManuelPresence(sid) {
        _manuelPresence[sid] = (_manuelPresence[sid] === 'present') ? 'absent' : 'present';
        _renderLeftBlocks(sid);
        const row = document.querySelector(`.left-student-row[data-id="${sid}"]`);
        if (row) {
            row.classList.toggle('bg-green-50/50',      _manuelPresence[sid] === 'present');
            row.classList.toggle('dark:bg-green-500/5', _manuelPresence[sid] === 'present');
        }
        _updateManuelInlineStats();
    }

    // Renvoie le HTML du badge P/A/— pour le panneau gauche.
    function _renderManuelBadge(status) {
        if (status === 'present') {
            return `<span class="inline-flex items-center justify-center w-7 h-7   text-green-700  dark:text-green-400  text-xs font-bold transition-all duration-200  select-none">P</span>`;
        }
        if (status === 'absent') {
            return `<span class="inline-flex items-center justify-center w-7 h-7   text-red-700   dark:text-red-400  text-xs font-bold transition-all duration-200  select-none">A</span>`;
        }
        return `<span class="inline-flex items-center justify-center w-7 h-7   text-muted-foreground-2  text-xs font-bold transition-all duration-200 select-none"> </span>`;
    }

    // Met à jour les compteurs P/A/ND dans la zone résumé.
    function _updateManuelInlineStats() {
        const vals = Object.values(_manuelPresence);
        const p    = vals.filter(v => v === 'present').length;
        const a    = vals.filter(v => v === 'absent').length;
        const nd   = vals.filter(v => v === null).length;

        const elP  = document.getElementById('manuel-count-p');
        const elA  = document.getElementById('manuel-count-a');
        const elND = document.getElementById('manuel-count-nd');
        const elPr = document.getElementById('manuel-inline-progress');
        if (elP)  elP.textContent  = p;
        if (elA)  elA.textContent  = a;
        if (elND) elND.textContent = nd;
        if (elPr) elPr.textContent = `${p + a} / ${vals.length}`;

        const cpG = document.getElementById('count-present');
        const caG = document.getElementById('count-absent');
        if (cpG) cpG.textContent = p;
        if (caG) caG.textContent = a;
    }

    // Délégation d'événement sur la liste — attachée une seule fois.
    function _initManuelClickDelegation() {
        const list = document.getElementById('left-student-list');
        if (!list || list.dataset.manuelDelegation) return;
        list.dataset.manuelDelegation = 'true';
        list.addEventListener('click', e => {
            if (!_manuelModeActive) return;
            const row = e.target.closest('.left-student-row');
            if (row?.dataset.id) _toggleManuelPresence(row.dataset.id);
        });
    }

    function openStartModal() {
        _attendanceType  = 'manuel';
        _attendanceHours = 1;
        _refreshTypeCards();
        _refreshModalTimeSummary();
        HSOverlay.open('#modal-start-appel');
    }

    function selectAttendanceType(type) {
        _attendanceType = type;
        _refreshTypeCards();
        _refreshModalTimeSummary();
    }

    function selectHours(h) {
        _attendanceHours = h;
        _refreshModalTimeSummary();
    }

    function _refreshTypeCards() {
        ['manuel', 'pin', 'qr'].forEach(t => {
            const el = document.getElementById(`type-card-${t}`);
            if (!el) return;
            const active = (t === _attendanceType);
            el.classList.toggle('border-primary', active);
            el.classList.toggle('bg-primary/5', active);
            el.classList.toggle('border-card-line', !active);
            el.classList.toggle('bg-card', !active);
            el.querySelectorAll('svg').forEach(svg => {
                svg.classList.toggle('text-primary', active);
                svg.classList.toggle('text-muted-foreground-2', !active);
            });
            el.querySelectorAll('span').forEach(s => {
                s.classList.toggle('text-primary', active);
                s.classList.toggle('text-muted-foreground-2', !active);
            });
        });
    }

    // Met à jour l'aperçu De→À, les boutons de durée et l'état du bouton Continuer.
    function _refreshModalTimeSummary() {
        const remaining = _remainingHours();

        // Clamp _attendanceHours dans [1, remaining]
        _attendanceHours = Math.max(1, Math.min(_attendanceHours, remaining));

        const heureFin = _computeHeureFin(_attendanceType.toUpperCase(), _attendanceHours);

        const elDebut   = document.getElementById('modal-heure-debut');
        const elFin     = document.getElementById('modal-heure-fin');
        const elRestant = document.getElementById('modal-heure-restant');
        if (elDebut)   elDebut.textContent   = _nextAppelStart;
        if (elFin)     elFin.textContent     = heureFin;
        if (elRestant) elRestant.textContent = remaining > 0 ? remaining + 'h' : '0h';

        // Sélecteur de durée visible pour tous les types
        document.getElementById('duration-selector')?.classList.remove('hidden');
        document.getElementById('manuel-duration-info')?.classList.add('hidden');

        _refreshHourBtns(remaining);

        // Désactive Continuer si plus de temps disponible
        const continueBtn = document.getElementById('modal-continue-btn');
        if (continueBtn) continueBtn.disabled = remaining <= 0;
    }

    function _refreshHourBtns(remaining) {
        const rem = remaining !== undefined ? remaining : _remainingHours();
        [1, 2, 3, 4].forEach(h => {
            const el = document.getElementById(`hour-btn-${h}`);
            if (!el) return;
            const available = h <= rem;
            const active    = (h === _attendanceHours) && available;
            el.classList.toggle('bg-primary', active);
            el.classList.toggle('text-white', active);
            el.classList.toggle('bg-muted', !active);
            el.classList.toggle('text-layer-foreground', !active);
            el.disabled = !available;
            el.classList.toggle('opacity-30', !available);
            el.classList.toggle('cursor-not-allowed', !available);
        });
    }

    async function startAttendance() {
        HSOverlay.close('#modal-start-appel');

        // Étape 1 : garantir l'existence d'une PresenceList en base
        try { await _ensurePresenceList(); } catch(e) { console.error('PresenceList creation failed:', e); }

        if (_attendanceType === 'manuel') {
            // Étape 2 (MANUEL) : durée choisie dans le modal, à partir de _nextAppelStart
            try {
                const debut = _nextAppelStart;
                const fin   = _computeHeureFin('MANUEL', _attendanceHours);
                const appel = await _createAppel('MANUEL', 0, debut + ':00', fin + ':00');
                if (appel) {
                    _appelId           = appel.id;
                    _currentAppelSlots = _computeSlotIndices(debut, fin);
                    _nextAppelStart    = (appel.heureFin || fin + ':00').substring(0, 5);
                }
            } catch(e) { console.error('Appel MANUEL creation failed:', e); }
            _activateManuelMode();
        } else {
            // Étape 2 (QR/PIN) : créer l'Appel avec code et afficher
            await _generateCodeInModal(_attendanceType, _attendanceHours);
        }
    }

    // ── Code PIN / QR dans les tabs (zone gauche) ────────────────────

    function _showAttendanceTabs(type) {
        const tabBtn = document.getElementById(type === 'qr' ? 'tab-qr-btn' : 'tab-pin-btn');
        if (tabBtn) tabBtn.click();

        const endPin = document.getElementById('btn-end-pin');
        const endQr  = document.getElementById('btn-end-qr');
        if (type === 'qr') {
            endQr?.classList.remove('hidden');
            endPin?.classList.add('hidden');
        } else {
            endPin?.classList.remove('hidden');
            endQr?.classList.add('hidden');
        }
    }

    const _PIN_DEFAULT_HTML = `
        <div class="flex flex-col items-center justify-center py-8 gap-3 text-center">
            <div class="size-12 rounded-2xl bg-muted flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" class="text-muted-foreground-2">
                    <rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/><circle cx="12" cy="16" r="1" fill="currentColor"/>
                </svg>
            </div>
            <p class="text-sm font-semibold text-layer-foreground">Aucun code PIN en cours</p>
            <p class="text-xs text-muted-foreground-2">Démarrez l'appel pour générer un code</p>
        </div>`;

    const _QR_DEFAULT_HTML = `
        <div class="flex flex-col items-center justify-center py-8 gap-3 text-center">
            <div class="size-12 rounded-2xl bg-muted flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" class="text-muted-foreground-2">
                    <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/>
                    <path d="M14 14h3v3h-3z"/><path d="M17 17h4v4h-4z"/><path d="M14 21h3"/>
                </svg>
            </div>
            <p class="text-sm font-semibold text-layer-foreground">Aucun QR code en cours</p>
            <p class="text-xs text-muted-foreground-2">Démarrez l'appel pour générer un QR code</p>
        </div>`;

    async function _generateCodeInModal(type, hours) {
        const displayId = type === 'qr' ? 'qr-display-zone' : 'pin-display-zone';
        _showAttendanceTabs(type);

        const display = document.getElementById(displayId);
        if (display) {
            const color = type === 'qr' ? 'purple' : 'blue';
            display.innerHTML = `
                <div class="flex flex-col items-center justify-center p-8 gap-3">
                    <div class="size-10 border-2 border-${color}-500 border-t-transparent rounded-full animate-spin"></div>
                    <p class="text-xs text-muted-foreground-2">Génération du code ${type.toUpperCase()}…</p>
                </div>`;
        }

        if (!_presenceListId || !window.SEANCE_ID || !window.ENSEIGNANT_ID) {
            if (display) display.innerHTML = `<p class="text-xs text-amber-600 p-4 text-center">Séance non configurée.</p>`;
            return;
        }

        try {
            // POST /api/appels crée l'Appel ET l'AttendanceCode sous-jacent en une seule requête.
            const duree = type === 'qr' ? 5 : 10;
            const debut = _nextAppelStart;
            const fin   = _computeHeureFin(type.toUpperCase(), _attendanceHours);
            const appel = await _createAppel(type.toUpperCase(), duree, debut + ':00', fin + ':00');
            if (!appel || !display) return;
            _appelId           = appel.id;
            _currentAppelSlots = _computeSlotIndices(debut, fin);
            _nextAppelStart    = (appel.heureFin || fin + ':00').substring(0, 5);

            if (type === 'qr') {
                display.innerHTML = `
                    <div class="flex flex-col items-center gap-4">
                        <div class="size-52 bg-white rounded-2xl border-4 border-purple-200 p-3 flex items-center justify-center" id="qr-container"></div>
                        <div class="text-center">
                            <p class="text-[10px] text-muted-foreground-2 uppercase tracking-widest font-bold">Expire dans</p>
                            <p class="text-3xl font-black text-purple-600 font-mono" id="qr-countdown">--:--</p>
                        </div>
                        <button onclick="_regenerateCode('qr')" class="w-full py-2.5 text-xsborder-layer-line border  rounded-lg  text-muted-foreground hover:bg-layer-hover transition-all">
                            Actualiser le QR Code
                        </button>
                    </div>`;
                const container = document.getElementById('qr-container');
                const qrContent = appel.valeur;
                if (typeof QRCode !== 'undefined') {
                    new QRCode(container, { text: qrContent, width: 190, height: 190, colorDark: '#7c3aed', colorLight: '#ffffff' });
                } else {
                    container.innerHTML = `<p class="text-xs font-mono text-purple-600 break-all text-center p-2">${qrContent}</p>`;
                }
                _startCountdown('qr-countdown', appel.dureeVieMinutes * 60);

            } else {
                const digits = appel.valeur.split('').map(d =>
                    `<div class="size-14 bg-muted border border-card-line rounded-xl flex items-center justify-center text-3xl font-black text-blue-600 font-mono">${d}</div>`
                ).join('');
                display.innerHTML = `
                    <div class="flex flex-col items-center gap-4">
                        <div class="flex gap-2">${digits}</div>
                        <div class="text-center">
                            <p class="text-[10px] text-muted-foreground-2 uppercase tracking-widest font-bold">Expire dans</p>
                            <p class="text-3xl font-black text-blue-500 font-mono" id="pin-countdown">--:--</p>
                        </div>
                        <div class="flex gap-2 w-full">
                            <button onclick="_regenerateCode('pin')" class="flex-1 py-2.5 text-xs font-bold rounded-xl bg-muted text-layer-foreground hover:bg-muted/80">Régénérer</button>
                            <button onclick="copyVAL('${appel.valeur}')" class="flex-1 py-2.5 text-xs font-bold rounded-xl bg-blue-500 text-white hover:bg-blue-600">Copier</button>
                        </div>
                    </div>`;
                _startCountdown('pin-countdown', appel.dureeVieMinutes * 60);
            }

            _startPresencePolling();
        } catch (e) {
            console.error('Erreur génération code:', e);
            if (display) display.innerHTML = `<p class="text-xs text-red-500 p-4 text-center">Erreur lors de la génération du code.</p>`;
        }
    }

    function _regenerateCode(type) {
        _generateCodeInModal(type, _attendanceHours);
    }

    function _startCountdown(elementId, totalSeconds) {
        let rem = totalSeconds;
        const tick = () => {
            const el = document.getElementById(elementId);
            if (!el) { clearInterval(t); return; }
            el.textContent = String(Math.floor(rem / 60)).padStart(2, '0') + ':' + String(rem % 60).padStart(2, '0');
            if (rem <= 0) { clearInterval(t); return; }
            rem--;
        };
        tick();
        const t = setInterval(tick, 1000);
    }

    async function confirmCodeAttendance() {
        // Clôture l'appel QR/PIN : les non-scanneurs deviennent absents
        await _closeAppel();

        const pinZone = document.getElementById('pin-display-zone');
        const qrZone  = document.getElementById('qr-display-zone');
        if (pinZone) pinZone.innerHTML = _PIN_DEFAULT_HTML;
        if (qrZone)  qrZone.innerHTML  = _QR_DEFAULT_HTML;
        document.getElementById('btn-end-pin')?.classList.add('hidden');
        document.getElementById('btn-end-qr')?.classList.add('hidden');
        endSession();
    }

    // ── Appel manuel ─────────────────────────────────────────────────

    function onManuelCheck(checkbox) {
        const row = checkbox.closest('.manuel-student-row');
        if (row) {
            const dot = row.querySelector('.manuel-status-dot');
            if (dot) dot.style.backgroundColor = checkbox.checked ? '#16a34a' : '#dc2626';
        }
        _updateManuelProgress();
    }

    function markAllManuel(present) {
        document.querySelectorAll('.manuel-checkbox').forEach(cb => {
            cb.checked = present;
            const row = cb.closest('.manuel-student-row');
            const dot = row?.querySelector('.manuel-status-dot');
            if (dot) dot.style.backgroundColor = present ? '#16a34a' : '#dc2626';
        });
        _updateManuelProgress();
    }

    function _updateManuelProgress() {
        const all    = document.querySelectorAll('.manuel-checkbox');
        const done   = document.querySelectorAll('.manuel-checkbox:checked').length;
        const total  = all.length || window.TOTAL_STUDENTS || 0;
        const pct    = total > 0 ? Math.round(done / total * 100) : 0;

        const bar   = document.getElementById('manuel-progress-bar');
        const pctEl = document.getElementById('manuel-progress-pct');
        const text  = document.getElementById('manuel-count-text');
        if (bar)   bar.style.width = pct + '%';
        if (pctEl) pctEl.textContent = pct + '%';
        if (text)  text.textContent = `${done} / ${total} marqué(s)`;
    }

    function initManuelSearch() {
        const input = document.getElementById('manuel-search');
        if (!input) return;
        input.addEventListener('input', e => {
            const q = e.target.value.toLowerCase().trim();
            document.querySelectorAll('.manuel-student-row').forEach(row => {
                const name = (row.dataset.name || '').toLowerCase();
                row.classList.toggle('hidden', q.length > 0 && !name.includes(q));
            });
        });
    }

    // ── Polling présence (scans QR/PIN) ──────────────────────────────
    // Rafraîchit les blocs A/P du panel gauche depuis l'API toutes les 10 s.
    // Nécessite window.PRESENCE_LIST_ID (injecté par Thymeleaf si liste ouverte)
    // et window.APPELS_CACHE (tableau des Appels de la liste, pour mapper les plages horaires).

    let _pollingTimer = null;

    async function _refreshPresenceFromAPI() {
        const listId = _presenceListId || window.PRESENCE_LIST_ID;
        if (!listId) return;

        try {
            const { default: api } = await import('../common/ClientHttp.js');

            // InfoPresenceRow = source de vérité par-appel :
            //   isPresent = true  → présent sur cet appel → créneaux couverts = 'present'
            //   isPresent = false → absent sur cet appel  → créneaux couverts = 'absent'
            //   isPresent = null  → appel en cours, statut non encore déterminé → 'empty'
            const infoRows = await api.get(`/api/presences/${listId}/info-rows`).catch(() => []);
            if (!infoRows || infoRows.length === 0) return;

            const hours   = getHours();
            const numSlots = hours.length;
            const rendered = new Set();

            infoRows.forEach(info => {
                const sid = String(info.etudiantId);
                if (!blockStatuses[sid]) blockStatuses[sid] = Array(numSlots).fill('empty');

                if (info.isPresent === true) {
                    _applyTimeRangeSlots(sid, info.heureDebut, info.heureFin, 'present', hours);
                } else if (info.isPresent === false) {
                    _applyTimeRangeSlots(sid, info.heureDebut, info.heureFin, 'absent', hours);
                }
                // null → slot reste 'empty' (appel non clôturé pour cet étudiant)

                rendered.add(sid);
            });

            rendered.forEach(sid => _renderLeftBlocks(sid));
        } catch (e) {
            // Silencieux : le polling n'est pas critique.
        }
    }

    // Applique un statut sur les créneaux couverts par une plage horaire donnée.
    // Utilise _parseTimeStr (minutes depuis minuit) pour un matching précis.
    function _applyTimeRangeSlots(sid, heureDebutStr, heureFinStr, status, hours) {
        if (!heureDebutStr || !heureFinStr) return;
        const dMin = _parseTimeStr(heureDebutStr.substring(0, 5));
        const fMin = _parseTimeStr(heureFinStr.substring(0, 5));
        hours.forEach((h, i) => {
            if (h * 60 >= dMin && h * 60 < fMin) {
                blockStatuses[sid][i] = status;
            }
        });
    }

    function _startPresencePolling() {
        if (!_presenceListId && !window.PRESENCE_LIST_ID) return;
        if (_pollingTimer) clearInterval(_pollingTimer);
        _refreshPresenceFromAPI();
        _pollingTimer = setInterval(_refreshPresenceFromAPI, 10000);
    }

    function _stopPresencePolling() {
        if (_pollingTimer) { clearInterval(_pollingTimer); _pollingTimer = null; }
    }

    async function confirmManuelAttendance() {
        const entries         = Object.entries(_manuelPresence);
        const presentStudents = entries.filter(([, s]) => s === 'present').map(([id]) => id);
        const total   = entries.length;
        const checked = presentStudents.length;
        const absent  = total - checked;

        // Persiste chaque présence via POST /api/appels/{appelId}/mark?etudiantId=
        if (_appelId && presentStudents.length > 0) {
            try {
                const { default: api } = await import('../common/ClientHttp.js');
                await Promise.all(
                    presentStudents.map(sid =>
                        api.post(`/api/appels/${_appelId}/mark?etudiantId=${sid}`)
                           .catch(e => console.error('Erreur marquage étudiant', sid, e))
                    )
                );
            } catch(e) {
                console.error('Erreur lors de la sauvegarde des présences:', e);
            }
        }

        // Clôture l'appel : les non-marqués deviennent absents (isPresent=false)
        await _closeAppel();

        _deactivateManuelMode();

        const elP = document.getElementById('final-present');
        const elA = document.getElementById('final-absent');
        const elL = document.getElementById('final-late');
        if (elP) elP.textContent = checked;
        if (elA) elA.textContent = absent;
        if (elL) elL.textContent = 0;

        const rate = total > 0 ? Math.round(checked / total * 100) : 0;
        const rateEl  = document.getElementById('final-rate');
        const rateBar = document.getElementById('final-rate-bar');
        if (rateEl)  rateEl.textContent  = rate + '%';
        if (rateBar) rateBar.style.width = rate + '%';

        const warn = document.getElementById('final-warning');
        if (warn) warn.style.display = 'none';

        HSOverlay.open('#modal-end-session');
    }

    // ── WebSocket — notifications présence en temps réel ────────────────
    // S'abonne au topic /topic/presences/{presenceListId} via STOMP/SockJS.
    // Affiche un toast animé (bas → haut) pour chaque étudiant marqué présent.

    let _stompClient = null;

    function _connectWebSocket(listId) {
        if (!listId) return;
        if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') return;
        if (_stompClient && _stompClient.connected) return;

        const socket = new SockJS('/ws');
        _stompClient = Stomp.over(socket);
        _stompClient.debug = null;

        _stompClient.connect({}, () => {
            _stompClient.subscribe(`/topic/presences/${listId}`, msg => {
                try { _showPresenceToast(JSON.parse(msg.body)); } catch (_) {}
            });
        }, () => {
            // Reconnexion automatique après 5 s si la connexion échoue.
            setTimeout(() => _connectWebSocket(listId), 5000);
        });
    }

   function _showPresenceToast(data) {
    const container = document.getElementById('presence-toast-container');
    if (!container) return;

    const initial = (data.prenom || '?')[0].toUpperCase();

    const avatarHtml = data.photoUrl
        ? `
        <img 
            src="${data.photoUrl}" 
            alt="${data.prenom || ''}"
            class="size-11 rounded-full object-cover border border-primary/20 shadow-sm shrink-0"
        >
        `
        : `
        <div
            class="size-11 rounded-full bg-gradient-to-br from-primary/20 to-primary/10 border border-primary/20 flex items-center justify-center text-primary font-bold shadow-sm shrink-0">
            ${initial}
        </div>
        `;

    const toast = document.createElement('div');
    toast.className = 'presence-toast';

    // Position horizontale aléatoire
    toast.style.left = (5 + Math.random() * 35) + '%';

    toast.innerHTML = `
        <div class="flex items-center gap-3">

            <!-- Avatar -->
            <div class="relative shrink-0">
                ${avatarHtml}

                <!-- Indicateur présence -->
                <div
                    class="absolute bottom-0 right-0 size-3 rounded-full bg-green-500 border-2 border-white">
                </div>
            </div>

            <!-- Bubble présence -->
            <div
                class="relative flex items-center gap-3 px-4 py-1 rounded-[24px] bg-card border border-layer-line shadow-md hover:shadow-lg transition-all duration-300 max-w-[260px]">

                <!-- Emoji SVG -->
                <div
                    class="size-8 rounded-full bg-amber-50 border border-amber-100 flex items-center justify-center shrink-0">

                    <img
                        src="https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f44b.svg"
                        alt="wave emoji"
                        class="size-5">
                </div>

                <!-- Nom -->
                <div class="min-w-0">
                    <span class="text-sm font-semibold text-muted-foreground truncate block">
                        ${data.prenom || ''} ${data.nom || ''}
                    </span>
                </div>

                <!-- Queue bulle -->
                <div
                    class="absolute left-[-6px] top-1/2 -translate-y-1/2 w-3 h-3 bg-card border-l border-b border-layer-line rotate-45">
                </div>

            </div>

        </div>
    `;

    container.appendChild(toast);

    // Suppression auto
    setTimeout(() => {
        toast.remove();
    }, 5000);
}

    // Connexion initiale si une liste est déjà ouverte (rechargement de page).
    if (window.PRESENCE_LIST_ID) _connectWebSocket(window.PRESENCE_LIST_ID);

    // ── Drawer mobile : #left-container utilisé comme bottom sheet ────────
    // Sur desktop (md+) : #left-container est toujours visible, ces fonctions ne font rien.
    // Sur mobile (<md)  : le même #left-container devient un panneau fixe en bas d'écran.

    function openClassDrawer() {
        if (window.innerWidth >= 768) return;
        const el = document.getElementById('left-container');
        const bd = document.getElementById('class-drawer-backdrop');
        if (!el) return;
        Object.assign(el.style, {
            position:      'fixed',
            bottom:        '0',
            left:          '0',
            right:         '0',
            width:         '100%',
            maxWidth:      'none',
            height:        '80vh',
            zIndex:        '110',
            borderRadius:  '1rem 1rem 0 0',
            borderRight:   'none',
            overflowY:     'auto',
            display:       'flex',
            flexDirection: 'column',
        });
        el.classList.remove('hidden');
        if (bd) bd.classList.remove('hidden');
    }

    function closeClassDrawer() {
        if (window.innerWidth >= 768) return;
        const el = document.getElementById('left-container');
        const bd = document.getElementById('class-drawer-backdrop');
        if (el) { el.removeAttribute('style'); el.classList.add('hidden'); }
        if (bd) bd.classList.add('hidden');
    }