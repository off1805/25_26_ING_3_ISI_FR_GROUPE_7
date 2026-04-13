const HEURE_DEBUT_STR = /*[[${seance != null ? #temporals.format(seance.heureDebut,'HH:mm') : '23:00'}]]*/ '23:00';
    const HEURE_FIN_STR   = /*[[${seance != null ? #temporals.format(seance.heureFin,'HH:mm') : '00:00'}]]*/ '23:50';
    const SEANCE_ID       = /*[[${seance != null ? seance.id : null}]]*/ null;
    const TOTAL_STUDENTS  = /*[[${#lists.size(etudiants)}]]*/ 0;

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
        return [dh, dh+1, dh+2, dh+3];
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
        if (!row) return;
        const blocks = blockStatuses[sid];
        // Couleur dominante
        const counts = { present:0, late:0, absent:0 };
        blocks.forEach(b => { if (b !== 'empty') counts[b]++; });
        const dominant = Object.entries(counts).sort((a,b)=>b[1]-a[1])[0];
        const ind = row.querySelector('.status-indicator');
        if (ind) ind.style.background = dominant[1] > 0 ? STATUS_COLOR[dominant[0]] : 'transparent';
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
        document.getElementById('count-present').textContent = p;
        document.getElementById('count-late').textContent    = l;
        document.getElementById('count-absent').textContent  = a;
        
        const cp2 = document.getElementById('count-present-2');
        if (cp2) cp2.textContent = p;
        const ca2 = document.getElementById('count-absent-2');
        if (ca2) ca2.textContent = a;

        const total = TOTAL_STUDENTS || Object.keys(blockStatuses).length;
        document.getElementById('progress-text').textContent = done + ' / ' + total + ' étudiants traités';
        const pct = total > 0 ? done/total*100 : 0;
        document.getElementById('attendance-progress').style.width = pct + '%';
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
            const res = await fetch('/api/attendance-codes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    seanceId: seanceId,
                    enseignantId: enseignantId,
                    type: type.toUpperCase(),
                    heuresAMarquer: 2.0,
                    dureeVieMinutes: type === 'qr' ? 5 : 10
                })
            });
            if (!res.ok) throw new Error('Erreur API');
            const code = await res.json();

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
        navigator.clipboard?.writeText(val).then(() => alert('Copié : ' + val));
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
        if (notDone > 0) {
            warn.classList.remove('hidden');
            warnMsg.textContent = notDone + ' étudiant(s) sans statut seront marqués absents.';
        } else { warn.classList.add('hidden'); }

        HSOverlay.open('#modal-end-session');
    }

    function confirmEnd() {
        HSOverlay.close('#modal-end-session');
        window.location.href = /*[[@{/teacher/dashboard}]]*/ '/teacher/dashboard';
    }

    function exportFiche() { alert('Fiche d\'appel exportée !'); }

    // ── Search ───────────────────────────────────────────────────────
    function initSearch() {
        const input = document.getElementById('student-search');
        if (!input) return;
        input.addEventListener('input', (e) => {
            const q = e.target.value.toLowerCase().trim();
            document.querySelectorAll('.student-row').forEach(row => {
                const name = row.querySelector('p').textContent.toLowerCase();
                const mat  = row.querySelector('.text-muted-foreground-2').textContent.toLowerCase();
                if (name.includes(q) || mat.includes(q)) {
                    row.classList.remove('hidden');
                } else {
                    row.classList.add('hidden');
                }
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
    });