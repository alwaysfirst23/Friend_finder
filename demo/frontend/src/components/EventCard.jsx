import './EventCard.css'
import CardTopic from './CardTopic'
import Avatar from '../assets/images/avatar2.svg'

function EventCard() {
  return (
    <article className="event-card">
      <CardTopic/>

      <div className="card-body">
        <img className="card-avatar" src={Avatar} alt='Аватар' />

        <div className='card-content'>
          <p className='card-author'>Наташа67:</p>
          <p className='card-text'>
            собираю компанию, чтобы вместе готовиться к пересдаче по питону,
            будем вместе молиться, чтобы нас не отчислили олололололололол
            fodskfksad;lfkdlkfl;ak;lfkda;fkd;lskfksfkdskfds;lkf;ldskf;ldsklf;kdsf
          </p>
        </div>
      </div>
    </article>
  )
}

export default EventCard