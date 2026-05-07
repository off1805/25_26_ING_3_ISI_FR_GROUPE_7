document.addEventListener('DOMContentLoaded', () => {
    const sessionData = document.getElementById('session-data');
    if (!sessionData) return;

    const seanceId = sessionData.getAttribute('data-seance-id');
    const enseignantId = sessionData.getAttribute('data-enseignant-id');
    const totalHours = parseFloat(sessionData.getAttribute('data-total-hours') || 0);
    const markedHours = parseFloat(sessionData.getAttribute('data-marked-hours') || 0);

    const remainingHours = Math.max(0, totalHours - markedHours);

    // Update UI constraints
    const hoursInput = document.getElementById('attendance-hours');
    if (hoursInput) {
        hoursInput.value = Math.min(1, remainingHours);
        hoursInput.max = remainingHours;
        
        // Optionally add a label for remaining hours
        const label = hoursInput.previousElementSibling;
        if (label) {
            label.innerHTML = `Nombre d'heures (Reste: ${remainingHours.toFixed(1)}h)`;
        }
    }

    // UI Elements
    const btnLaunch = document.getElementById('btn-launch-attendance');
    const btnSubmitManual = document.getElementById('btn-submit-manual');
    const stepSelection = document.getElementById('drawer-step-selection');
    const stepManual = document.getElementById('drawer-step-manual');
    const stepDisplay = document.getElementById('drawer-step-display');
    
    const qrContainer = document.getElementById('qr-display-container');
    const pinContainer = document.getElementById('pin-display-container');
    const pinDigits = document.querySelectorAll('.pin-digit');
    const timerDisplay = document.getElementById('attendance-timer');

    let countdownInterval;

    // Launch Attendance
    btnLaunch.addEventListener('click', async () => {
        const mode = document.querySelector('input[name="attendance-mode"]:checked').value;
        const hours = document.getElementById('attendance-hours').value;

        if (mode === 'MANUAL') {
            stepSelection.classList.add('hidden');
            stepManual.classList.remove('hidden');
            return;
        }

        try {
            const response = await fetch('/api/attendance/launch', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    seanceId: parseInt(seanceId), 
                    enseignantId: parseInt(enseignantId), 
                    type: mode, 
                    heuresAMarquer: parseFloat(hours),
                    dureeVieMinutes: 15 // Default duration
                })
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || 'Erreur lors du lancement de l\'appel');
            }

            const data = await response.json();
            handleLaunchResult(mode, data);
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    });

    function handleLaunchResult(mode, data) {
        stepSelection.classList.add('hidden');

        if (mode === 'MANUAL') {
            stepManual.classList.remove('hidden');
        } else {
            stepDisplay.classList.remove('hidden');
            if (mode === 'QR') {
                qrContainer.classList.remove('hidden');
                // Mock QR representation (In a real app, use a QR library)
                document.getElementById('attendance-qr-code').innerHTML = `<div class="w-full h-full flex items-center justify-center bg-black text-white text-[10px] p-4 text-center">QR CODE: ${data.code}</div>`;
            } else if (mode === 'PIN') {
                pinContainer.classList.remove('hidden');
                const codeStr = data.code.toString();
                pinDigits.forEach((el, i) => {
                    el.textContent = codeStr[i] || '-';
                });
            }
            startTimer(15 * 60); // 15 minutes
        }
    }

    // Manual Submission
    btnSubmitManual.addEventListener('click', async () => {
        const studentCheckboxes = Array.from(document.querySelectorAll('.student-manual-check'));
        
        const presentStudentIds = studentCheckboxes
            .filter(cb => cb.checked)
            .map(cb => parseInt(cb.getAttribute('data-id')));
        
        const allStudentIds = studentCheckboxes
            .map(cb => parseInt(cb.getAttribute('data-id')));
        
        const hours = parseFloat(document.getElementById('attendance-hours').value);

        try {
            const response = await fetch('/api/attendance/submit-manual', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    seanceId: parseInt(seanceId),
                    enseignantId: parseInt(enseignantId),
                    presentStudentIds: presentStudentIds,
                    allStudentIds: allStudentIds,
                    hoursToMark: hours
                })
            });

            if (!response.ok) throw new Error('Erreur lors de la validation');

            alert('Présence validée avec succès');
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    });

    function startTimer(duration) {
        clearInterval(countdownInterval);
        let timer = duration;
        countdownInterval = setInterval(() => {
            const minutes = Math.floor(timer / 60);
            const seconds = timer % 60;
            timerDisplay.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            if (--timer < 0) {
                clearInterval(countdownInterval);
                timerDisplay.textContent = "00:00";
                alert("Le temps d'appel est écoulé.");
            }
        }, 1000);
    }

    // Student table search
    const tableSearch = document.getElementById('student-table-search');
    if (tableSearch) {
        tableSearch.addEventListener('input', (e) => {
            const term = e.target.value.toLowerCase();
            const rows = document.querySelectorAll('tbody tr.group');
            rows.forEach(row => {
                const text = row.innerText.toLowerCase();
                if (text.includes(term)) {
                    row.classList.remove('hidden');
                } else {
                    row.classList.add('hidden');
                }
            });
        });
    }
});
