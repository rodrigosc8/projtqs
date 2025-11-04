import { useState } from 'react'
import BookingForm from '../components/BookingForm'

export default function CitizenPage() {
  const [showSuccess, setShowSuccess] = useState(false)
  const [createdBooking, setCreatedBooking] = useState(null)

  const handleSuccess = (booking) => {
    setCreatedBooking(booking)
    setShowSuccess(true)
  }

  return (
    <div className="citizen-page">
      <h1>Área do Cidadão</h1>
      <BookingForm onSuccess={handleSuccess} />

      {showSuccess && createdBooking && (
        <div className="success-modal">
          <div className="modal-content">
            <h2>Reserva Criada com Sucesso!</h2>
            <div className="token-display">
              <strong>Token:</strong>
              <code>{createdBooking.token}</code>
            </div>
            <p>Guarde este token para consultar a sua reserva.</p>
            <button 
              onClick={() => setShowSuccess(false)} 
              className="btn btn-primary"
            >
              Fechar
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
