const days_fr   = ['Dimanche','Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi'];
    const months_fr = ['janvier','février','mars','avril','mai','juin','juillet','août','septembre','octobre','novembre','décembre'];
    const now = new Date();
    const heroDate = document.getElementById('hero-date');
    if (heroDate) heroDate.textContent = days_fr[now.getDay()] + ' ' + now.getDate() + ' ' + months_fr[now.getMonth()] + ' ' + now.getFullYear();

    // Mini calendrier semaine — panel droit dark
    (function() {
        const container = document.getElementById('week-mini');
        if (!container) return;
        const todayIdx = now.getDay() === 0 ? 5 : now.getDay() - 1;
        const lbls = ['L','M','M','J','V','S'];
        lbls.forEach((l, i) => {
            const d = new Date(now);
            d.setDate(now.getDate() - todayIdx + i);
            const isToday = i === todayIdx;
            const isPast  = i < todayIdx;
            const hasCourse = [0,1,2,4].includes(i);
            const div = document.createElement('div');
            div.className = 'flex flex-col items-center gap-1.5';
            div.innerHTML =
                '<span class="text-xs font-semibold ' + (isToday ? 'text-violet-400' : 'text-white/30') + '">' + l + '</span>' +
                '<div class="size-8 rounded-xl flex items-center justify-center text-xs font-bold transition-all ' +
                (isToday ? 'bg-violet-500 text-white shadow-lg shadow-violet-500/30' :
                 isPast && hasCourse ? 'bg-green-500/20 text-green-400' :
                 hasCourse ? 'bg-white/10 text-white/70' : 'text-white/20') +
                '">' + d.getDate() + '</div>';
            container.appendChild(div);
        });
        const wl = document.getElementById('week-label-small');
        if (wl) {
            const mon = new Date(now); mon.setDate(now.getDate() - todayIdx);
            const sat = new Date(mon); sat.setDate(mon.getDate() + 5);
            wl.textContent = mon.getDate() + ' – ' + sat.getDate() + ' ' + months_fr[sat.getMonth()];
        }
    })();

    function startCountdown(id, s) {
        const el = document.getElementById(id); if (!el) return;
        let r = s;
        const t = setInterval(() => {
            r--;
            if (r <= 0) { clearInterval(t); el.textContent = '00:00'; return; }
            el.textContent = String(Math.floor(r/60)).padStart(2,'0') + ':' + String(r%60).padStart(2,'0');
        }, 1000);
    }

    document.addEventListener('DOMContentLoaded', () => {
        if (typeof HSStaticMethods !== 'undefined') HSStaticMethods.autoInit();
        startCountdown('qr-countdown', 299);
        startCountdown('pin-countdown', 599);
    });