document.getElementById('togglePwd')?.addEventListener('click', () => {
    const input = document.getElementById('password');
    const icon  = document.getElementById('eyeIcon');
    if (!input || !icon) return;
    const show = input.type === 'password';
    input.type = show ? 'text' : 'password';
    icon.innerHTML = show
        ? `<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
           <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
           <line x1="1" x2="23" y1="1" y2="23"/>`
        : `<path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/>
           <circle cx="12" cy="12" r="3"/>`;
});

export function showError(msg) {
    const box  = document.getElementById('otherError');
    const text = document.getElementById('otherErrorText');
    if (text) text.textContent = msg;
    box?.classList.remove('hidden');
    box?.classList.add('flex');
}

export function hideError() {
    const box = document.getElementById('otherError');
    box?.classList.add('hidden');
    box?.classList.remove('flex');
}

export function setLoading(v) {
    const btn     = document.getElementById('submitBtn');
    const text    = document.getElementById('submitText');
    const spinner = document.getElementById('submitSpinner');
    if (btn)     btn.disabled = v;
    if (text)    text.textContent = v ? 'Connexion…' : 'Se connecter';
    spinner?.classList.toggle('hidden', !v);
}
