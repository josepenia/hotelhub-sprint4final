import './WhatsAppButton.css';

const WHATSAPP_NUMBER = '5491100000000'; // número de ejemplo del hotel
const WHATSAPP_MSG = encodeURIComponent('Hola! Quiero hacer una consulta sobre una habitación en HotelHub.');

export default function WhatsAppButton() {
  const url = `https://wa.me/${WHATSAPP_NUMBER}?text=${WHATSAPP_MSG}`;

  return (
    <a
      href={url}
      target="_blank"
      rel="noopener noreferrer"
      className="whatsapp-btn"
      title="Consultanos por WhatsApp"
      aria-label="Contactar por WhatsApp"
    >
      <svg viewBox="0 0 32 32" fill="currentColor" className="whatsapp-icon">
        <path d="M16 0C7.163 0 0 7.163 0 16c0 2.833.74 5.49 2.032 7.8L0 32l8.42-2.006A15.94 15.94 0 0016 32c8.837 0 16-7.163 16-16S24.837 0 16 0zm0 29.333a13.28 13.28 0 01-6.763-1.847l-.485-.287-4.997 1.191 1.238-4.86-.317-.5A13.24 13.24 0 012.667 16C2.667 8.636 8.636 2.667 16 2.667S29.333 8.636 29.333 16 23.364 29.333 16 29.333zm7.27-9.928c-.398-.199-2.358-1.163-2.724-1.296-.366-.133-.633-.199-.9.2-.266.398-1.032 1.296-1.265 1.562-.233.266-.466.3-.864.1-.398-.2-1.682-.62-3.203-1.977-1.184-1.057-1.983-2.362-2.216-2.76-.233-.398-.025-.613.175-.812.18-.179.398-.466.597-.7.2-.232.266-.398.4-.664.133-.266.066-.499-.033-.698-.1-.2-.9-2.163-1.232-2.963-.325-.78-.655-.673-.9-.686l-.766-.013c-.266 0-.698.1-1.065.499-.366.398-1.398 1.364-1.398 3.328s1.432 3.86 1.631 4.127c.2.266 2.82 4.306 6.832 6.036.955.412 1.7.658 2.281.843.959.305 1.832.262 2.52.159.769-.115 2.358-.964 2.691-1.895.333-.932.333-1.73.233-1.896-.099-.166-.366-.266-.764-.466z"/>
      </svg>
      <span className="whatsapp-tooltip">¿Necesitás ayuda?</span>
    </a>
  );
}
