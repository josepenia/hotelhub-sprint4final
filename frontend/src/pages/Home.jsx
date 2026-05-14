import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ProductCard from '../components/Product/ProductCard';
import { getProductosAleatorios, getCategorias, getAllProductos, getMisFavoritosIds, toggleFavorito } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './Home.css';

export default function Home() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [favIds, setFavIds] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([getProductosAleatorios(), getCategorias()])
      .then(([prods, cats]) => { setProductos(prods); setCategorias(cats); })
      .catch(() => setError('No se pudieron cargar los productos. Verificá que el backend esté corriendo.'))
      .finally(() => setLoading(false));

    if (isLoggedIn()) {
      getMisFavoritosIds().then(setFavIds).catch(() => {});
    }
  }, []);

  const handleBuscar = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await getAllProductos(0, 100);
      let filtrados = data.content;
      if (busqueda.trim()) {
        filtrados = filtrados.filter(p =>
          p.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
          p.descripcion.toLowerCase().includes(busqueda.toLowerCase())
        );
      }
      setProductos(filtrados);
    } catch { setError('Error al buscar productos.'); }
    finally { setLoading(false); }
  };

  const handleCategoria = async (catId) => {
    setLoading(true);
    try {
      const data = await getAllProductos(0, 100);
      setProductos(data.content.filter(p => p.categoriaId === catId));
    } catch { setError('Error al filtrar.'); }
    finally { setLoading(false); }
  };

  const handleVerTodos = async () => {
    setBusqueda(''); setFechaInicio(''); setFechaFin('');
    setLoading(true);
    try { setProductos(await getProductosAleatorios()); }
    finally { setLoading(false); }
  };

  const handleToggleFav = async (productoId) => {
    if (!isLoggedIn()) { navigate('/login'); return; }
    try {
      const res = await toggleFavorito(productoId);
      setFavIds(ids => res.favorito ? [...ids, productoId] : ids.filter(id => id !== productoId));
    } catch {}
  };

  return (
    <main className="home">
      {/* Buscador */}
      <section className="home-hero">
        <div className="container">
          <h1 className="home-hero-title">Encontrá tu habitación ideal</h1>
          <p className="home-hero-sub">Buscá por nombre o seleccioná un rango de fechas para ver disponibilidad</p>
          <form className="home-search" onSubmit={handleBuscar}>
            <input
              type="text"
              placeholder="Buscar habitaciones..."
              value={busqueda}
              onChange={e => setBusqueda(e.target.value)}
              className="home-search-input"
            />
            <div className="home-search-dates">
              <div className="home-date-field">
                <label>Check-in</label>
                <input type="date" value={fechaInicio} min={new Date().toISOString().split('T')[0]}
                  onChange={e => setFechaInicio(e.target.value)} className="home-date-input" />
              </div>
              <div className="home-date-field">
                <label>Check-out</label>
                <input type="date" value={fechaFin} min={fechaInicio || new Date().toISOString().split('T')[0]}
                  onChange={e => setFechaFin(e.target.value)} className="home-date-input" />
              </div>
            </div>
            <button type="submit" className="btn btn-accent">Buscar</button>
            {(busqueda || fechaInicio) && (
              <button type="button" className="btn btn-outline home-search-clear" onClick={handleVerTodos}>Ver todos</button>
            )}
          </form>
        </div>
      </section>

      <div className="container home-body">
        {/* Categorías */}
        <section className="home-cats">
          <h2 className="section-title">Categorías</h2>
          <div className="home-cats-grid">
            {categorias.map(cat => (
              <button key={cat.id} className="cat-chip" onClick={() => handleCategoria(cat.id)}>
                <span className="cat-icon">{cat.icono}</span>{cat.nombre}
              </button>
            ))}
          </div>
        </section>

        {/* Productos */}
        <section className="home-products">
          <h2 className="section-title">Recomendaciones</h2>
          {error && <div className="error-msg">{error}</div>}
          {loading ? (
            <div className="loading">Cargando habitaciones...</div>
          ) : productos.length === 0 ? (
            <p className="home-empty">No se encontraron resultados.</p>
          ) : (
            <div className="home-products-grid">
              {productos.map(p => (
                <ProductCard
                  key={p.id}
                  producto={p}
                  esFavorito={favIds.includes(p.id)}
                  onToggleFav={handleToggleFav}
                />
              ))}
            </div>
          )}
        </section>
      </div>
    </main>
  );
}
