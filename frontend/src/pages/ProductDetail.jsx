import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProductoById, getFechasOcupadas, crearReserva, getPuntuaciones, getPromedioPuntuacion, puntuar, getPoliticas } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './ProductDetail.css';

function estrellasHTML(n) {
  return Array.from({ length: 5 }, (_, i) => i < n ? '★' : '☆').join('');
}

function generarMeses(cantidad = 2) {
  const hoy = new Date();
  const meses = [];
  for (let m = 0; m < cantidad; m++) {
    const fecha = new Date(hoy.getFullYear(), hoy.getMonth() + m, 1);
    const diasEnMes = new Date(fecha.getFullYear(), fecha.getMonth() + 1, 0).getDate();
    const primerDia = fecha.getDay();
    meses.push({ year: fecha.getFullYear(), month: fecha.getMonth(), diasEnMes, primerDia });
  }
  return meses;
}

function esFechaOcupada(year, month, day, fechasOcupadas) {
  const fecha = new Date(year, month, day);
  return fechasOcupadas.some(r => {
    const ini = new Date(r.inicio + 'T00:00:00');
    const fin = new Date(r.fin + 'T00:00:00');
    return fecha >= ini && fecha <= fin;
  });
}

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isLoggedIn } = useAuth();

  const [producto, setProducto] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [galeriaAbierta, setGaleriaAbierta] = useState(false);
  const [imagenActiva, setImagenActiva] = useState(0);

  const [fechasOcupadas, setFechasOcupadas] = useState([]);
  const [reservaInicio, setReservaInicio] = useState('');
  const [reservaFin, setReservaFin] = useState('');
  const [reservaMsg, setReservaMsg] = useState('');
  const [reservaError, setReservaError] = useState('');

  const [puntuaciones, setPuntuaciones] = useState([]);
  const [promedio, setPromedio] = useState({ promedio: 0, total: 0 });
  const [misEstrellas, setMisEstrellas] = useState(0);
  const [miComentario, setMiComentario] = useState('');
  const [resenaMsg, setResenaMsg] = useState('');

  const [politicas, setPoliticas] = useState([]);
  const [shareOpen, setShareOpen] = useState(false);
  const [mensajeCompartir, setMensajeCompartir] = useState('');

  const meses = generarMeses(2);

  useEffect(() => {
    Promise.all([
      getProductoById(id),
      getFechasOcupadas(id),
      getPuntuaciones(id),
      getPromedioPuntuacion(id),
      getPoliticas(id)
    ]).then(([prod, fechas, punts, prom, pols]) => {
      setProducto(prod);
      setFechasOcupadas(fechas);
      setPuntuaciones(punts);
      setPromedio(prom);
      setPoliticas(pols);
    }).catch(() => setError('No se pudo cargar el producto.'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleReservar = (e) => {
    e.preventDefault();
    setReservaMsg(''); setReservaError('');
    if (!isLoggedIn()) { navigate(`/reserva/${id}`); };
    try {
      await crearReserva({ productoId: id, fechaInicio: reservaInicio, fechaFin: reservaFin });
      setReservaMsg('✅ ¡Reserva realizada con éxito!');
      setReservaInicio(''); setReservaFin('');
      const fechas = await getFechasOcupadas(id);
      setFechasOcupadas(fechas);
    } catch (err) {
      setReservaError(err.response?.data?.error || 'Error al realizar la reserva.');
    }
  };

  const handlePuntuar = async (e) => {
    e.preventDefault();
    setResenaMsg('');
    if (!isLoggedIn()) { navigate(`/reserva/${id}`); };
    try {
      await puntuar(id, { estrellas: misEstrellas, comentario: miComentario });
      setResenaMsg('✅ ¡Gracias por tu reseña!');
      const [punts, prom] = await Promise.all([getPuntuaciones(id), getPromedioPuntuacion(id)]);
      setPuntuaciones(punts); setPromedio(prom);
      setMisEstrellas(0); setMiComentario('');
    } catch (err) {
      setResenaMsg(err.response?.data?.error || 'Error al puntuar.');
    }
  };

  const handleCompartir = (red) => {
    const url = window.location.href;
    const texto = mensajeCompartir || `¡Mirá esta habitación: ${producto?.nombre}!`;
    const links = {
      facebook: `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`,
      twitter: `https://twitter.com/intent/tweet?text=${encodeURIComponent(texto)}&url=${encodeURIComponent(url)}`,
      copiar: null
    };
    if (red === 'copiar') {
      navigator.clipboard.writeText(url);
      alert('¡Enlace copiado al portapapeles!');
    } else {
      window.open(links[red], '_blank');
    }
    setShareOpen(false);
  };

  if (loading) return <div className="loading" style={{ paddingTop: '6rem' }}>Cargando...</div>;
  if (error) return <div className="container" style={{ paddingTop: '6rem' }}><div className="error-msg">{error}</div></div>;
  if (!producto) return null;

  const imagenes = producto.imagenes || [];
  const imgPrincipal = imagenes[0] || 'https://via.placeholder.com/800x500?text=Sin+imagen';
  const imgSecundarias = imagenes.slice(1, 5);
  const hoy = new Date().toISOString().split('T')[0];

  return (
    <main className="detail">
      {/* Header */}
      <div className="detail-header">
        <h1 className="detail-title">{producto.nombre}</h1>
        <button className="btn btn-outline detail-back" onClick={() => navigate(-1)}>← Volver</button>
      </div>

      <div className="container detail-body">
        {/* Galería */}
        <section className="detail-gallery">
          <div className="gallery-main" onClick={() => { setImagenActiva(0); setGaleriaAbierta(true); }}>
            <img src={imgPrincipal} alt={producto.nombre} />
          </div>
          <div className="gallery-grid">
            {imgSecundarias.map((img, i) => (
              <div key={i} className="gallery-thumb" onClick={() => { setImagenActiva(i + 1); setGaleriaAbierta(true); }}>
                <img src={img} alt={`${producto.nombre} ${i + 2}`} />
                {i === 3 && imagenes.length > 5 && (
                  <div className="gallery-more-overlay"><span>Ver más</span></div>
                )}
              </div>
            ))}
            {imgSecundarias.length > 0 && (
              <button className="gallery-ver-mas" onClick={() => setGaleriaAbierta(true)}>Ver más imágenes</button>
            )}
          </div>
        </section>

        {/* Info + Reserva */}
        <section className="detail-info">
          {producto.categoriaNombre && <span className="detail-badge">{producto.categoriaNombre}</span>}

          {/* Promedio estrellas */}
          {promedio.total > 0 && (
            <div className="detail-rating">
              <span className="detail-stars">{estrellasHTML(Math.round(promedio.promedio))}</span>
              <span className="detail-rating-num">{promedio.promedio} ({promedio.total} reseñas)</span>
            </div>
          )}

          <h2 className="detail-subtitle">Descripción</h2>
          <p className="detail-desc">{producto.descripcion}</p>

          {/* Características */}
          {producto.caracteristicas?.length > 0 && (
            <div className="detail-caracts">
              <h3>Características</h3>
              <div className="detail-caracts-grid">
                {producto.caracteristicas.map(c => (
                  <div key={c.id} className="detail-caract-item"><span>{c.icono}</span><span>{c.nombre}</span></div>
                ))}
              </div>
            </div>
          )}

          {/* Reserva */}
          <div className="detail-reserva-form">
            <h3>Reservar</h3>
            <p style={{fontSize:'0.85rem', color:'var(--text-muted)', marginBottom:'0.75rem'}}>
              Seleccioná las fechas disponibles y confirmá tu reserva.
            </p>
            <button
              className="btn btn-primary detail-reserve"
              onClick={() => navigate(`/reserva/${id}`)}
            >
              Seleccionar fechas y reservar →
            </button>
          </div>

          {/* Compartir */}
          <div style={{ marginTop: '1rem', position: 'relative' }}>
            <button className="btn btn-outline share-btn" onClick={() => setShareOpen(o => !o)}>
              🔗 Compartir
            </button>
            {shareOpen && (
              <div className="share-popup">
                <p className="share-popup-title">Compartir producto</p>
                <div className="share-product-preview">
                  <img src={imgPrincipal} alt={producto.nombre} />
                  <div>
                    <strong>{producto.nombre}</strong>
                    <small>{producto.descripcion?.slice(0, 60)}...</small>
                  </div>
                </div>
                <textarea
                  className="share-msg-input"
                  placeholder="Agregá un mensaje personalizado..."
                  value={mensajeCompartir}
                  onChange={e => setMensajeCompartir(e.target.value)}
                  rows={2}
                />
                <div className="share-btns">
                  <button className="share-net-btn facebook" onClick={() => handleCompartir('facebook')}>📘 Facebook</button>
                  <button className="share-net-btn twitter" onClick={() => handleCompartir('twitter')}>🐦 Twitter</button>
                  <button className="share-net-btn copy" onClick={() => handleCompartir('copiar')}>📋 Copiar link</button>
                </div>
              </div>
            )}
          </div>
        </section>
      </div>

      {/* Calendario de disponibilidad */}
      <div className="container">
        <section className="detail-calendar">
          <h2 className="detail-section-title">Disponibilidad</h2>
          <p className="detail-calendar-legend">
            <span className="legend-libre">■</span> Disponible &nbsp;
            <span className="legend-ocupado">■</span> Ocupado
          </p>
          <div className="calendar-double">
            {meses.map(({ year, month, diasEnMes, primerDia }) => {
              const nombreMes = new Date(year, month).toLocaleString('es-AR', { month: 'long', year: 'numeric' });
              const dias = [];
              for (let i = 0; i < primerDia; i++) dias.push(null);
              for (let d = 1; d <= diasEnMes; d++) dias.push(d);
              return (
                <div key={`${year}-${month}`} className="calendar-month">
                  <div className="calendar-month-title">{nombreMes.charAt(0).toUpperCase() + nombreMes.slice(1)}</div>
                  <div className="calendar-weekdays">
                    {['Do','Lu','Ma','Mi','Ju','Vi','Sa'].map(d => <span key={d}>{d}</span>)}
                  </div>
                  <div className="calendar-days">
                    {dias.map((d, i) => {
                      if (!d) return <span key={`e-${i}`} />;
                      const ocupado = esFechaOcupada(year, month, d, fechasOcupadas);
                      const pasado = new Date(year, month, d) < new Date(new Date().setHours(0,0,0,0));
                      return (
                        <span key={d} className={`cal-day ${ocupado ? 'ocupado' : ''} ${pasado ? 'pasado' : ''}`}>
                          {d}
                        </span>
                      );
                    })}
                  </div>
                </div>
              );
            })}
          </div>
        </section>
      </div>

      {/* Políticas */}
      {politicas.length > 0 && (
        <div className="container">
          <section className="detail-politicas">
            <h2 className="detail-section-title politicas-title">Políticas del producto</h2>
            <div className="politicas-grid">
              {politicas.map(p => (
                <div key={p.id} className="politica-item">
                  <strong>{p.titulo}</strong>
                  <p>{p.descripcion}</p>
                </div>
              ))}
            </div>
          </section>
        </div>
      )}

      {/* Reseñas */}
      <div className="container">
        <section className="detail-resenas">
          <h2 className="detail-section-title">
            Reseñas {promedio.total > 0 && <span className="resenas-avg">{estrellasHTML(Math.round(promedio.promedio))} {promedio.promedio} ({promedio.total})</span>}
          </h2>

          {/* Formulario reseña */}
          {isLoggedIn() && (
            <form className="resena-form" onSubmit={handlePuntuar}>
              <p className="resena-form-label">Tu puntuación:</p>
              <div className="star-selector">
                {[1,2,3,4,5].map(n => (
                  <span key={n} className={`star-opt ${n <= misEstrellas ? 'sel' : ''}`} onClick={() => setMisEstrellas(n)}>★</span>
                ))}
              </div>
              <textarea
                placeholder="Contá tu experiencia (opcional)..."
                value={miComentario}
                onChange={e => setMiComentario(e.target.value)}
                rows={3}
                className="resena-textarea"
              />
              {resenaMsg && <div className={resenaMsg.startsWith('✅') ? 'success-msg-sm' : 'error-msg-sm'}>{resenaMsg}</div>}
              <button type="submit" className="btn btn-primary" disabled={misEstrellas === 0}>Enviar reseña</button>
            </form>
          )}

          {/* Lista reseñas */}
          <div className="resenas-list">
            {puntuaciones.length === 0 ? (
              <p className="resenas-empty">Todavía no hay reseñas para este producto.</p>
            ) : puntuaciones.map(r => (
              <div key={r.id} className="resena-item">
                <div className="resena-header">
                  <strong>{r.usuario}</strong>
                  <span className="resena-stars">{estrellasHTML(r.estrellas)}</span>
                  <span className="resena-fecha">{r.fecha}</span>
                </div>
                {r.comentario && <p className="resena-comentario">{r.comentario}</p>}
              </div>
            ))}
          </div>
        </section>
      </div>

      {/* Modal galería */}
      {galeriaAbierta && (
        <div className="gallery-modal" onClick={() => setGaleriaAbierta(false)}>
          <div className="gallery-modal-content" onClick={e => e.stopPropagation()}>
            <button className="gallery-modal-close" onClick={() => setGaleriaAbierta(false)}>✕</button>
            <img src={imagenes[imagenActiva] || imgPrincipal} alt={producto.nombre} className="gallery-modal-img" />
            <div className="gallery-modal-thumbs">
              {imagenes.map((img, i) => (
                <img key={i} src={img} alt="" className={`gallery-modal-thumb ${i === imagenActiva ? 'active' : ''}`} onClick={() => setImagenActiva(i)} />
              ))}
            </div>
            <div className="gallery-modal-nav">
              <button className="btn btn-outline" onClick={() => setImagenActiva(i => Math.max(0, i - 1))} disabled={imagenActiva === 0}>← Anterior</button>
              <span>{imagenActiva + 1} / {imagenes.length}</span>
              <button className="btn btn-outline" onClick={() => setImagenActiva(i => Math.min(imagenes.length - 1, i + 1))} disabled={imagenActiva === imagenes.length - 1}>Siguiente →</button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
