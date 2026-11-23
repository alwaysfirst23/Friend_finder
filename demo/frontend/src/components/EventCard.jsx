import './EventCard.css'
import CardTopic from './CardTopic'
import defaultAvatar from '../assets/images/avatar2.svg'

function EventCard({ id, topic, date, members, author, text, avatar, onOpen }) {
  return (
    <article className="event-card" id={`event-${id}`} role="button" tabIndex={0}
      aria-label={`Открыть мероприятие: ${topic}, ${author}`}
      onClick={onOpen}
      onKeyDown={(event) => {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault()
          onOpen?.()
        }
      }}>
      <CardTopic topic={topic} date={date} members={members}/>

      <div className="card-body">
        <img className="card-avatar" src={avatar || defaultAvatar} alt='Аватар' />

        <div className='card-content'>
          <p className='card-author'>{author}:</p>
          <p className='card-text'>{text}</p>
        </div>
      </div>
    </article>
  )
}

export default EventCard
