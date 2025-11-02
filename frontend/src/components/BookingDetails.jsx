import { useState, useEffect } from 'react'
import axios from 'axios'

export default function BookingDetails({ token, onClose, onUpdate }) {
  const [booking, setBooking] = useState(null)
  const [newStatus, setNewStatus] = useState('')
  const [note, setNote] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadBooking()
  }, [token])

  const loadBooking = async () => {
    try {
      const response = await axios.get(`/api/bookings/${token}`)
      setBooking(response.data)
      setNewStatus(response.data.status)
      setLoading(false)
    } catch (err) {
      console.error('Erro ao carregar booking:', err)
      setLoading(false)
    }
  }

  const handleUpdateStatus = async () => {
    try {
      await axios.put(`/api/bookings/${token}/status`, {
        newStatus,
        note
      })
      alert('Estado atualizado com sucesso!')
      onUpdate()
      onClose()
    } catch (err) {
      alert('Erro ao atualizar estado')
    }
  }

  if (loading) return <div className="modal-overlay"><p>Carregando...</p></div>
  if (!booking) return null

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Detalhes da Reserva</h2>
          <button onClick={onClose} className="btn-close">×</button>
        </div>

        <div className="modal-body">
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
            <strong>Estado Atual:</strong>
            <span className={`badge badge-${booking.status.toLowerCase()}`}>
              {booking.status}
            </span>
          </div>

          <hr />

          <h3>Alterar Estado</h3>
          <div className="form-group">
            <select 
              value={newStatus} 
              onChange={(e) => setNewStatus(e.target.value)}
              className="form-control"
            >
              <option value="RECEIVED">RECEIVED</option>
              <option value="ASSIGNED">ASSIGNED</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="COMPLETED">COMPLETED</option>
              <option value="CANCELLED">CANCELLED</option>
            </select>
          </div>

          <div className="form-group">
            <input
              type="text"
              placeholder="Nota (opcional)"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              className="form-control"
            />
          </div>

          <button onClick={handleUpdateStatus} className="btn btn-primary">
            Atualizar Estado
          </button>

          {booking.stateHistory && booking.stateHistory.length > 0 && (
            <>
              <hr />
              <h3>Histórico de Estados</h3>
              <div className="history-list">
                {booking.stateHistory.map((sh, idx) => (
                  <div key={idx} className="history-item">
                    <strong>{sh.newStatus}</strong>
                    <span className="history-date">
                      {new Date(sh.changedAt).toLocaleString('pt-PT')}
                    </span>
                    {sh.note && <p className="history-note">{sh.note}</p>}
                  </div>
                ))}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  )
}
