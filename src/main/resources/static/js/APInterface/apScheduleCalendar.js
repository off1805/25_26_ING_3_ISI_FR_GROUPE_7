import { Calendar } from '/js/vanilla-calendar.min.js';

export function initAPScheduleCalendar() {
    let startDate = null;
    const currentDate = new Date().getFullYear();
    const dropdownButton = document.getElementById('btn-schedule-calendar');
    const originalButtonText = dropdownButton ? dropdownButton.innerHTML : 'Calendrier';

    const toISO = (d) => {
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    const formatFR = (d) => {
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        return `${day}/${month}/${year}`;
    };

    const options = {
        type: 'default',
        disableDatesPast: true,
        enableEdgeDatesOnly: true,
        selectionDatesMode: 'multiple-ranged',
        displayDateMin: `${currentDate}-01-01`,
        displayDateMax: `${currentDate + 1}-12-31`,

        onClickDate(self, event) {
            console.log(self);
            const clickedDate = event.target.ariaLabel;
            if (!clickedDate) return;

            console.log('Cliqué:', clickedDate, '| startDate:', startDate);

            if (!startDate) {
                console.log('first click')
                startDate = new Date(clickedDate);

                const max = new Date(startDate);
                max.setDate(max.getDate() + 6); // +6 = 7 jours inclusif

                const beforeStart = new Date(startDate);
                beforeStart.setDate(beforeStart.getDate() - 1);

                const afterMax = new Date(max);
                afterMax.setDate(afterMax.getDate() + 1);

                console.log("limite gauche blocage:", toISO(beforeStart));
                console.log("limite droite blocage:", toISO(afterMax));

                self.disableDates = [
                    `${currentDate}-01-01:${toISO(beforeStart)}`,
                    `${toISO(afterMax)}:${currentDate + 1}-12-31`,
                ];

                self.update({
                    dates: false,
                    month: false,
                    year: false,
                    holidays: false,
                });

            } else {
                console.log('second click')
                const endDate = new Date(clickedDate);

                // Enhancement: Update the dropdown button with the chosen range
                if (dropdownButton) {
                    const rangeText = `${formatFR(startDate)} — ${formatFR(endDate)}`;
                    dropdownButton.innerHTML = `
                        <span class="font-bold text-primary">${rangeText}</span>
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-calendar-week"><path stroke="none" d="M0 0h24v24H0z" fill="none" /><path d="M4 7a2 2 0 0 1 2 -2h12a2 2 0 0 1 2 2v12a2 2 0 0 1 -2 2h-12a2 2 0 0 1 -2 -2v-12" /><path d="M16 3v4" /><path d="M8 3v4" /><path d="M4 11h16" /><path d="M7 14h.013" /><path d="M10.01 14h.005" /><path d="M13.01 14h.005" /><path d="M16.015 14h.005" /><path d="M13.015 17h.005" /><path d="M7.01 17h.005" /><path d="M10.01 17h.005" /></svg>
                    `;
                }

                startDate = null;
                self.disableDates = [];
                self.disableDatesPast = true;

                self.update({
                    dates: false,
                    month: false,
                    year: false,
                    holidays: false,
                });
            }
        }
    };

    const calendar = new Calendar('#calendar', options);
    calendar.init();
    return calendar;
}
