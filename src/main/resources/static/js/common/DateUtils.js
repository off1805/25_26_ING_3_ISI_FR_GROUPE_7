export function attach7DaysLimit(inputId) {
  const input = document.getElementById(inputId);

  function waitCalendar() {
    // ⚠️ selon version Preline
    const calendar =
      input._hsDatepicker?.calendar ||
      input._calendar ||
      input.calendar;

    if (!calendar) {
      requestAnimationFrame(waitCalendar);
      return;
    }

    let startDate = null;
    console.log("ok");
    calendar.update({
      actions: {
        clickDay(event, self) {
          const selected = self.selectedDates;

          // 1️⃣ premier clic
          if (selected.length === 1) {
            startDate = new Date(selected[0]);

            const maxDate = new Date(startDate);
            maxDate.setDate(maxDate.getDate() + 7);

            self.update({
              settings: {
                disabled: {
                  dates: (date) =>
                    date < startDate || date > maxDate
                }
              }
            });
          }

          // 2️⃣ deuxième clic
          if (selected.length === 2) {
            self.update({
              settings: {
                disabled: {
                  dates: []
                }
              }
            });
          }
        }
      }
    });
  }

  waitCalendar();
}