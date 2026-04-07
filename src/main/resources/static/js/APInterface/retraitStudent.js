// ── CSRF ─────────────────────────────────────────────────────────────────
    const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]')?.content;
    const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]')?.content;

    // ── Sélection checkboxes ──────────────────────────────────────────────────
    function getCheckedCheckboxes() {
        return [...document.querySelectorAll('.student-checkbox:checked')];
    }

    function onCheckboxChange() {
        const checked = getCheckedCheckboxes();
        const bar   = document.getElementById('bulk-actions-bar');
        const count = document.getElementById('selected-count');
        if (checked.length > 0) {
            bar.classList.remove('hidden');
            count.textContent = checked.length + ' sélectionné(s)';
        } else {
            bar.classList.add('hidden');
        }
        // Sync "tout sélectionner"
        document.querySelectorAll('.select-all-checkbox').forEach(cb => {
            const panel = document.getElementById(cb.dataset.panel);
            if (!panel) return;
            const total = panel.querySelectorAll('.student-checkbox').length;
            const chk   = panel.querySelectorAll('.student-checkbox:checked').length;
            cb.indeterminate = chk > 0 && chk < total;
            cb.checked       = total > 0 && chk === total;
        });
    }

    function toggleSelectAll(masterCb) {
        const panel = document.getElementById(masterCb.dataset.panel);
        if (!panel) return;
        panel.querySelectorAll('.student-checkbox').forEach(cb => cb.checked = masterCb.checked);
        onCheckboxChange();
    }

    function selectAllInPanel(btn) {
        const panel = document.getElementById(btn.dataset.panel);
        if (!panel) return;
        panel.querySelectorAll('.student-checkbox').forEach(cb => cb.checked = true);
        onCheckboxChange();
    }

    function clearSelection() {
        document.querySelectorAll('.student-checkbox, .select-all-checkbox').forEach(cb => {
            cb.checked = false;
            cb.indeterminate = false;
        });
        document.getElementById('bulk-actions-bar').classList.add('hidden');
    }

    // ── Retrait ───────────────────────────────────────────────────────────────
    let pendingUserIds  = [];
    let pendingClasseId = null;

    function removeSelected() {
        const checked = getCheckedCheckboxes();
        if (checked.length === 0) return;
        pendingUserIds  = checked.map(cb => cb.dataset.userId);
        pendingClasseId = checked[0].dataset.classeId;
        document.getElementById('confirm-remove-msg').textContent =
            checked.length === 1
                ? 'Cet étudiant sera retiré de la classe mais son compte sera conservé.'
                : checked.length + ' étudiants seront retirés de la classe. Leurs comptes seront conservés.';
        HSOverlay.open('#modal-confirm-remove');
    }

    function removeSingle(btn) {
        pendingUserIds  = [btn.dataset.userId];
        pendingClasseId = btn.dataset.classeId;
        document.getElementById('confirm-remove-msg').textContent =
            'Cet étudiant sera retiré de la classe mais son compte sera conservé.';
        HSOverlay.open('#modal-confirm-remove');
    }

    document.getElementById('confirm-remove-btn').addEventListener('click', async () => {
        HSOverlay.close('#modal-confirm-remove');

        for (const userId of pendingUserIds) {
            try {
                const headers = { 'X-Requested-With': 'XMLHttpRequest' };
                if (CSRF_HEADER && CSRF_TOKEN) headers[CSRF_HEADER] = CSRF_TOKEN;

                const resp = await fetch(
`/api/students/${userId}/classes/${pendingClasseId}`,
                    { method: 'DELETE', headers }
                );

                if (!resp.ok) {
                    const msg = await resp.text().catch(() => 'Erreur serveur');
                    console.error('Erreur retrait:', msg);
                    // TODO : Toastify.toast({ text: 'Erreur : ' + msg, style: { background: '#ef4444' } });
                    continue;
                }

                // Retirer la ligne du tableau sans rechargement
                const row = document.querySelector(`.student-row[data-user-id="${userId}"]`);
                if (row) row.remove();

            } catch (err) {
                console.error('Erreur réseau:', err);
            }
        }

        clearSelection();
        pendingUserIds  = [];
        pendingClasseId = null;
    });

    // ── Recherche client ───────────────────────────────────────────────────────
    document.getElementById('student-search')?.addEventListener('input', function () {
        const q = this.value.toLowerCase().trim();
        document.querySelectorAll('.student-row').forEach(row => {
            row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
        });
    });

    document.addEventListener('DOMContentLoaded', () => {
        if (typeof HSStaticMethods !== 'undefined') HSStaticMethods.autoInit();
    });
    window.addEventListener('load', () => {
        if (typeof HSStaticMethods !== 'undefined') HSStaticMethods.autoInit();
    });