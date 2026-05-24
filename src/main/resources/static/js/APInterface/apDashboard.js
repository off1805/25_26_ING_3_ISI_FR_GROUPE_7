// ── State ─────────────────────────────────────────────────────────────────────
let currentView   = 'bar';   // 'bar' | 'line'
let currentClasse = 'all';
let currentPeriod = 90;      // days, 0 = all
let activeChart   = null;

// ── Init ──────────────────────────────────────────────────────────────────────
function init() {
    if (typeof HSStaticMethods !== 'undefined') HSStaticMethods.autoInit();
    populateClasseFilter();
    bindControls();
    renderChart();
}

// ── Filtre classe — population dynamique ─────────────────────────────────────
function populateClasseFilter() {
    const select = document.getElementById('classe-filter');
    if (!select) return;
    const classes = allClasses();
    classes.forEach(cls => {
        const opt = document.createElement('option');
        opt.value = cls;
        opt.textContent = cls;
        select.appendChild(opt);
    });
    if (classes.length <= 1) select.style.display = 'none';
}

function allClasses() {
    const fromBar  = window.dashboardData?.absences?.labels ?? [];
    const fromLine = [...new Set((window.dashboardData?.evolution ?? []).map(e => e.classeCode))].sort();
    return [...new Set([...fromBar, ...fromLine])].sort();
}

// ── Contrôles ─────────────────────────────────────────────────────────────────
function bindControls() {
    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            currentView = btn.dataset.view;
            document.querySelectorAll('.view-btn').forEach(b => {
                const active = b.dataset.view === currentView;
                b.classList.toggle('active', active);
                b.classList.toggle('bg-card', active);
                b.classList.toggle('text-primary', active);
                b.classList.toggle('shadow-sm', active);
                b.classList.toggle('text-muted-foreground-2', !active);
            });
            document.getElementById('period-filter')?.classList.toggle('hidden', currentView !== 'line');
            updateChartMeta();
            renderChart();
        });
    });

    document.getElementById('classe-filter')?.addEventListener('change', e => {
        currentClasse = e.target.value;
        renderChart();
    });

    document.getElementById('period-filter')?.addEventListener('change', e => {
        currentPeriod = parseInt(e.target.value, 10);
        renderChart();
    });
}

function updateChartMeta() {
    const title    = document.getElementById('chart-title');
    const subtitle = document.getElementById('chart-subtitle');
    if (currentView === 'bar') {
        if (title)    title.textContent    = 'Absences par classe';
        if (subtitle) subtitle.textContent = 'Total des absences enregistrées par classe';
    } else {
        if (title)    title.textContent    = 'Évolution des absences';
        if (subtitle) subtitle.textContent = 'Nombre d\'absences enregistrées par jour';
    }
}

// ── Rendu principal ───────────────────────────────────────────────────────────
function renderChart() {
    if (activeChart) { activeChart.destroy(); activeChart = null; }
    if (currentView === 'bar') renderBarChart();
    else renderLineChart();
}

// ── Utilitaires CSS ───────────────────────────────────────────────────────────
function getCssVar(name) {
    return getComputedStyle(document.documentElement).getPropertyValue(name).trim();
}

function isDark() {
    return document.documentElement.classList.contains('dark') ||
           document.documentElement.getAttribute('data-theme')?.includes('dark');
}

function tooltipTheme() { return isDark() ? 'dark' : 'light'; }

// Palette de couleurs distinctes pour les séries multi-lignes
const SERIES_COLORS = ['#6366f1','#06b6d4','#f59e0b','#10b981','#f43f5e','#8b5cf6','#ec4899','#14b8a6'];

