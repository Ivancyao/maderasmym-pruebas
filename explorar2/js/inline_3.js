
    // Toggle del dropdown al hacer click
    document.querySelectorAll('.has-dropdown > .menu__link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const dropdown = this.nextElementSibling;
            
            // Cerrar otros dropdowns
            document.querySelectorAll('.menu__nesting').forEach(other => {
                if (other !== dropdown) {
                    other.style.display = 'none';
                }
            });
            
            // Toggle del dropdown actual
            if (dropdown.style.display === 'block') {
                dropdown.style.display = 'none';
            } else {
                dropdown.style.display = 'block';
            }
        });
    });

    /* ========================================
       LOGICA PARA MODAL DE DETALLE DE PRODUCTO
       ======================================== */
    // El <div id="product-detail-modal"> está más abajo en el HTML (después de
    // este <script>), así que getElementById aquí mismo devolvería null. Se
    // resuelve recién en DOMContentLoaded; openProductModal/closeProductModal
    // solo se invocan por clic del usuario, momento en que ya está disponible.
    let PRODUCT_MODAL = null;

    async function openProductModal(id) {
        // Buscar el producto en los datos cargados actualmente
        // (allItems o visibles según donde estemos, pero currentProductsData suele tener los visibles)
        // Intentaremos buscar en currentProductsData (usado por el catálogo)
        let p = (typeof currentProductsData !== 'undefined') ? currentProductsData.find(prod => prod.id_producto === Number(id)) : null;
        
        // Si no está ahí (ej. es un destacado no cargado en el grid principal), lo buscamos en el catálogo completo
        if (!p) {
             try {
                 const all = await cargarCatalogo();
                 p = all.find(prod => prod.id_producto === Number(id));
             } catch(e) { console.error("Error buscando producto para modal", e); }
        }

        if (!p) {
            console.error("Producto no encontrado", id);
            return;
        }

        const img = imagenDe(p);
        const precio = Number(p.precio_unidad)||0;

        const modalImg = document.getElementById('modal-product-img');
        const zoomBtn = document.getElementById('modal-product-zoom');
        zoomBtn.classList.toggle('sin-foto', img === PLACEHOLDER);
        modalImg.onerror = () => {
            modalImg.onerror = null;
            modalImg.src = PLACEHOLDER;
            zoomBtn.classList.add('sin-foto');
        };
        modalImg.src = img;
        modalImg.alt = p.nombre_prod || '';
        document.getElementById('modal-product-title').innerText = p.nombre_prod;
        document.getElementById('modal-product-price').innerText = `$${precio.toLocaleString('es-CL')}`;
        document.getElementById('modal-product-type').innerText = p.tipo || '—';
        document.getElementById('modal-product-measures').innerText = p.medidas || '—';
        document.getElementById('modal-product-dims').innerText = p.dimensiones || '—';
        document.getElementById('modal-product-stock').innerText = p.disponibilidad ?? '—';
        document.getElementById('modal-product-description').innerText = p.descripcion || 'Sin descripción disponible por el momento.';

        const actionsDiv = document.getElementById('modal-product-actions');
        const quantityInCart = getProductQuantityInCart(p.id_producto);

        if (quantityInCart > 0) {
            actionsDiv.innerHTML = `
                <div class="quantity-control" style="width: 100%; justify-content: center; background: #f3f4f6; padding: 10px; border-radius: 12px; border: 1px solid #e5e7eb;">
                    <button class="qty-btn-minus" data-action="minus" data-id="${p.id_producto}" style="width:45px; height:45px; font-size: 20px;">−</button>
                    <div class="qty-display" style="font-size: 1.1rem; font-weight: 700;">
                        ${quantityInCart}
                        <span style="font-size: 0.8rem; display: block; font-weight: 400; color: #6b7280;">en carrito</span>
                    </div>
                    <button class="qty-btn-plus" data-action="plus" data-id="${p.id_producto}" style="width:45px; height:45px; font-size: 20px;">+</button>
                </div>
            `;
            
            // Vincular eventos de los botones internos
            actionsDiv.querySelectorAll('button').forEach(btn => {
                btn.onclick = (e) => {
                    e.stopPropagation();
                    const action = btn.dataset.action;
                    const cur = getProductQuantityInCart(p.id_producto);
                    if (action === 'plus') updateProductQuantity(p.id_producto, cur + 1);
                    else updateProductQuantity(p.id_producto, Math.max(0, cur - 1));
                    openProductModal(p.id_producto); // Refrescar modal
                };
            });
        } else {
            // Listener en vez de onclick en línea: un nombre con comillas
            // rompía el atributo y el botón no hacía nada.
            actionsDiv.innerHTML = `
                <button class="cart-action-btn" style="width: 100%; height: 55px; font-size: 1.1rem;">
                    Añadir al carrito
                </button>
            `;
            actionsDiv.querySelector('button').onclick = () => {
                addToCart({ id_producto: p.id_producto, name: p.nombre_prod, precio, Linkimg: img, quantity: 1 });
                openProductModal(p.id_producto);
            };
        }

        PRODUCT_MODAL.classList.add('is-open');
        document.body.style.overflow = 'hidden';
    }

    function closeProductModal() {
        closeLightbox();
        PRODUCT_MODAL.classList.remove('is-open');
        document.body.style.overflow = '';
    }

    /* ========================================
       VISOR DE FOTO AMPLIADA
       Clic en la foto del modal la abre a pantalla completa. Dentro, un clic
       acerca (2,5x) en el punto tocado y otro vuelve a encajarla; con la foto
       acercada se recorre arrastrando o con la rueda/scroll.
       ======================================== */
    const LIGHTBOX_ZOOM = 2.5;
    let LIGHTBOX = null, LIGHTBOX_STAGE = null, LIGHTBOX_IMG = null;

    function openLightbox() {
        const modalImg = document.getElementById('modal-product-img');
        if (!LIGHTBOX || document.getElementById('modal-product-zoom').classList.contains('sin-foto')) return;
        LIGHTBOX_IMG.src = modalImg.currentSrc || modalImg.src;
        LIGHTBOX_IMG.alt = modalImg.alt;
        setLightboxZoom(false);
        LIGHTBOX.classList.add('is-open');
        LIGHTBOX.querySelector('.product-lightbox-close').focus();
    }

    function closeLightbox() {
        if (!LIGHTBOX?.classList.contains('is-open')) return;
        LIGHTBOX.classList.remove('is-open');
        setLightboxZoom(false);
        document.getElementById('modal-product-zoom')?.focus();
    }

    // punto = {x, y} en proporción (0..1) de la foto, para centrar ahí el zoom
    function setLightboxZoom(acercar, punto = { x: 0.5, y: 0.5 }) {
        if (!acercar) {
            LIGHTBOX?.classList.remove('is-zoomed');
            if (LIGHTBOX_IMG) LIGHTBOX_IMG.style.width = '';
            return;
        }
        const anchoEncajado = LIGHTBOX_IMG.getBoundingClientRect().width;
        LIGHTBOX.classList.add('is-zoomed');
        LIGHTBOX_IMG.style.width = `${Math.round(anchoEncajado * LIGHTBOX_ZOOM)}px`;
        LIGHTBOX_STAGE.scrollLeft = punto.x * LIGHTBOX_IMG.offsetWidth - LIGHTBOX_STAGE.clientWidth / 2;
        LIGHTBOX_STAGE.scrollTop  = punto.y * LIGHTBOX_IMG.offsetHeight - LIGHTBOX_STAGE.clientHeight / 2;
    }

    function initLightbox() {
        LIGHTBOX = document.getElementById('product-lightbox');
        LIGHTBOX_STAGE = LIGHTBOX.querySelector('.product-lightbox-stage');
        LIGHTBOX_IMG = document.getElementById('product-lightbox-img');

        document.getElementById('modal-product-zoom').addEventListener('click', openLightbox);
        LIGHTBOX.querySelector('.product-lightbox-close').addEventListener('click', closeLightbox);

        // Arrastrar para recorrer la foto acercada. Si el puntero se movió, el
        // clic que sigue no cambia el zoom.
        let arrastre = null;
        LIGHTBOX_STAGE.addEventListener('pointerdown', (e) => {
            if (!LIGHTBOX.classList.contains('is-zoomed') || e.pointerType !== 'mouse') return;
            arrastre = { x: e.clientX, y: e.clientY, left: LIGHTBOX_STAGE.scrollLeft, top: LIGHTBOX_STAGE.scrollTop, movido: false };
            LIGHTBOX.classList.add('is-panning');
        });
        window.addEventListener('pointermove', (e) => {
            if (!arrastre) return;
            const dx = e.clientX - arrastre.x, dy = e.clientY - arrastre.y;
            if (Math.abs(dx) + Math.abs(dy) > 4) arrastre.movido = true;
            LIGHTBOX_STAGE.scrollLeft = arrastre.left - dx;
            LIGHTBOX_STAGE.scrollTop  = arrastre.top - dy;
        });
        window.addEventListener('pointerup', () => {
            if (!arrastre) return;
            LIGHTBOX.classList.remove('is-panning');
            // El click llega justo después de este pointerup y todavía ve el
            // arrastre (para ignorarse si hubo movimiento); luego se limpia.
            const fin = arrastre;
            setTimeout(() => { if (arrastre === fin) arrastre = null; }, 0);
        });

        LIGHTBOX_STAGE.addEventListener('click', (e) => {
            if (arrastre?.movido) return;
            if (e.target !== LIGHTBOX_IMG) {
                // clic en el fondo oscuro: cierra si la foto está encajada
                if (!LIGHTBOX.classList.contains('is-zoomed')) closeLightbox();
                return;
            }
            const r = LIGHTBOX_IMG.getBoundingClientRect();
            setLightboxZoom(!LIGHTBOX.classList.contains('is-zoomed'), {
                x: (e.clientX - r.left) / r.width,
                y: (e.clientY - r.top) / r.height
            });
        });
    }

    // Cerrar modal al hacer click fuera (se registra cuando el DOM ya tiene el modal)
    document.addEventListener('DOMContentLoaded', () => {
        PRODUCT_MODAL = document.getElementById('product-detail-modal');
        PRODUCT_MODAL.addEventListener('click', (e) => {
            if (e.target === PRODUCT_MODAL) closeProductModal();
        });
        initLightbox();
    });

    // ESC: primero cierra la foto ampliada; si no hay, el modal
    document.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        if (LIGHTBOX?.classList.contains('is-open')) closeLightbox();
        else if (PRODUCT_MODAL?.classList.contains('is-open')) closeProductModal();
    });

    // Cerrar dropdown al hacer click fuera
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.has-dropdown')) {
            document.querySelectorAll('.menu__nesting').forEach(dropdown => {
                dropdown.style.display = 'none';
            });
        }
    });
    