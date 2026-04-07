const HEURE_DEBUT_STR = /*[[${seance != null ? #temporals.format(seance.heureDebut,'HH:mm') : '08:00'}]]*/ '08:00';
    const HEURE_FIN_STR   = /*[[${seance != null ? #temporals.format(seance.heureFin,'HH:mm') : '12:00'}]]*/ '12:00';
    const SEANCE_ID       = /*[[${seance != null ? seance.id : null}]]*/ null;
    const TOTAL_STUDENTS  = /*[[${#lists.size(etudiants)}]]*/ 0;

    // studentId → [status_h0, status_h1, status_h2, status_h3]
    // 'empty' | 'present' | 'late' | 'absent'
    const blockStatuses = {};

    // ── Init blocks ───────────────────────────────────────────────────
    function initBlocks() {
        document.querySelectorAll('.hour-blocks-container').forEach(container => {
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
    function updateTimeBar() {
        const now = new Date();
        const [dh,dm] = HEURE_DEBUT_STR.split(':').map(Number);
        const [fh,fm] = HEURE_FIN_STR.split(':').map(Number);
        const start = dh*60+dm, end = fh*60+fm;
        const cur   = now.getHours()*60+now.getMinutes();
        const pct   = Math.min(100, Math.max(0, (cur-start)/(end-start)*100));
        const bar   = document.getElementById('time-progress');
        if (bar) bar.style.width = pct+'%';
        const elapsed = Math.max(0, cur-start);
        const el = document.getElementById('time-elapsed');
        if (el) el.textContent = Math.floor(elapsed/60)+'h'+String(elapsed%60).padStart(2,'0')+' écoulées';
    }
    updateTimeBar(); setInterval(updateTimeBar, 60000);

    // ── Blocs horaires ────────────────────────────────────────────────
    const STATUS_CYCLE = ['empty','present','late','absent'];
    const STATUS_COLOR = { present:'#16a34a', late:'#d97706', absent:'#dc2626', empty:'transparent' };

    function setBlock(block, status) {
        const sid   = block.dataset.studentId;
        const hour  = parseInt(block.dataset.hour);
        // Retirer toutes les classes de statut
        block.classList.remove('empty','present','late','absent','active');
        block.classList.add(status);
        if (status !== 'empty') block.classList.add('active');
        block.dataset.status = status;
        blockStatuses[sid][hour] = status;
        updateRowIndicator(sid);
        updateCounts();
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

    // Clic simple — cycle status
    function onBlockClick(block) {
        const cur = STATUS_CYCLE.indexOf(block.dataset.status);
        const next = STATUS_CYCLE[(cur + 1) % STATUS_CYCLE.length];
        setBlock(block, next);
    }

    // Drag pour étendre le statut sur plusieurs blocs
    let dragState = null;

    function initBlockDrag() {
        document.querySelectorAll('.hour-blocks-container').forEach(container => {
const blocks = Array.from(container.querySelectorAll('.hour-block'));
            // Mouse
            blocks.forEach(block => {
                block.addEventListener('click', () => onBlockClick(block));

                block.addEventListener('mousedown', e => {
                    e.preventDefault();
                    dragState = {
                        sid: block.dataset.studentId,
                        status: STATUS_CYCLE[(STATUS_CYCLE.indexOf(block.dataset.status) + 1) % STATUS_CYCLE.length],
                        startHour: parseInt(block.dataset.hour),
                        active: true
                    };
                });
            });

            container.addEventListener('mousemove', e => {
                if (!dragState || !dragState.active) return;
                const target = e.target.closest('.hour-block');
                if (!target || target.dataset.studentId !== dragState.sid) return;
                const h = parseInt(target.dataset.hour);
                const min = Math.min(dragState.startHour, h);
                const max = Math.max(dragState.startHour, h);
                blocks.forEach(b => {
                    const bh = parseInt(b.dataset.hour);
                    if (bh >= min && bh <= max) {
                        if (b.dataset.status !== dragState.status) setBlock(b, dragState.status);
                    }
                });
            });
        });

        document.addEventListener('mouseup', () => { dragState = null; });

        // Touch swipe sur ligne
        document.querySelectorAll('.student-row').forEach(row => {
            let tx = 0, ty = 0, moved = false;
            row.addEventListener('touchstart', e => { tx = e.touches[0].clientX; ty = e.touches[0].clientY; moved = false; });
            row.addEventListener('touchmove', e => {
                if (Math.abs(e.touches[0].clientX - tx) > 20) moved = true;
            });
            row.addEventListener('touchend', e => {
                if (!moved) return;
                const dx = e.changedTouches[0].clientX - tx;
                const sid = row.dataset.id;
                const status = dx > 40 ? 'present' : dx < -40 ? 'absent' : null;
                if (!status) return;
                const container = row.querySelector('.hour-blocks-container');
                container?.querySelectorAll('.hour-block').forEach(b => setBlock(b, status));
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
        const total = TOTAL_STUDENTS || Object.keys(blockStatuses).length;
        document.getElementById('progress-text').textContent = done + ' / ' + total + ' étudiants traités';
        const pct = total > 0 ? done/total*100 : 0;
        document.getElementById('attendance-progress').style.width = pct + '%';
    }

    function markAllBlocks(status) {
        document.querySelectorAll('.hour-blocks-container').forEach(container => {
            container.querySelectorAll('.hour-block').forEach(b => setBlock(b, status));
        });
    }

    function resetAllBlocks() {
        document.querySelectorAll('.hour-blocks-container').forEach(container => {
            container.querySelectorAll('.hour-block').forEach(b => setBlock(b, 'empty'));
        });
    }

    // ── Method switch ─────────────────────────────────────────────────
    function switchMethod(method) {
        // Rien à switcher visuellement, le Manuel est toujours visible
        // QR/PIN ouvrent le panel latéral
    }

    // ── Side Panel ────────────────────────────────────────────────────
    let panelTimerQR = null, panelTimerPIN = null;

    function openSidePanel(type) {
        const panel   = document.getElementById('side-panel');
        const overlay = document.getElementById('side-panel-overlay');
        const qrC     = document.getElementById('sp-qr-content');
        const pinC    = document.getElementById('sp-pin-content');
        const title   = document.getElementById('sp-title');
        const subtitle = document.getElementById('sp-subtitle');

        if (type === 'qr') {
            qrC.classList.remove('hidden');
            pinC.classList.add('hidden');
            title.textContent = 'QR Code de présence';
            subtitle.textContent = 'Les étudiants scannent pour se marquer présents';
            startPanelTimer('qr', 299);
        } else {
            pinC.classList.remove('hidden');
            qrC.classList.add('hidden');
            title.textContent = 'Code PIN de présence';
            subtitle.textContent = 'Dictez ce code aux étudiants';
            startPanelTimer('pin', 599);
        }

        panel.classList.add('open');
        overlay.classList.add('open');
    }

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

    function regenerateCode(type) { startPanelTimer(type, type==='qr'?299:599); }

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

    // ── Init ──────────────────────────────────────────────────────────
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof HSStaticMethods !== 'undefined') HSStaticMethods.autoInit();
        initBlocks();
        buildHoursLegend();
        initBlockDrag();
        startPanelTimer('qr', 299);
        startPanelTimer('pin', 599);
    });