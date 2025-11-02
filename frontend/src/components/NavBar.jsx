import { Link } from 'react-router-dom'

export default function NavBar() {
  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="nav-logo">
          Sistema de Reservas
        </Link>
        <div className="nav-links">
          <Link to="/citizen" className="nav-link">Cidadão</Link>
          <Link to="/staff" className="nav-link">Staff</Link>
          <Link to="/search" className="nav-link">Consultar</Link>
        </div>
      </div>
    </nav>
  )
}
