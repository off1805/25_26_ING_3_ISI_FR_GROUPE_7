
/**
 * 
 * @param {String} str Couleur oklab sous forme de chaine de caractere ex oklab(0.5 0.1 0.1) ou oklab(50% 10 10)
 * @returns 
 */
export function oklabStringToRgb(str) {

    if (!str || !str.startsWith("oklab")) return str;
    const values = str.match(/[-\d.]+/g);
    if (!values || values.length < 3) return str;
    let [L, a, b] = values.map(Number);
    if (str.includes("%")) {
        L = L / 100;
    }
    return oklabToRgbString(L, a, b);

}

/**
 * 
 * @param {*} str Couleur oklch sous forme de chaine de caractere ex oklch(0.5 0.1 120) ou oklch(50% 10 120)
 * @returns 
 */
export function oklchStringToRgb(str) {

    if (!str || !str.startsWith("oklch")) return str;
    const values = str.match(/[-\d.]+/g);
    if (!values || values.length < 3) return str;
    let [L, C, h] = values.map(Number);
    if (str.includes("%")) {
        L = L / 100;
    }
    const hRad = (h * Math.PI) / 180;
    const a = C * Math.cos(hRad);
    const b = C * Math.sin(hRad);
    return oklabToRgbString(L, a, b);
}

/**
 * Helper pour convertir oklab en rgb, utilisé par les deux fonctions d'avant
 * @param {*} L 
 * @param {*} a 
 * @param {*} b 
 * @returns 
 */
function oklabToRgbString(L, a, b) {

    const l_ = L + 0.3963377774 * a + 0.2158037573 * b;
    const m_ = L - 0.1055613458 * a - 0.0638541728 * b;
    const s_ = L - 0.0894841775 * a - 1.2914855480 * b;

    const l = l_ ** 3;
    const m = m_ ** 3;
    const s = s_ ** 3;

    let r = +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s;
    let g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s;
    let bVal = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s;

    const gamma = (c) =>
        c <= 0.0031308
            ? 12.92 * c
            : 1.055 * Math.pow(c, 1 / 2.4) - 0.055;

    r = gamma(r);
    g = gamma(g);
    bVal = gamma(bVal);

    const to255 = (x) =>
        Math.max(0, Math.min(255, Math.round(x * 255)));

    return `rgb(${to255(r)}, ${to255(g)}, ${to255(bVal)})`;
}