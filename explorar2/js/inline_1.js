
    /* =========================
    UTILIDADES CARRITO (LS)
    ========================= */
    function getCart(){ try{ return JSON.parse(localStorage.getItem('cart')) || []; }catch{ return []; } }
    function saveCart(cart){ localStorage.setItem('cart', JSON.stringify(cart)); }
    function clearCart(){ saveCart([]); updateCartDisplay(); refreshProductCards(); }
    
    // Obtener cantidad de un producto en el carrito
    function getProductQuantityInCart(productId) {
        const cart = getCart();
        const item = cart.find(p => p.id_producto === productId);
        return item ? item.quantity : 0;
    }
    
    // Actualizar cantidad de un producto
    function updateProductQuantity(productId, newQuantity) {
        const cart = getCart();
        const idx = cart.findIndex(p => p.id_producto === productId);
        
        if (newQuantity <= 0) {
            // Eliminar del carrito
            if (idx !== -1) {
                cart.splice(idx, 1);
            }
        } else {
            if (idx !== -1) {
                cart[idx].quantity = newQuantity;
            }
        }
        
        saveCart(cart);
        updateCartDisplay();
        refreshProductCards();
    }
    
    function addToCart(product){
        const cart = getCart();
        const i = cart.findIndex(p => p.id_producto === product.id_producto);
        if (i !== -1) cart[i].quantity += product.quantity || 1;
        else { product.quantity = product.quantity || 1; cart.push(product); }
        saveCart(cart); 
        updateCartDisplay();
        refreshProductCards();
        try{ window.showPopup && showPopup('Producto añadido al carrito'); }catch{}
    }
    
    function updateCartDisplay(){
        const cart = getCart();
        const cartContainer   = document.querySelector('.cart-container');
        const cartTotalEl     = document.getElementById('cart-total');
        const cartCountEl     = document.querySelector('.cart-count');
        const cartCountMobileEl = document.querySelector('.cart-count-mobile'); // Contador móvil
        
        // Elementos del carrito fullscreen (móvil)
        const fsCartContainer = document.querySelector('.cart-items-container');
        const fsCartTotalEl   = document.getElementById('fs-cart-total');
        
        const totalItems = cart.reduce((a,b)=>a + (b.quantity||0), 0);
        if (cartCountEl) cartCountEl.textContent = totalItems;
        if (cartCountMobileEl) cartCountMobileEl.textContent = totalItems;

        let totalPrice = 0;
        cart.forEach((product) => {
            totalPrice += (Number(product.precio)||0) * (Number(product.quantity)||0);
        });

        // ========== Renderizar en modal desktop ==========
        if (cartContainer) {
            cartContainer.innerHTML = '';
            
            if (!cart.length){
                cartContainer.innerHTML = '<p>El carrito está vacío.</p>';
            } else {
                cart.forEach((product, idx) => {
                    const sub = (Number(product.precio)||0) * (Number(product.quantity)||0);
                    const div = document.createElement('div');
                    div.className = 'cart-item';
                    div.innerHTML = `
                    <img src="assets/productos/${product.Linkimg || product.id_producto}.jpg" alt="${product.name}">
                    <div class="cart-item-info">
                        <h4>${product.name}</h4>
                        <p>Precio: $${Number(product.precio).toLocaleString('es-CL')}</p>
                        <p>Subtotal: $${sub.toLocaleString('es-CL')}</p>
                    </div>
                    <div class="cart-quantity">
                        <button data-act="minus" data-idx="${idx}">-</button>
                        <span>${product.quantity}</span>
                        <button data-act="plus" data-idx="${idx}">+</button>
                    </div>
                    <button class="remove-btn" data-act="rm" data-idx="${idx}">Eliminar</button>
                    `;
                    cartContainer.appendChild(div);
                });
                
                cartContainer.onclick = (e)=>{
                    const btn = e.target.closest('button[data-act]');
                    if (!btn) return;
                    const cart = getCart();
                    const idx = Number(btn.dataset.idx);
                    const act = btn.dataset.act;
                    if (act === 'minus' && cart[idx]) cart[idx].quantity = Math.max(1, (cart[idx].quantity||1)-1);
                    if (act === 'plus'  && cart[idx]) cart[idx].quantity = (cart[idx].quantity||1)+1;
                    if (act === 'rm') cart.splice(idx,1);
                    saveCart(cart); updateCartDisplay(); refreshProductCards();
                };
            }
            
            if (cartTotalEl) cartTotalEl.textContent = totalPrice.toLocaleString('es-CL');
        }
        
        // ========== Renderizar en carrito fullscreen (móvil) ==========
        if (fsCartContainer) {
            fsCartContainer.innerHTML = '';
            
            if (!cart.length){
                fsCartContainer.innerHTML = `
                    <div class="cart-empty-state">
                        <i class="fa-solid fa-cart-shopping"></i>
                        <h3>Tu carrito está vacío</h3>
                        <p>Agrega productos para comenzar tu pedido</p>
                    </div>
                `;
            } else {
                cart.forEach((product, idx) => {
                    const sub = (Number(product.precio)||0) * (Number(product.quantity)||0);
                    const div = document.createElement('div');
                    div.className = 'cart-item';
                    div.innerHTML = `
                    <img src="assets/productos/${product.Linkimg || product.id_producto}.jpg" alt="${product.name}" onerror="this.src='assets/placeholder.svg'">
                    <div class="cart-item-info">
                        <h4>${product.name}</h4>
                        <div class="item-price">$${sub.toLocaleString('es-CL')}</div>
                    </div>
                    <div class="cart-quantity">
                        <button data-fsact="minus" data-idx="${idx}">−</button>
                        <span>${product.quantity}</span>
                        <button data-fsact="plus" data-idx="${idx}">+</button>
                    </div>
                    <button class="remove-item-btn" data-fsact="rm" data-idx="${idx}">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                    `;
                    fsCartContainer.appendChild(div);
                });
                
                fsCartContainer.onclick = (e)=>{
                    const btn = e.target.closest('button[data-fsact]');
                    if (!btn) return;
                    const cart = getCart();
                    const idx = Number(btn.dataset.idx);
                    const act = btn.dataset.fsact;
                    if (act === 'minus' && cart[idx]) cart[idx].quantity = Math.max(1, (cart[idx].quantity||1)-1);
                    if (act === 'plus'  && cart[idx]) cart[idx].quantity = (cart[idx].quantity||1)+1;
                    if (act === 'rm') cart.splice(idx,1);
                    saveCart(cart); updateCartDisplay(); refreshProductCards();
                };
            }
            
            if (fsCartTotalEl) fsCartTotalEl.textContent = totalPrice.toLocaleString('es-CL');
        }
    }

    /* =========================
    MODAL CARRITO + OVERLAY + FULLSCREEN
    ========================= */
    const UI = {
    modal:    null,
    fullscreen: null,
    wait:     null,
    genBtn:   null,
    placing:  false,
    
    // Detectar si es móvil
    isMobile() {
        return window.innerWidth <= 768;
    },
    
    // Abrir carrito (detecta si usar modal o fullscreen)
    openCart(e) {
        if(e) e.preventDefault();
        
        if (this.isMobile()) {
            // Móvil: abrir fullscreen
            this.fullscreen?.classList.add('is-open');
            document.body.style.overflow = 'hidden';
        } else {
            // Desktop: abrir modal
            this.modal?.classList.add('is-open');
            document.body.style.overflow = 'hidden';
        }
        updateCartDisplay();
    },
    
    // Cerrar carrito
    closeCart() {
        this.modal?.classList.remove('is-open');
        this.fullscreen?.classList.remove('is-open');
        document.body.style.overflow = '';
    },
    
    // Métodos legacy para compatibilidad
    openModal(e){ this.openCart(e); },
    closeModal(){ this.closeCart(); },
    
    openWait(){ this.wait?.classList.add('is-open'); },
    closeWait(){ this.wait?.classList.remove('is-open'); }
    };

    /* =========================
    ✅ VARIABLES GLOBALES PARA MANTENER EL ESTADO DEL FILTRO
    ========================= */
    let currentQuery = '';
    let currentTipo = '';
    let currentMedida = '';
    let isFilterByTypeAndSize = false;
    let currentProductsData = []; // Guardar datos de productos actuales

    /* =========================
    MOSTRAR SOLO PRODUCTOS CON FOTO
    Mientras se van subiendo las fotos del catálogo, las tarjetas sin imagen
    salen con el placeholder y afean la portada. Ponlo en false para volver a
    mostrar el catálogo completo.
    ========================= */
    const SOLO_CON_FOTO = true;

    // En la tabla conviven cuatro maneras de decir "todavía no tiene foto":
    // cadena vacía, el literal 'pendiente', un 'https://' suelto sin dominio,
    // y el placeholder. Solo cuenta como foto una URL o ruta de imagen real.
    function tieneFoto(p) {
        const ruta = String(p?.ruta ?? '').trim();
        if (!ruta) return false;
        if (/^(pendiente|null|undefined|n\/a|-|s\/f)$/i.test(ruta)) return false;
        if (/^https?:\/\/$/i.test(ruta)) return false;   // 'https://' sin dominio
        if (/placeholder/i.test(ruta)) return false;

        // URL absoluta: necesita dominio y algo después de la barra
        if (/^https?:\/\//i.test(ruta)) return /^https?:\/\/[^\/\s]+\/.+/i.test(ruta);

        // Ruta relativa: tiene que apuntar a un archivo de imagen
        return /\.(jpe?g|png|webp|gif|avif)$/i.test(ruta);
    }

    // Deja solo los que tienen foto. Si no queda ninguno devuelve la lista
    // original: más vale una tarjeta fea que una tienda en blanco.
    function filtrarConFoto(items, etiqueta) {
        if (!SOLO_CON_FOTO) return items;

        const conFoto = items.filter(tieneFoto);
        if (!conFoto.length) {
            console.warn(`[catálogo] ${etiqueta}: ningún producto tiene foto, se muestran todos para no dejar la vista vacía.`);
            return items;
        }
        if (conFoto.length < items.length) {
            console.log(`[catálogo] ${etiqueta}: ${conFoto.length} de ${items.length} productos tienen foto.`);
        }
        return conFoto;
    }

    // Devuelve una imagen usable. Sin esto, un producto con ruta 'pendiente'
    // acaba con src="pendiente" y el navegador pinta un icono roto en vez del
    // placeholder, que es justo lo que se ve mal.
    const PLACEHOLDER = 'assets/placeholder.svg';
    function imagenDe(p) {
        return tieneFoto(p) ? p.ruta : PLACEHOLDER;
    }

    /* =========================
    BÚSQUEDA SIN IMPORTAR TILDES NI PLURALES
    El sidebar busca "Base metalica" y el producto se llama "Base Metálica";
    busca "Planchas Osb" y el tipo es "Plancha". Comparando el texto tal cual
    esas categorías salían vacías.
    ========================= */
    // Minúsculas, sin tildes y con coma decimal pasada a punto ("3,20" = "3.20").
    function normalizarTexto(s) {
        return String(s ?? '')
            .normalize('NFD').replace(/[̀-ͯ]/g, '')
            .toLowerCase()
            .replace(/(\d),(\d)/g, '$1.$2')
            .replace(/\s+/g, ' ')
            .trim();
    }

    // Una palabra buscada también vale en singular: "planchas" → "plancha",
    // "protectores" → "protector", "polines" → "polin".
    function contienePalabra(texto, palabra) {
        if (texto.includes(palabra)) return true;
        const singular = palabra.replace(/(es|s)$/, '');
        return singular !== palabra && singular.length >= 3 && texto.includes(singular);
    }

    // q ya viene normalizado
    function coincideBusqueda(p, q) {
        const tipo = normalizarTexto(p.tipo);
        const nombre = normalizarTexto(p.nombre_prod);

        // Tipos principales: "Bruto" y "Cepillado" filtran por tipo
        if (q === 'bruto' || q === 'cepillado') return tipo.includes(q);
        if (q.includes('bruto')) return tipo.includes('bruto') || nombre.includes('bruto');
        if (q.includes('cepillado')) return tipo.includes('cepillado') || nombre.includes('cepillado');

        // Búsqueda general: cada palabra tiene que aparecer en algún campo
        // (el precio también, como hacía el filtro del servidor)
        const campos = [nombre, tipo, p.medidas, p.dimensiones, p.categoria, p.precio_unidad]
            .map(normalizarTexto).join(' ');
        return q.split(' ').every(palabra => contienePalabra(campos, palabra));
    }

    // Para la búsqueda: no esconde nada (el cliente busca algo concreto y debe
    // encontrarlo), pero pone delante los que sí tienen foto.
    function fotosPrimero(items) {
        return [...items].sort((a, b) => (tieneFoto(b) ? 1 : 0) - (tieneFoto(a) ? 1 : 0));
    }

    /* =========================
    REFRESCAR TARJETAS DE PRODUCTOS (actualizar botones)
    ========================= */
    function refreshProductCards() {
        // Actualizar todas las tarjetas de productos visibles
        document.querySelectorAll('.product-card .actions, .featured-card .actions').forEach(actionsDiv => {
            const productId = Number(actionsDiv.dataset.productId);
            if (!productId) return;
            
            const quantity = getProductQuantityInCart(productId);
            const productData = actionsDiv.dataset;
            
            if (quantity > 0) {
                // Mostrar controles de cantidad
                actionsDiv.innerHTML = `
                    <div class="quantity-control">
                        <button class="qty-btn-remove" data-action="remove" data-id="${productId}" title="Eliminar">
                            <i class="fas fa-trash"></i>
                        </button>
                        <button class="qty-btn-minus" data-action="minus" data-id="${productId}">−</button>
                        <div class="qty-display">
                            ${quantity}
                            <span>en carrito</span>
                        </div>
                        <button class="qty-btn-plus" data-action="plus" data-id="${productId}">+</button>
                    </div>
                `;
            } else {
                // Mostrar botón de agregar
                actionsDiv.innerHTML = `
                    <button class="add-btn"
                        data-id="${productData.productId}"
                        data-name="${productData.productName}"
                        data-price="${productData.productPrice}"
                        data-img="${productData.productImg}">
                        Agregar al carrito
                    </button>
                `;
            }
        });
        
        // Re-bind eventos
        bindProductCardEvents();
    }
    
    // Bind de eventos para tarjetas de productos
    function bindProductCardEvents() {
        // Botones de agregar
        document.querySelectorAll('.add-btn').forEach(btn => {
            btn.onclick = () => {
                addToCart({
                    id_producto: Number(btn.dataset.id),
                    name: btn.dataset.name,
                    precio: Number(btn.dataset.price) || 0,
                    Linkimg: btn.dataset.img,
                    quantity: 1
                });
            };
        });
        
        // Controles de cantidad
        document.querySelectorAll('.quantity-control button').forEach(btn => {
            btn.onclick = (e) => {
                e.stopPropagation();
                const action = btn.dataset.action;
                const productId = Number(btn.dataset.id);
                const currentQty = getProductQuantityInCart(productId);
                
                if (action === 'plus') {
                    updateProductQuantity(productId, currentQty + 1);
                } else if (action === 'minus') {
                    updateProductQuantity(productId, Math.max(0, currentQty - 1));
                } else if (action === 'remove') {
                    updateProductQuantity(productId, 0);
                }
            };
        });
    }
    
    /* =========================
    MARCAR CATEGORÍA ACTIVA EN SIDEBAR
    ========================= */
    function setActiveCategory(element) {
        // Remover clase activa de todos
        document.querySelectorAll('.sidebar a').forEach(a => a.classList.remove('active-category'));
        // Agregar clase activa al elemento actual
        if (element) {
            element.classList.add('active-category');
        }
    }

    /* =========================
    CATÁLOGO COMPLETO (se descarga una vez por visita)
    El sidebar, el buscador y los destacados filtran en el navegador sobre el
    catálogo entero. Se pedía /productos?limit=10000, pero el servidor entrega
    como máximo 100 por página (tope de seguridad) y lo recortaba en silencio:
    el sidebar solo veía los 100 productos más nuevos y la mayoría de las
    categorías y medidas salían vacías. Aquí se recorren todas las páginas.
    ========================= */
    const CATALOGO_POR_PAGINA = 100; // máximo que acepta GET /productos
    let catalogoPromesa = null;

    async function pedirPaginaCatalogo(pagina) {
        const url = new URL('/productos', window.location.origin);
        url.searchParams.set('limit', CATALOGO_POR_PAGINA);
        url.searchParams.set('page', pagina);
        const res = await fetch(url);
        if (!res.ok) throw new Error(`GET /productos respondió ${res.status}`);
        return res.json();
    }

    function cargarCatalogo() {
        if (!catalogoPromesa) {
            catalogoPromesa = (async () => {
                const primera = await pedirPaginaCatalogo(1);
                const totalPaginas = Math.min(Number(primera.totalPages) || 1, 50);
                const resto = await Promise.all(
                    Array.from({ length: totalPaginas - 1 }, (_, i) => pedirPaginaCatalogo(i + 2))
                );
                return [primera, ...resto].flatMap(d => Array.isArray(d) ? d : (d.productos || []));
            })().catch(err => { catalogoPromesa = null; throw err; }); // reintenta en el próximo uso
        }
        return catalogoPromesa;
    }

    const esVisible = p => p.visible === 1 || p.visible === true || p.visible === '1';

    /* =========================
    CATALOGO + DESTACADOS
    ========================= */
   async function loadProductsPage(page = 1, q = '') {
    // ✅ Guardar el estado del filtro
    currentQuery = q;
    currentTipo = '';
    currentMedida = '';
    isFilterByTypeAndSize = false;
    
  const GRID  = document.getElementById('catalog-grid');
  const PAGER = document.getElementById('pager');
  const PAGE_SIZE = 15;

  try {
    // 1) Catálogo completo; la búsqueda y la paginación se hacen en el cliente
    const allItems = await cargarCatalogo();

    // 2) Filtramos solo visibles (1/true/'1')
    let visibles = allItems.filter(esVisible);

    // 2a) Sin búsqueda es la portada: solo productos con foto.
    if (!q) {
      visibles = filtrarConFoto(visibles, 'catálogo');
    }

    // 2b) Filtramos por búsqueda - ALGORITMO MEJORADO
    if (q) {
        // "a|b" busca a O b: lo usa el sidebar para "Protectores|Barniz"
        const alternativas = q.split('|').map(normalizarTexto).filter(Boolean);
        console.log('🔍 Filtrando productos con query:', alternativas.join(' | '));

        visibles = visibles.filter(p => alternativas.some(alt => coincideBusqueda(p, alt)));

        console.log('✅ Productos encontrados:', visibles.length);
    }
    
    // 2c) En una búsqueda no se esconde nada: el cliente busca algo concreto y
    //     tiene que encontrarlo aunque aún no tenga foto. Solo se reordena.
    if (q) {
      visibles = fotosPrimero(visibles);
    }

    // 3) Paginación cliente solo con visibles
    const totalPages = Math.max(1, Math.ceil(visibles.length / PAGE_SIZE));
    const safePage = Math.min(Math.max(1, page), totalPages);
    const start = (safePage - 1) * PAGE_SIZE;
    const pageItems = visibles.slice(start, start + PAGE_SIZE);

    // 4) Render
    renderGrid(pageItems);
    renderPager(safePage, totalPages, q);

  } catch (err) {
    console.error(err);
    GRID.innerHTML = '<p>Error cargando productos.</p>';
    PAGER.innerHTML = '';
  }
}

function renderGrid(items){
  const GRID = document.getElementById('catalog-grid');
  if (!items?.length){ GRID.innerHTML = '<p>No hay productos para mostrar.</p>'; return; }
  
  currentProductsData = items; // Guardar para refresh
  
  GRID.innerHTML = items.map(p => {
      const img = imagenDe(p);
      const precio = Number(p.precio_unidad)||0;
      const productId = p.id_producto;
      const quantityInCart = getProductQuantityInCart(productId);
      
      // Decidir qué mostrar: botón o controles de cantidad
      let actionsHTML;
      if (quantityInCart > 0) {
          actionsHTML = `
              <div class="quantity-control">
                  <button class="qty-btn-remove" data-action="remove" data-id="${productId}" title="Eliminar">
                      <i class="fas fa-trash"></i>
                  </button>
                  <button class="qty-btn-minus" data-action="minus" data-id="${productId}">−</button>
                  <div class="qty-display">
                      ${quantityInCart}
                      <span>en carrito</span>
                  </div>
                  <button class="qty-btn-plus" data-action="plus" data-id="${productId}">+</button>
              </div>
          `;
      } else {
          actionsHTML = `
              <button class="add-btn"
                  data-id="${productId}"
                  data-name="${p.nombre_prod}"
                  data-price="${precio}"
                  data-img="${p.imagen_url || productId}">
                  Agregar al carrito
              </button>
          `;
      }
      
      return `
      <article class="product-card" onclick="openProductModal(${productId})">
        <div class="thumb"><img src="${img}" alt="${p.nombre_prod}" loading="lazy"
             onerror="this.onerror=null;this.src='assets/placeholder.svg';"></div>
        <div class="info">
          <h4 class="title" title="${p.nombre_prod}">${p.nombre_prod}</h4>
          <div class="price">$${precio.toLocaleString('es-CL')}</div>
          <div class="meta">
            <div><strong>Tipo:</strong> ${p.tipo || '—'}</div>
            <div><strong>Largo:</strong> ${p.medidas || '—'}</div>
            <div><strong>Dimensiones:</strong> ${p.dimensiones || '—'}</div>
            <div><strong>Stock:</strong> ${p.disponibilidad ?? '—'}</div>
          </div>
          <div class="actions" 
               onclick="event.stopPropagation()"
               data-product-id="${productId}"
               data-product-name="${p.nombre_prod}"
               data-product-price="${precio}"
               data-product-img="${p.imagen_url || productId}">
            ${actionsHTML}
          </div>
        </div>
      </article>`;
  }).join('');

  // Bind eventos
  bindProductCardEvents();
}

function renderPager(page, totalPages, q){
  const PAGER = document.getElementById('pager');
  if (totalPages <= 1){ PAGER.innerHTML=''; return; }

  const btn = (label, goto, disabled=false, active=false)=>`
    <button ${disabled?'disabled':''} class="${active?'active':''}" data-goto="${goto}" type="button">${label}</button>
  `;

  const parts = [];
  parts.push(btn('« Anterior', Math.max(1, page-1), page===1));
  const W = 2, start = Math.max(1, page-W), end = Math.min(totalPages, page+W);
  if (start>1){ parts.push(btn('1',1,false,page===1)); if(start>2) parts.push('<span>…</span>'); }
  for(let p=start;p<=end;p++) parts.push(btn(String(p), p, false, p===page));
  if (end<totalPages){ if(end<totalPages-1) parts.push('<span>…</span>'); parts.push(btn(String(totalPages), totalPages, false, page===totalPages)); }
  parts.push(btn('Siguiente »', Math.min(totalPages, page+1), page===totalPages));

  PAGER.innerHTML = parts.join('');
  PAGER.querySelectorAll('button[data-goto]').forEach(b=>{
    b.addEventListener('click', ()=> loadProductsPage(Number(b.dataset.goto), q));
  });
}

async function loadFeatured(){
  const WRAP = document.querySelector('.featured-container');
  try{
    const productos = await cargarCatalogo();
    const visibles = filtrarConFoto(productos.filter(esVisible), 'destacados');
    const items = visibles.slice(0, 5); // mostramos hasta 5

    WRAP.innerHTML = items.map(p => {
      const img = imagenDe(p);
      const precio = Number(p.precio_unidad)||0;
      const productId = p.id_producto;
      const quantityInCart = getProductQuantityInCart(productId);
      
      // Decidir qué mostrar: botón o controles de cantidad
      let actionsHTML;
      if (quantityInCart > 0) {
          actionsHTML = `
              <div class="quantity-control">
                  <button class="qty-btn-remove" data-action="remove" data-id="${productId}" title="Eliminar">
                      <i class="fas fa-trash"></i>
                  </button>
                  <button class="qty-btn-minus" data-action="minus" data-id="${productId}">−</button>
                  <div class="qty-display">
                      ${quantityInCart}
                      <span>en carrito</span>
                  </div>
                  <button class="qty-btn-plus" data-action="plus" data-id="${productId}">+</button>
              </div>
          `;
      } else {
          actionsHTML = `
              <button class="add-btn"
                  data-id="${productId}"
                  data-name="${p.nombre_prod}"
                  data-price="${precio}"
                  data-img="${p.imagen_url || productId}">
                  Agregar al carrito
              </button>
          `;
      }
      
      return `
      <article class="featured-card" onclick="openProductModal(${productId})">
        <div class="thumb"><img src="${img}" alt="${p.nombre_prod}" loading="lazy"
             onerror="this.onerror=null;this.src='assets/placeholder.svg';"></div>
        <div class="info">
          <h4 class="title" title="${p.nombre_prod}">${p.nombre_prod}</h4>
          <div class="price">$${precio.toLocaleString('es-CL')}</div>
          <div class="meta">
            <div><strong>Tipo:</strong> ${p.tipo || '—'}</div>
            <div><strong>Largo:</strong> ${p.medidas || '—'}</div>
            <div><strong>Dimensiones:</strong> ${p.dimensiones || '—'}</div>
            <div><strong>Stock:</strong> ${p.disponibilidad ?? '—'}</div>
          </div>
          <div class="actions"
               data-product-id="${productId}"
               data-product-name="${p.nombre_prod}"
               data-product-price="${precio}"
               data-product-img="${p.imagen_url || productId}">
            ${actionsHTML}
          </div>
        </div>
      </article>`;
    }).join('');

    // Bind eventos
    bindProductCardEvents();

  } catch(e){
    console.error(e);
    WRAP.innerHTML = '<p>Error cargando destacados.</p>';
  }
}

/* =========================
   FUNCIÓN ESPECÍFICA PARA FILTRAR POR TIPO Y MEDIDA
   ========================= */
async function loadProductsPageByTypeAndSize(page = 1, tipo = '', medida = '') {
  // ✅ Guardar el estado del filtro
  currentQuery = '';
  currentTipo = tipo;
  currentMedida = medida;
  isFilterByTypeAndSize = true;
  
  const GRID  = document.getElementById('catalog-grid');
  const PAGER = document.getElementById('pager');
  const PAGE_SIZE = 18;

  console.log('=== FILTRANDO POR TIPO Y MEDIDA ===');
  console.log('Tipo:', tipo);
  console.log('Medida:', medida);

  try {
    // Traer todos los productos
    const allItems = await cargarCatalogo();

    // Solo visibles. Elegir una medida es una búsqueda concreta, igual que
    // pinchar la categoría: no se esconden los que aún no tienen foto (más
    // abajo se ponen al final), si no la medida aparecía vacía.
    let visibles = allItems.filter(esVisible);

    console.log('Total productos visibles:', visibles.length);

    // Filtrar por tipo (Bruto/Cepillado)
    if (tipo) {
      const tipoBuscado = normalizarTexto(tipo);
      visibles = visibles.filter(p => normalizarTexto(p.tipo).includes(tipoBuscado));
      console.log('Después de filtrar por tipo "' + tipo + '":', visibles.length);
    }

    // Filtrar por medida
    if (medida) {
      // normalizarTexto pasa "3,20" a "3.20": hay productos cargados con coma
      const medidaBuscada = normalizarTexto(medida);
      visibles = visibles.filter(p =>
        [p.medidas, p.dimensiones, p.nombre_prod].some(c => normalizarTexto(c).includes(medidaBuscada))
      );
      console.log('Después de filtrar por medida "' + medida + '":', visibles.length);
    }

    // Mostrar productos encontrados en consola para debug
    if (visibles.length > 0) {
      console.log('Productos encontrados:');
      visibles.slice(0, 5).forEach(p => {
        console.log('- ', p.nombre_prod, '| Tipo:', p.tipo, '| Medidas:', p.medidas);
      });
    } else {
      console.log('❌ No se encontraron productos con estos filtros');
    }

    // Los que tienen foto primero
    visibles = fotosPrimero(visibles);

    // Paginación
    const totalPages = Math.max(1, Math.ceil(visibles.length / PAGE_SIZE));
    const safePage = Math.min(Math.max(1, page), totalPages);
    const start = (safePage - 1) * PAGE_SIZE;
    const pageItems = visibles.slice(start, start + PAGE_SIZE);

    // Render
    renderGrid(pageItems);
    // ✅ CORREGIDO: Usar nueva función que mantiene tipo y medida
    renderPagerByTypeAndSize(safePage, totalPages, tipo, medida);

  } catch (err) {
    console.error(err);
    GRID.innerHTML = '<p>Error cargando productos.</p>';
    PAGER.innerHTML = '';
  }
}

/* =========================
   ✅ NUEVA FUNCIÓN: renderPagerByTypeAndSize
   Mantiene el filtro de tipo y medida en la paginación
   ========================= */
function renderPagerByTypeAndSize(page, totalPages, tipo, medida){
  const PAGER = document.getElementById('pager');
  if (totalPages <= 1){ PAGER.innerHTML=''; return; }

  const btn = (label, goto, disabled=false, active=false)=>`
    <button ${disabled?'disabled':''} class="${active?'active':''}" data-goto="${goto}" type="button">${label}</button>
  `;

  const parts = [];
  parts.push(btn('« Anterior', Math.max(1, page-1), page===1));
  const W = 2, start = Math.max(1, page-W), end = Math.min(totalPages, page+W);
  if (start>1){ parts.push(btn('1',1,false,page===1)); if(start>2) parts.push('<span>…</span>'); }
  for(let p=start;p<=end;p++) parts.push(btn(String(p), p, false, p===page));
  if (end<totalPages){ if(end<totalPages-1) parts.push('<span>…</span>'); parts.push(btn(String(totalPages), totalPages, false, page===totalPages)); }
  parts.push(btn('Siguiente »', Math.min(totalPages, page+1), page===totalPages));

  PAGER.innerHTML = parts.join('');
  // ✅ CORREGIDO: Pasar tipo y medida a la función
  PAGER.querySelectorAll('button[data-goto]').forEach(b=>{
    b.addEventListener('click', ()=> loadProductsPageByTypeAndSize(Number(b.dataset.goto), tipo, medida));
  });
}

    /* =========================
    GENERAR PEDIDO (con retry si deadlock)
    ========================= */
    // Función para generar pedido con parámetros (usado desde fullscreen móvil)
    async function generateOrderWithParams(delivery, comentarios){
        const cart = getCart();
        if (!cart.length){ alert('Tu carrito está vacío.'); return; }

        // payload que espera el backend
        const payload = cart.map(p => ({
            id_producto: p.id_producto,
            precio: Number(p.precio ?? p.precio_unidad ?? 0),
            quantity: Number(p.quantity ?? 1)
        }));

        const attempts = 3;
        for (let i=1; i<=attempts; i++){
            try{
            const res = await fetch('/api/generar-pedido', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ cart: payload, delivery, comentarios })
            });

            const data = await res.json();
            if (!res.ok){
                // Si no está logueado, redirigir al login con mensaje claro
                if (res.status === 401) {
                    if (confirm('Debes iniciar sesión para generar un pedido. ¿Ir al login?')) {
                        location.href = '/login';
                    }
                    return;
                }
                const msg = data.error || 'Error generando pedido';
                if (i<attempts && msg.toLowerCase().includes('deadlock')){
                await new Promise(r=>setTimeout(r,300));
                continue;
                }
                throw new Error(msg);
            }

            clearCart(); updateCartDisplay();
            UI.closeCart();
            alert(data.message || 'Pedido generado exitosamente. ¡Gracias!');
            return;
            }catch(err){
            if (i === attempts){
                console.error('Error generando pedido:', err);
                alert('No se pudo generar el pedido: ' + (err?.message || 'Inténtalo nuevamente.'));
            }
            }
        }
    }

    async function generateOrder(){
    const cart = getCart();
    if (!cart.length){ alert('Tu carrito está vacío.'); return; }

    const delivery    = document.querySelector('input[name="delivery"]:checked')?.value || 'envio';
    const comentarios = document.getElementById('order-comments')?.value?.trim() || '';

    // payload que espera el backend
    const payload = cart.map(p => ({
        id_producto: p.id_producto,
        precio: Number(p.precio ?? p.precio_unidad ?? 0),
        quantity: Number(p.quantity ?? 1)
    }));

    const attempts = 3;
    for (let i=1; i<=attempts; i++){
        try{
        const res = await fetch('/api/generar-pedido', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ cart: payload, delivery, comentarios })
        });

        const data = await res.json();
        if (!res.ok){
            // Si no está logueado, redirigir al login con mensaje claro
            if (res.status === 401) {
                if (confirm('Debes iniciar sesión para generar un pedido.\n¿Ir al login?')) {
                    location.href = '/login';
                }
                return;
            }
            const msg = data.error || 'Error generando pedido';
            if (i<attempts && msg.toLowerCase().includes('deadlock')){
            await new Promise(r=>setTimeout(r,300));
            continue;
            }
            throw new Error(msg);
        }

        clearCart(); updateCartDisplay();
        UI.closeCart();
        alert(data.message || 'Pedido generado exitosamente. ¡Gracias!');
        return;
        }catch(err){
        if (i === attempts){
            console.error('Error generando pedido:', err);
            alert('No se pudo generar el pedido: ' + (err?.message || 'Inténtalo nuevamente.'));
        }
        }
    }
    }

    /* =========================
    ARRANQUE
    ========================= */
    document.addEventListener('DOMContentLoaded', ()=>{
    // Referencias UI
    UI.modal  = document.getElementById('cartModal');
    UI.fullscreen = document.getElementById('cartFullscreen'); // Carrito fullscreen móvil
    UI.wait   = document.getElementById('order-wait');
    UI.genBtn = document.getElementById('generateOrder');

    // Abrir/Cerrar carrito (desktop y móvil)
    const cartLink   = document.getElementById('cart-link');
    const mobileCartLink = document.getElementById('mobile-cart-link'); // Carrito móvil
    const closeBtn   = UI.modal?.querySelector('.close');
    const backBtn    = document.getElementById('cart-back-btn'); // Botón volver en fullscreen
    
    cartLink?.addEventListener('click', (e)=> UI.openCart(e));
    mobileCartLink?.addEventListener('click', (e)=> UI.openCart(e));
    closeBtn?.addEventListener('click', ()=> UI.closeCart());
    backBtn?.addEventListener('click', ()=> UI.closeCart()); // Cerrar fullscreen con botón volver
    
    UI.modal?.addEventListener('click', (e)=>{ if (e.target === UI.modal) UI.closeCart(); });
    document.addEventListener('keydown', (e)=>{ 
        if (e.key==='Escape') {
            if (UI.modal?.classList.contains('is-open') || UI.fullscreen?.classList.contains('is-open')) {
                UI.closeCart();
            }
        }
    });

    // Botones del carrito (modal desktop)
    const clearBtn = document.getElementById('clear-cart-btn');
    clearBtn?.addEventListener('click', (e)=>{ e.preventDefault(); clearCart(); });
    
    // Botones del carrito fullscreen (móvil)
    const fsClearBtn = document.getElementById('fs-clear-cart');
    const fsGenBtn = document.getElementById('fs-generate-order');
    
    fsClearBtn?.addEventListener('click', (e)=>{ 
        e.preventDefault(); 
        clearCart(); 
    });
    
    // Generar pedido desde fullscreen móvil
    fsGenBtn?.addEventListener('click', async (e)=>{
        e.preventDefault();
        if (UI.placing) return;
        UI.placing = true;
        
        const original = fsGenBtn.innerHTML;
        fsGenBtn.disabled = true;
        fsGenBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Generando…';
        UI.openWait();
        
        try { 
            // Obtener valores del fullscreen
            const delivery = document.querySelector('input[name="fs-delivery"]:checked')?.value || 'envio';
            const comentarios = document.getElementById('fs-order-comments')?.value?.trim() || '';
            
            await generateOrderWithParams(delivery, comentarios);
        }
        finally {
            UI.closeWait();
            fsGenBtn.disabled = false;
            fsGenBtn.innerHTML = original;
            UI.placing = false;
        }
    });

    // ÚNICO listener del botón "Generar pedido" (modal desktop)
    if (UI.genBtn && !UI.genBtn.dataset.bound){
        UI.genBtn.dataset.bound = '1';
        UI.genBtn.addEventListener('click', async (e)=>{
        e.preventDefault();
        if (UI.placing) return;
        UI.placing = true;

        const original = UI.genBtn.textContent;
        UI.genBtn.disabled = true;
        UI.genBtn.textContent = 'Generando…';
        UI.openWait();

        try{ await generateOrder(); }
        finally{
            UI.closeWait();
            UI.genBtn.disabled = false;
            UI.genBtn.textContent = original;
            UI.placing = false;
        }
        });
    }

    // Búsqueda
    const FORM  = document.getElementById('search-form');
    const INPUT = document.getElementById('search-input');
    FORM?.addEventListener('submit', (e)=>{
        e.preventDefault(); 
        setActiveCategory(null); // Limpiar categoría activa
        loadProductsPage(1, (INPUT?.value||'').trim());
    });

    /* =========================
       ✅ SIDEBAR - CATEGORÍAS CON SUBMENÚ (Bruto, Cepillado, Terciados)
       ========================= */
    const toggles = document.querySelectorAll('.toggle-submenu');
    toggles.forEach(toggle => {
        toggle.addEventListener('click', (e) => {
            e.preventDefault();
            const submenu = toggle.nextElementSibling;
            
            // Cerrar otros submenús abiertos
            document.querySelectorAll('.submenu').forEach(sub => {
                if (sub !== submenu) {
                    sub.style.display = 'none';
                }
            });
            
            // Toggle del submenú actual
            if (submenu && submenu.style.display === 'block') {
                submenu.style.display = 'none';
            } else if (submenu) {
                submenu.style.display = 'block';
            }
            
            // Filtrar por la categoría principal
            const categoria = toggle.dataset.category;
            if (categoria) {
                console.log('📂 Filtrando por categoría principal:', categoria);
                setActiveCategory(toggle);
                loadProductsPage(1, categoria);
            }
        });
    });

    /* =========================
       ✅ SIDEBAR - CATEGORÍAS SIMPLES (sin submenú)
       ========================= */
    const simpleCategories = document.querySelectorAll('.simple-category');
    console.log('📋 Categorías simples encontradas:', simpleCategories.length);
    
    simpleCategories.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation(); // Evitar que suba al padre
            
            const searchQuery = link.dataset.search;
            console.log('🔍 Click en categoría simple:', searchQuery);
            
            if (searchQuery) {
                setActiveCategory(link);
                
                // Hacer scroll hacia los productos
                const productosArea = document.querySelector('.conteiner');
                if (productosArea) {
                    productosArea.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
                
                loadProductsPage(1, searchQuery);
            }
        });
    });
    
    /* =========================
       ✅ SIDEBAR - SUBCATEGORÍAS SIMPLES (ej: Terciado estructural)
       ========================= */
    const simpleSubcategories = document.querySelectorAll('.simple-subcategory');
    console.log('📋 Subcategorías simples encontradas:', simpleSubcategories.length);
    
    simpleSubcategories.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            const searchQuery = link.dataset.search;
            console.log('🔍 Click en subcategoría simple:', searchQuery);
            
            if (searchQuery) {
                setActiveCategory(link);
                loadProductsPage(1, searchQuery);
            }
        });
    });

    /* =========================
       ✅ SIDEBAR - SUBMENÚS DE MEDIDAS (Bruto/Cepillado + metros)
       ========================= */
    const medidaLinks = document.querySelectorAll('.medida-link');
    console.log('📏 Enlaces de medidas encontrados:', medidaLinks.length);
    
    medidaLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            // Obtener la medida del data attribute
            const medida = link.dataset.medida;
            
            // Obtener el tipo del padre (submenu)
            const parentSubmenu = link.closest('.submenu');
            const tipoProducto = parentSubmenu ? parentSubmenu.dataset.parentType : '';
            
            console.log('📏 Filtro por medida:');
            console.log('   - Tipo:', tipoProducto);
            console.log('   - Medida:', medida);
            
            if (tipoProducto && medida) {
                setActiveCategory(link);
                loadProductsPageByTypeAndSize(1, tipoProducto, medida);
            }
        });
    });

    /* =========================
       ✅ NAVBAR DROPDOWN - Productos
       ========================= */
    const navbarDropdownLinks = document.querySelectorAll('.menu__nesting .menu__inside .menu__link--inside');
    console.log('🔗 Enlaces del navbar encontrados:', navbarDropdownLinks.length);

    navbarDropdownLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            const texto = link.textContent.trim();
            console.log('🔗 Click en navbar dropdown:', texto);
            
            let query = '';
            
            // Determinar qué buscar según el texto del enlace
            if (texto.includes('Pino Oregón Bruto')) {
                query = 'Bruto';
            } else if (texto.includes('Pino Oregón Cepillado')) {
                query = 'Cepillado';
            } else if (texto.includes('Placas metalicas')) {
                query = 'Placas metalicas';
            } else if (texto.includes('Base')) {
                query = 'Base';
            } else {
                query = texto;
            }
            
            console.log('   Query:', query);
            
            // Cerrar el menú después de hacer click
            document.querySelectorAll('.menu__nesting').forEach(dropdown => {
                dropdown.style.display = 'none';
            });
            
            // Hacer scroll hacia los productos
            const productosArea = document.querySelector('.conteiner');
            if (productosArea) {
                productosArea.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
            
            // Ejecutar la búsqueda
            loadProductsPage(1, query);
        });
    });

    // Cargas iniciales
    loadProductsPage(1);
    loadFeatured();
    updateCartDisplay();
    });
    