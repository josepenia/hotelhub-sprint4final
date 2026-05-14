import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMisFavoritos, toggleFavorito } from '../services/api';
import { useAuth } from '../context/AuthContext';
import ProductCard from '../components/Product/ProductCard';
import './Favoritos.css';

export default function Favoritos() {
  const [favoritos, setFavoritos] = useState([]);
  const [loading, setLoading] = useState(true);
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoggedIn()) { navigate('/login'); return; }
    getMisFavoritos()
      .then(setFavoritos)
      .finally(() => setLoading(false));
  }, []);

  const handleToggle = async (productoId) => {
    await toggleFavorito(productoId);
    setFavoritos(favs => favs.filter(f => f.productoId !== productoId));
  };

  return (
    <main className="favoritos-page">
      <div className="container">
        <div className="favoritos-header">
          <h1>❤️ Mis favoritos</h1>
          <button className="btn btn-outline" onClick={() => navigate('/')}>← Volver al inicio</button>
        </div>

        {loading ? (
          <div className="loading">Cargando favoritos...</div>
        ) : favoritos.length === 0 ? (
          <div className="favoritos-empty">
            <p>🤍 No tenés productos favoritos todavía.</p>
            <button className="btn btn-primary" onClick={() => navigate('/')}>Explorar habitaciones</button>
          </div>
        ) : (
          <div className="favoritos-grid">
            {favoritos.map(f => (
              <ProductCard
                key={f.productoId}
                producto={{ id: f.productoId, nombre: f.nombre, descripcion: f.descripcion, imagenes: f.imagenes, categoriaNombre: f.categoriaNombre }}
                esFavorito={true}
                onToggleFav={handleToggle}
              />
            ))}
          </div>
        )}
      </div>
    </main>
  );
}
