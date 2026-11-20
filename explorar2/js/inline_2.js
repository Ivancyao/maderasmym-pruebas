
    (function(){
    const now  = new Date();
    const y    = now.getFullYear();
    const startShow = new Date(`${y}-09-01T00:00:00`);
    const endShow   = new Date(`${y}-09-20T23:59:59`);
    const KEY = `holiday_fiestas_patrias_${y}`;

    // Solo mostrar entre el 1 y el 20 de septiembre y si el usuario no lo desactivó
    if (now >= startShow && now <= endShow && localStorage.getItem(KEY) !== 'hide') {
        const root = document.getElementById('holiday-pop');
        const ok   = document.getElementById('holiday-ok');
        const more = document.getElementById('holiday-more');
        const x    = document.getElementById('holiday-x');
        const dont = document.getElementById('holiday-dontshow');

        const close = (remember=false) => {
        if (remember || dont.checked) localStorage.setItem(KEY, 'hide');
        root.classList.remove('is-open');
        // limpia del DOM para no dejar listeners colgando
        setTimeout(()=> root.remove(), 200);
        };

        ok.addEventListener('click', ()=> close(true));
        more.addEventListener('click', ()=> close(false));
        x.addEventListener('click', ()=> close(false));
        // cerrar al click fuera del cuadro
        root.addEventListener('click', (e)=> { if (e.target === root) close(false); });
        // cerrar con ESC
        document.addEventListener('keydown', (e)=> { if (e.key === 'Escape') close(false); });

        // ¡Mostrar!
        root.classList.add('is-open');
    }
    })();
    