// ── Graphe en barres ─────────────────────────────────────────────────────────
function renderBarChart() {
    const el = document.getElementById('chart-absences');
    if (!el || !window.ApexCharts) return;

    let labels = [...(window.dashboardData?.absences?.labels ?? [])];
    let data   = [...(window.dashboardData?.absences?.data   ?? [])];

    if (currentClasse !== 'all') {
        const idx = labels.indexOf(currentClasse);
        if (idx >= 0) { labels = [labels[idx]]; data = [data[idx]]; }
        else          { labels = []; data = []; }
    }

    el.innerHTML = '';
    if (!labels.length) {
        el.innerHTML = '<p class="text-sm text-muted-foreground-2">Aucune absence pour cette sélection</p>';
        return;
    }

    const primaryHsl = `hsl(${getCssVar('--primary')})`;
    const gridColor  = `hsl(${getCssVar('--card-line')})`;
    const textColor  = `hsl(${getCssVar('--muted-foreground-2')})`;

    activeChart = new window.ApexCharts(el, {
        chart: { type: 'bar', height: 320, toolbar: { show: false }, fontFamily: 'inherit', background: 'transparent' },
        series: [{ name: 'Absences', data }],
        xaxis: {
            categories: labels,
            labels: { style: { colors: textColor, fontSize: '11px' } },
            axisBorder: { show: false }, axisTicks: { show: false }
        },
        yaxis: {
            labels: { style: { colors: textColor, fontSize: '11px' }, formatter: v => Math.round(v) },
            min: 0
        },
        colors: [primaryHsl],
        plotOptions: {
            bar: { borderRadius: 6, columnWidth: labels.length <= 3 ? '35%' : labels.length <= 6 ? '50%' : '65%', distributed: true }
        },
        dataLabels: {
            enabled: true,
            style: { fontSize: '11px', fontWeight: '700', colors: ['#fff'] },
            formatter: v => v > 0 ? v : ''
        },
        grid: { strokeDashArray: 4, borderColor: gridColor, xaxis: { lines: { show: false } } },
        legend: { show: false },
        tooltip: { y: { formatter: v => v + ' absence(s)' }, theme: tooltipTheme() }
    });
    activeChart.render();
}

// ── Graphe en ligne (évolution) ───────────────────────────────────────────────
function renderLineChart() {
    const el = document.getElementById('chart-absences');
    if (!el || !window.ApexCharts) return;

    let rows = [...(window.dashboardData?.evolution ?? [])];

    // Filtre période
    if (currentPeriod > 0) {
        const cutoff = new Date();
        cutoff.setDate(cutoff.getDate() - currentPeriod);
        const cutoffStr = cutoff.toISOString().slice(0, 10);
        rows = rows.filter(r => r.jour >= cutoffStr);
    }

    // Filtre classe
    if (currentClasse !== 'all') {
        rows = rows.filter(r => r.classeCode === currentClasse);
    }

    el.innerHTML = '';
    if (!rows.length) {
        el.innerHTML = '<p class="text-sm text-muted-foreground-2">Aucune absence sur cette période</p>';
        return;
    }

    // Dates uniques triées (axe X)
    const allDates = [...new Set(rows.map(r => r.jour))].sort();

    // Classes à afficher
    const classes = currentClasse === 'all'
        ? [...new Set(rows.map(r => r.classeCode))].sort()
        : [currentClasse];

    // Construction des séries ApexCharts
    const series = classes.map((cls, i) => ({
        name: cls,
        color: SERIES_COLORS[i % SERIES_COLORS.length],
        data: allDates.map(date => {
            const row = rows.find(r => r.classeCode === cls && r.jour === date);
            return row ? Number(row.nbAbsences) : 0;
        })
    }));

    const gridColor = `hsl(${getCssVar('--card-line')})`;
    const textColor = `hsl(${getCssVar('--muted-foreground-2')})`;

    // Format d'affichage des dates sur l'axe X : "12 mai"
    const months = ['jan','fév','mar','avr','mai','jun','jul','aoû','sep','oct','nov','déc'];
    const fmtDate = iso => {
        const [,m,d] = iso.split('-');
        return parseInt(d) + ' ' + months[parseInt(m) - 1];
    };

    activeChart = new window.ApexCharts(el, {
        chart: {
            type: 'line', height: 320, toolbar: { show: false },
            fontFamily: 'inherit', background: 'transparent',
            animations: { enabled: true, speed: 400 }
        },
        series,
        xaxis: {
            categories: allDates.map(fmtDate),
            labels: {
                style: { colors: textColor, fontSize: '10px' },
                rotate: allDates.length > 20 ? -45 : 0,
                rotateAlways: allDates.length > 20
            },
            axisBorder: { show: false }, axisTicks: { color: gridColor }
        },
        yaxis: {
            labels: { style: { colors: textColor, fontSize: '11px' }, formatter: v => Math.round(v) },
            min: 0
        },
        stroke: { curve: 'smooth', width: 2.5 },
        markers: { size: allDates.length <= 15 ? 4 : 0, hover: { size: 6 } },
        grid: { strokeDashArray: 4, borderColor: gridColor },
        legend: {
            position: 'top', horizontalAlign: 'right',
            fontSize: '11px', labels: { colors: textColor }
        },
        tooltip: {
            shared: true, intersect: false,
            y: { formatter: v => v + ' absence(s)' },
            theme: tooltipTheme()
        }
    });
    activeChart.render();
}

// ── Démarrage ────────────────────────────────────────────────────────────────
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}
