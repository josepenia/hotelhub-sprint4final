import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  getAllProductos, crearProducto, eliminarProducto,
  getCategorias, crearCategoria, eliminarCategoria,
  getCaracteristicas, crearCaracteristica, editarCaracteristica, eliminarCaracteristica,
  getUsuarios, cambiarRolUsuario
} from '../services/api';
import './Admin.css';

export default function Admin() {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const [vista, setVista] = useState('menu');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [exito, setExito] = useState('');

  // Datos
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [caracteristicas, setCaracteristicas] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [confirmDelete, setConfirmDelete] = useState(null);

  // Forms
  const [formProducto, setFormProducto] = useState({ nombre: '', descripcion: '', categoriaId: '', imagenes: [''], caracteristicaIds: [] });
  const [formCategoria, setFormCategoria] = useState({ nombre: '', descripcion: '', icono: '', imagenUrl: '' });
  const [formCaract, setFormCaract] = useState({ nombre: '', icono: '' });
  const [editCaract, setEditCaract] = useState(null);

  const esMobile = window.innerWidth < 768;
  if (esMobile) return (
    <div className="admin-mobile-msg">
      <h2>Panel no disponible</h2>
      <p>El panel de administración no está disponible en dispositivos móviles.</p>
    </div>
  );

  if (!isAdmin()) return (
    <div className="admin-mobile-msg">
      <h2>Acceso denegado</h2>
      <p>Solo los administradores pueden acceder a esta sección.</p>
      <button className="btn btn-primary" onClick={() => navigate('/')}>Volver al inicio</button>
    </div>
  );

  const msg = (ok, err) => { if (ok) setExito(ok); if (err) setError(err); setTimeout(() => { setExito(''); setError(''); }, 3000); };

  const cargarProductos = async (page = 0) => {
    setLoading(true);
    try {
      const data = await getAllProductos(page, 10);
      setProductos(data.content); setTotalPaginas(data.totalPages); setPagina(page);
    } catch { setError('Error al cargar productos.'); } finally { setLoading(false); }
  };

  const irA = async (v) => {
    setVista(v); setError(''); setExito('');
    if (v === 'lista') { cargarProductos(0); getCategorias().then(setCategorias); }
    if (v === 'agregar') {
      const [cats, caracts] = await Promise.all([getCategorias(), getCaracteristicas()]);
      setCategorias(cats); setCaracteristicas(caracts);
      setFormProducto({ nombre: '', descripcion: '', categoriaId: '', imagenes: [''], caracteristicaIds: [] });
    }
    if (v === 'categorias') getCategorias().then(setCategorias);
    if (v === 'caracteristicas') getCaracteristicas().then(setCaracteristicas);
    if (v === 'usuarios') getUsuarios().then(setUsuarios);
  };

  // Producto
  const handleSubmitProducto = async e => {
    e.preventDefault(); setError(''); setExito('');
    try {
      await crearProducto({
        ...formProducto,
        categoriaId: formProducto.categoriaId ? Number(formProducto.categoriaId) : null,
        imagenes: formProducto.imagenes.filter(i => i.trim()),
        caracteristicaIds: formProducto.caracteristicaIds.map(Number)
      });
      msg('✅ Producto agregado correctamente.', '');
      setFormProducto({ nombre: '', descripcion: '', categoriaId: '', imagenes: [''], caracteristicaIds: [] });
    } catch (err) { msg('', err.response?.data?.error || 'Error al agregar el producto.'); }
  };

  const handleEliminarProducto = async (id) => {
    try { await eliminarProducto(id); setProductos(ps => ps.filter(p => p.id !== id)); setConfirmDelete(null); msg('Producto eliminado.', ''); }
    catch { msg('', 'Error al eliminar.'); }
  };

  // Categoría
  const handleSubmitCategoria = async e => {
    e.preventDefault();
    try { await crearCategoria(formCategoria); const cats = await getCategorias(); setCategorias(cats); setFormCategoria({ nombre: '', descripcion: '', icono: '', imagenUrl: '' }); msg('✅ Categoría creada.', ''); }
    catch (err) { msg('', err.response?.data?.error || 'Error al crear categoría.'); }
  };

  const handleEliminarCategoria = async (id) => {
    try { await eliminarCategoria(id); setCategorias(cs => cs.filter(c => c.id !== id)); msg('Categoría eliminada.', ''); }
    catch { msg('', 'Error al eliminar categoría.'); }
  };

  // Característica
  const handleSubmitCaract = async e => {
    e.preventDefault();
    try {
      if (editCaract) {
        await editarCaracteristica(editCaract.id, formCaract);
        msg('✅ Característica editada.', '');
        setEditCaract(null);
      } else {
        await crearCaracteristica(formCaract);
        msg('✅ Característica creada.', '');
      }
      const caracts = await getCaracteristicas(); setCaracteristicas(caracts);
      setFormCaract({ nombre: '', icono: '' });
    } catch (err) { msg('', err.response?.data?.error || 'Error.'); }
  };

  const handleEliminarCaract = async (id) => {
    try { await eliminarCaracteristica(id); setCaracteristicas(cs => cs.filter(c => c.id !== id)); msg('Característica eliminada.', ''); }
    catch { msg('', 'Error al eliminar.'); }
  };

  // Usuarios
  const handleCambiarRol = async (id, rol) => {
    try { await cambiarRolUsuario(id, rol); const us = await getUsuarios(); setUsuarios(us); msg('Rol actualizado.', ''); }
    catch { msg('', 'Error al cambiar rol.'); }
  };

  const toggleCaract = (id) => {
    setFormProducto(f => ({
      ...f,
      caracteristicaIds: f.caracteristicaIds.includes(id)
        ? f.caracteristicaIds.filter(c => c !== id)
        : [...f.caracteristicaIds, id]
    }));
  };

  const navItems = [
    { id: 'menu', label: '🏠 Inicio' },
    { id: 'lista', label: '📋 Productos' },
    { id: 'agregar', label: '➕ Agregar producto' },
    { id: 'categorias', label: '🏷️ Categorías' },
    { id: 'caracteristicas', label: '⭐ Características' },
    { id: 'usuarios', label: '👥 Usuarios' },
  ];

  return (
    <div className="admin">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-title">🏨 Admin</div>
        <nav className="admin-nav">
          {navItems.map(item => (
            <button key={item.id} className={`admin-nav-item ${vista === item.id ? 'active' : ''}`} onClick={() => irA(item.id)}>
              {item.label}
            </button>
          ))}
        </nav>
      </aside>

      <main className="admin-content">
        {error && <div className="error-msg">{error}</div>}
        {exito && <div className="success-msg">{exito}</div>}

        {/* Menú */}
        {vista === 'menu' && (
          <div className="admin-menu">
            <h1>Panel de Administración</h1>
            <p className="admin-menu-sub">Gestioná el catálogo de HotelHub.</p>
            <div className="admin-menu-cards">
              {[{ id: 'lista', icon: '📋', title: 'Productos', desc: 'Ver y eliminar' },
                { id: 'agregar', icon: '➕', title: 'Agregar producto', desc: 'Añadir habitación' },
                { id: 'categorias', icon: '🏷️', title: 'Categorías', desc: 'Gestionar categorías' },
                { id: 'caracteristicas', icon: '⭐', title: 'Características', desc: 'Gestionar características' },
                { id: 'usuarios', icon: '👥', title: 'Usuarios', desc: 'Gestionar roles' }
              ].map(c => (
                <div key={c.id} className="admin-menu-card" onClick={() => irA(c.id)}>
                  <span className="admin-menu-icon">{c.icon}</span>
                  <strong>{c.title}</strong>
                  <span>{c.desc}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Lista productos */}
        {vista === 'lista' && (
          <div>
            <h2 className="admin-section-title">Lista de productos</h2>
            {loading ? <div className="loading">Cargando...</div> : (
              <>
                <table className="admin-table">
                  <thead><tr><th>ID</th><th>Nombre</th><th>Categoría</th><th>Acciones</th></tr></thead>
                  <tbody>
                    {productos.map(p => (
                      <tr key={p.id}>
                        <td>{p.id}</td><td>{p.nombre}</td><td>{p.categoriaNombre || '—'}</td>
                        <td><button className="btn btn-danger admin-btn-sm" onClick={() => setConfirmDelete(p)}>Eliminar</button></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {totalPaginas > 1 && (
                  <div className="admin-pagination">
                    <button className="btn btn-outline" disabled={pagina === 0} onClick={() => cargarProductos(0)}>« Inicio</button>
                    <button className="btn btn-outline" disabled={pagina === 0} onClick={() => cargarProductos(pagina - 1)}>← Anterior</button>
                    <span>Página {pagina + 1} de {totalPaginas}</span>
                    <button className="btn btn-outline" disabled={pagina >= totalPaginas - 1} onClick={() => cargarProductos(pagina + 1)}>Siguiente →</button>
                  </div>
                )}
              </>
            )}
          </div>
        )}

        {/* Agregar producto */}
        {vista === 'agregar' && (
          <div>
            <h2 className="admin-section-title">Agregar producto</h2>
            <form className="admin-form" onSubmit={handleSubmitProducto}>
              <div className="form-group">
                <label>Nombre *</label>
                <input value={formProducto.nombre} onChange={e => setFormProducto(f => ({ ...f, nombre: e.target.value }))} required placeholder="Ej: Suite Presidencial" />
              </div>
              <div className="form-group">
                <label>Descripción *</label>
                <textarea value={formProducto.descripcion} onChange={e => setFormProducto(f => ({ ...f, descripcion: e.target.value }))} required rows={3} />
              </div>
              <div className="form-group">
                <label>Categoría</label>
                <select value={formProducto.categoriaId} onChange={e => setFormProducto(f => ({ ...f, categoriaId: e.target.value }))}>
                  <option value="">Sin categoría</option>
                  {categorias.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Características</label>
                <div className="caracts-grid">
                  {caracteristicas.map(c => (
                    <label key={c.id} className={`caract-chip ${formProducto.caracteristicaIds.includes(c.id) ? 'selected' : ''}`}>
                      <input type="checkbox" checked={formProducto.caracteristicaIds.includes(c.id)} onChange={() => toggleCaract(c.id)} style={{ display: 'none' }} />
                      {c.icono} {c.nombre}
                    </label>
                  ))}
                </div>
              </div>
              <div className="form-group">
                <label>Imágenes (URLs)</label>
                {formProducto.imagenes.map((img, i) => (
                  <input key={i} value={img} onChange={e => { const imgs = [...formProducto.imagenes]; imgs[i] = e.target.value; setFormProducto(f => ({ ...f, imagenes: imgs })); }} placeholder={`URL imagen ${i + 1}`} style={{ marginBottom: '0.4rem' }} />
                ))}
                <button type="button" className="btn btn-outline admin-add-img" onClick={() => setFormProducto(f => ({ ...f, imagenes: [...f.imagenes, ''] }))}>+ Agregar imagen</button>
              </div>
              <button type="submit" className="btn btn-primary admin-submit">Guardar producto</button>
            </form>
          </div>
        )}

        {/* Categorías */}
        {vista === 'categorias' && (
          <div>
            <h2 className="admin-section-title">Categorías</h2>
            <form className="admin-form" onSubmit={handleSubmitCategoria} style={{ marginBottom: '2rem' }}>
              <div className="admin-row-2">
                <div className="form-group">
                  <label>Nombre *</label>
                  <input value={formCategoria.nombre} onChange={e => setFormCategoria(f => ({ ...f, nombre: e.target.value }))} required />
                </div>
                <div className="form-group">
                  <label>Ícono (emoji)</label>
                  <input value={formCategoria.icono} onChange={e => setFormCategoria(f => ({ ...f, icono: e.target.value }))} placeholder="🏨" />
                </div>
              </div>
              <div className="form-group">
                <label>Descripción</label>
                <input value={formCategoria.descripcion} onChange={e => setFormCategoria(f => ({ ...f, descripcion: e.target.value }))} />
              </div>
              <div className="form-group">
                <label>URL de imagen</label>
                <input value={formCategoria.imagenUrl} onChange={e => setFormCategoria(f => ({ ...f, imagenUrl: e.target.value }))} placeholder="https://..." />
              </div>
              <button type="submit" className="btn btn-primary admin-submit">Agregar categoría</button>
            </form>
            <table className="admin-table">
              <thead><tr><th>ID</th><th>Ícono</th><th>Nombre</th><th>Acciones</th></tr></thead>
              <tbody>
                {categorias.map(c => (
                  <tr key={c.id}>
                    <td>{c.id}</td><td>{c.icono}</td><td>{c.nombre}</td>
                    <td><button className="btn btn-danger admin-btn-sm" onClick={() => handleEliminarCategoria(c.id)}>Eliminar</button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Características */}
        {vista === 'caracteristicas' && (
          <div>
            <h2 className="admin-section-title">Características</h2>
            <form className="admin-form" onSubmit={handleSubmitCaract} style={{ marginBottom: '2rem' }}>
              <div className="admin-row-2">
                <div className="form-group">
                  <label>Nombre *</label>
                  <input value={formCaract.nombre} onChange={e => setFormCaract(f => ({ ...f, nombre: e.target.value }))} required />
                </div>
                <div className="form-group">
                  <label>Ícono (emoji)</label>
                  <input value={formCaract.icono} onChange={e => setFormCaract(f => ({ ...f, icono: e.target.value }))} placeholder="📶" />
                </div>
              </div>
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <button type="submit" className="btn btn-primary admin-submit">
                  {editCaract ? 'Guardar cambios' : 'Agregar característica'}
                </button>
                {editCaract && <button type="button" className="btn btn-outline" onClick={() => { setEditCaract(null); setFormCaract({ nombre: '', icono: '' }); }}>Cancelar</button>}
              </div>
            </form>
            <table className="admin-table">
              <thead><tr><th>ID</th><th>Ícono</th><th>Nombre</th><th>Acciones</th></tr></thead>
              <tbody>
                {caracteristicas.map(c => (
                  <tr key={c.id}>
                    <td>{c.id}</td><td>{c.icono}</td><td>{c.nombre}</td>
                    <td style={{ display: 'flex', gap: '0.4rem' }}>
                      <button className="btn btn-outline admin-btn-sm" onClick={() => { setEditCaract(c); setFormCaract({ nombre: c.nombre, icono: c.icono }); }}>Editar</button>
                      <button className="btn btn-danger admin-btn-sm" onClick={() => handleEliminarCaract(c.id)}>Eliminar</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Usuarios */}
        {vista === 'usuarios' && (
          <div>
            <h2 className="admin-section-title">Usuarios</h2>
            <table className="admin-table">
              <thead><tr><th>ID</th><th>Nombre</th><th>Email</th><th>Rol</th><th>Acciones</th></tr></thead>
              <tbody>
                {usuarios.map(u => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td>{u.nombre} {u.apellido}</td>
                    <td>{u.email}</td>
                    <td><span className={`rol-badge ${u.rol}`}>{u.rol}</span></td>
                    <td>
                      {u.rol === 'USER'
                        ? <button className="btn btn-outline admin-btn-sm" onClick={() => handleCambiarRol(u.id, 'ADMIN')}>Hacer admin</button>
                        : <button className="btn btn-outline admin-btn-sm" onClick={() => handleCambiarRol(u.id, 'USER')}>Quitar admin</button>
                      }
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Modal confirmación eliminar producto */}
        {confirmDelete && (
          <div className="admin-modal-overlay">
            <div className="admin-modal">
              <h3>¿Eliminar producto?</h3>
              <p>Estás por eliminar <strong>{confirmDelete.nombre}</strong>. Esta acción no se puede deshacer.</p>
              <div className="admin-modal-actions">
                <button className="btn btn-outline" onClick={() => setConfirmDelete(null)}>Cancelar</button>
                <button className="btn btn-danger" onClick={() => handleEliminarProducto(confirmDelete.id)}>Sí, eliminar</button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
