export function getCalendar() {
    // Date d'aujourd'hui
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const currentMonth = today.getMonth();
    const currentYear = today.getFullYear();

    // Mois et année courants pour l'affichage
    const currentMonthName = today.toLocaleString('fr', { month: 'long' });
    const monthYearTitle = `${currentMonthName} ${currentYear}`;

    // Trouver le lundi de la semaine courante
    const currentMonday = new Date(today);
    const dayOfWeek = today.getDay();
    const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    currentMonday.setDate(today.getDate() + diffToMonday);

    // Semaines à afficher
    const weeksOffset = [-7, 0, 7];

    const weekDays = ['Lu', 'Ma', 'Me', 'Je', 'Ve', 'Sa', 'Di'];

    let weeksHtml = '';

    for (const offset of weeksOffset) {
        const weekStart = new Date(currentMonday);
        weekStart.setDate(currentMonday.getDate() + offset);

        let daysHtml = '';
        for (let i = 0; i < 7; i++) {
            const currentDate = new Date(weekStart);
            currentDate.setDate(weekStart.getDate() + i);
            const dayNumber = currentDate.getDate();
            const isToday = currentDate.toDateString() === today.toDateString();
            const isCurrentMonth = (currentDate.getMonth() === currentMonth && currentDate.getFullYear() === currentYear);

            // Déterminer les classes CSS
            let btnClass = 'm-px size-10 flex justify-center items-center rounded-full focus:outline-hidden ';

            if (isToday) {
                btnClass += 'bg-primary border-[1.5px] border-transparent text-sm font-medium text-primary-foreground hover:border-primary-hover';
            } else if (isCurrentMonth) {
                btnClass += 'border-[1.5px] border-transparent text-sm text-foreground hover:border-primary-hover hover:text-primary-hover';
            } else {
                // Jours hors mois : grisés, pas d'interaction
                btnClass += 'border-[1.5px] border-transparent text-sm text-muted-foreground-1 opacity-60 cursor-default';
            }

            daysHtml += `
        <div>
          <button type="button" class="${btnClass}" ${!isCurrentMonth && !isToday ? 'disabled' : ''}>
            ${dayNumber}
          </button>
        </div>
      `;
        }
        weeksHtml += `<div class="flex">${daysHtml}</div>`;
    }

    const weekHeader = `
    <div class="flex pb-1.5">
      ${weekDays.map(day => `<span class="m-px w-10 block text-center text-sm text-muted-foreground-1">${day}</span>`).join('')}
    </div>
  `;

    const monthHeader = `
    <div class="grid grid-cols-5 items-center gap-x-3 mx-1.5 pb-3">
      <div class="col-span-1"></div>
      <div class="col-span-3 flex justify-center items-center gap-x-1">
        <span class="text-sm font-medium text-foreground">${monthYearTitle}</span>
      </div>
      <div class="col-span-1"></div>
    </div>
  `;

    return `
    <div class="w-80 flex flex-col bg-dropdown overflow-hidden">
      <div class="p-3 space-y-0.5">
        ${monthHeader}
        ${weekHeader}
        ${weeksHtml}
      </div>
    </div>
  `;
}