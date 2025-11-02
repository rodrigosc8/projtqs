import { useState } from 'react'
import axios from 'axios'

export default function SearchPage() {
  const [token, setToken] = useState('')
  const [booking, setBooking] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSearch = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setBooking(null)

    try {
      const response = await axios.get(`/api/bookings/${token}`)
      setBooking(response.data)
    } catch (err) {
      setError('Reserva não encontrada')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="search-page">
      <h1>Consultar Reserva</h1>

      <div className="card">
        <form onSubmit={handleSearch}>
          <div className="form-group">
            <label htmlFor="token">Token da Reserva</label>
            <input
              type="text"
              id="token"
              value={token}
              onChange={(e) => setToken(e.target.value)}
              placeholder="Cole o token aqui"
              required
            />
          </div>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Procurando...' : 'Consultar'}
          </button>
        </form>

        {error && <div className="alert alert-error">{error}</div>}

        {booking && (
          <div className="booking-result">
            <h3>Detalhes da Reserva</h3>
            <div className="detail-row">
              <strong>Token:</strong>
              <span>{booking.token}</span>
            </div>
            <div className="detail-row">
              <strong>Município:</strong>
              <span>{booking.municipality}</span>
            </div>
            <div className="detail-row">
              <strong>Data:</strong>
              <span>{booking.date}</span>
            </div>
            <div className="detail-row">
              <strong>Período:</strong>
              <span>{booking.timeslot}</span>
            </div>
            <div className="detail-row">
              <strong>Descrição:</strong>
              <span>{booking.description}</span>
            </div>
            <div className="detail-row">
              <strong>Estado:</strong>
              <span className={`badge badge-${booking.status.toLowerCase()}`}>
                {booking.status}
              </span>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
