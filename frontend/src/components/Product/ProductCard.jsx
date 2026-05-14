import { useNavigate } from 'react-router-dom';
import './ProductCard.css';

export default function ProductCard({ producto, esFavorito, onToggleFav }) {
  const navigate = useNavigate();
  const imagen = producto.imagenes?.[0] || 'https://via.placeholder.com/400x250?text=Sin+imagen';

  return (
    <div className="product-card">
      <div className="product-card-img" onClick={() => navigate(`/producto/${producto.id}`)}>
        <img src={imagen} alt={producto.nombre} loading="lazy" />
        {producto.categoriaNombre && (
          <span className="product-card-badge">{producto.categoriaNombre}</span>
        )}
        {onToggleFav && (
          <button
            className={`fav-btn ${esFavorito ? 'active' : ''}`}
            onClick={e => { e.stopPropagation(); onToggleFav(producto.id); }}
            title={esFavorito ? 'Quitar de favoritos' : 'Agregar a favoritos'}
          >
            {esFavorito ? '❤️' : '🤍'}
          </button>
        )}
      </div>
      <div className="product-card-body" onClick={() => navigate(`/producto/${producto.id}`)}>
        <h3 className="product-card-title">{producto.nombre}</h3>
        <p className="product-card-desc">{producto.descripcion}</p>
        <button className="btn btn-primary product-card-btn">Ver detalle</button>
      </div>
    </div>
  );
}
