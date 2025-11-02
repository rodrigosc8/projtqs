import { Link } from 'react-router-dom'

export default function HomePage() {
  return (
    <div className="home-page">
      <h1>Sistema de Reservas</h1>
      <p className="subtitle">Escolha o seu perfil</p>

      <div className="card-grid">
        <div className="card card-hover">
          <div className="card-icon">👤</div>
          <h2>Cidadão</h2>
          <p>Criar uma nova reserva</p>
          <Link to="/citizen" className="btn btn-primary">Aceder</Link>
        </div>

        <div className="card card-hover">
          <div className="card-icon">👨‍💼</div>
          <h2>Staff</h2>
          <p>Gerir reservas existentes</p>
          <Link to="/staff" className="btn btn-secondary">Aceder</Link>
        </div>
      </div>
    </div>
  )
}
