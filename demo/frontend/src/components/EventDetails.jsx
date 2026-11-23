import { useEffect, useRef } from 'react'
import { getTopicIcon } from '../utils/icons'
import defaultAvatar from '../assets/images/avatar2.svg'
import chatIcon from '../assets/icons/chat-icon-green.svg'
import locationIcon from '../assets/icons/location-icon-black.svg'
import calendarIcon from '../assets/icons/calendar-icon-green.svg'
import timeIcon from '../assets/icons/time-icon.svg'
import personsIcon from '../assets/icons/persons-icon.svg'
import descriptionIcon from '../assets/icons/openlist-icon.svg'
import './EventDetails.css'

function EventDetails({ event, favorite, joined, onBack, onFavorite, onJoin }) {
  const headingRef = useRef(null)
  const members = event.members + (joined ? 1 : 0)
  const isFull = event.capacity && members >= event.capacity
  const [day, month, year] = event.date.split('.')
  const date = new Date(Number(year), Number(month) - 1, Number(day)).toLocaleDateString('ru-RU', {
    day: 'numeric', month: 'long',
  })

  useEffect(() => {
    headingRef.current?.focus()
  }, [])

  return (
    <section className='event-details' aria-label='Подробности мероприятия'>
      <button className='event-details__back' type='button' onClick={onBack}>← К мероприятиям</button>

      <div className='event-details__intro'>
        <div className='event-details__organizer'>
          <img className='event-details__avatar' src={event.avatar || defaultAvatar} alt={`Аватар ${event.author}`} />
          <div>
            <p className='event-details__owner-label'>Владелец события</p>
            <p className='event-details__owner-name'>{event.author}</p>
          </div>
          <button className='event-details__chat' type='button' disabled title='Чат будет доступен после подключения бэкенда'>
            <img src={chatIcon} alt='' />
            Чат с организатором
          </button>
        </div>

        <div className='event-details__summary'>
          <div className='event-details__summary-content'>
            <h1 ref={headingRef} tabIndex={-1} className='event-details__topic'>
              <img src={getTopicIcon(event.topic)} alt='' />{event.topic}
            </h1>
            <p className='event-details__title'>{event.title || event.text}</p>
          </div>
          <button className={`event-details__favorite${favorite ? ' event-details__favorite--active' : ''}`}
            type='button' aria-label={favorite ? 'Убрать из избранного' : 'Добавить в избранное'}
            aria-pressed={favorite} onClick={onFavorite}>
            <svg viewBox='0 0 32 32' aria-hidden='true'>
              <path d='M16 28 4 16C-5 6 9-2 16 7 23-2 37 6 28 16Z' />
            </svg>
          </button>
        </div>
      </div>

      <div className='event-details__description'>
        <h2><img src={descriptionIcon} alt='' />Описание</h2>
        <p>{event.description || event.text}</p>
      </div>

      <div className='event-details__location'>
        <span><img src={locationIcon} alt='' />Место проведения:</span>
        <p>{event.address || 'Место уточняется'}</p>
      </div>

      <div className='event-details__footer'>
        <div className='event-details__meta'><img src={calendarIcon} alt='' /><span>{date}</span></div>
        <div className='event-details__meta'><img src={timeIcon} alt='' /><span>{event.time || 'Время уточняется'}</span></div>
        <div className='event-details__meta' aria-label={`Участников: ${members}${event.capacity ? ` из ${event.capacity}` : ''}`}>
          <img src={personsIcon} alt='' /><span>{members}{event.capacity ? `/${event.capacity}` : ''}</span>
        </div>
        <button className='event-details__join' type='button' onClick={onJoin}
          aria-pressed={joined} disabled={!joined && isFull}>
          {joined ? 'Отменить участие' : isFull ? 'Мест нет' : 'Присоединиться'}
        </button>
      </div>
      <p className='event-details__notice' role='status'>
        {joined ? 'Вы присоединились. ' : ''}Демонстрационный режим: участие и избранное сохраняются до обновления страницы.
      </p>
    </section>
  )
}

export default EventDetails
