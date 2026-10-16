import './EventCard.css'
import CardTopic from './CardTopic'
import defaultAvatar from '../assets/images/avatar2.svg'

function EventCard({ topic, date, members, author, text, avatar }) {
  return (
    <article className="event-card">
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