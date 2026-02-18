 const shell = document.getElementById('appShell') || document.querySelector('.app-shell');
  const btn = document.getElementById('sidebarToggle');
  const mobileBtn = document.getElementById('sidebarToggleMobile');
  const backdrop = document.getElementById('sidebarBackdrop');

  function isMobile() {
    return window.matchMedia('(max-width: 992px)').matches;
  }

  function closeMobileDrawer() {
    shell.classList.remove('sidebar-open');
  }

  btn?.addEventListener('click', () => {
    if (!isMobile())
      shell.classList.toggle('sidebar-collapsed');
  });

  mobileBtn?.addEventListener('click', () => {
    if (isMobile()) shell.classList.toggle('sidebar-open');
  });

  backdrop?.addEventListener('click', closeMobileDrawer);

  window.addEventListener('resize', () => {
    if (!isMobile()) closeMobileDrawer();
  });