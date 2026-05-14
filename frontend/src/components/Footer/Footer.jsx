import './Footer.css';

export default function Footer() {
  const year = new Date().getFullYear();

  return (
    <footer className="footer">
      <div className="footer-inner container">
        <div className="footer-brand">
          <span className="footer-logo">🏨</span>
          <div>
            <span className="footer-name">HotelHub</span>
            <span className="footer-copy">© {year} HotelHub. Todos los derechos reservados.</span>
          </div>
        </div>
      </div>
    </footer>
  );
}
