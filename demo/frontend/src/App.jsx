import { useEffect, useRef, useState } from 'react'
import MainHeader from './components/MainHeader'
import EventCard from './components/EventCard'
import FeedControls from './components/FeedControls'
import Menu from './components/Menu'
import mountains from './assets/images/mount.svg'
import EventDetails from './components/EventDetails'
import { fetchEvents, fetchEventDetails } from './utils/eventsApi'
import './App.css'

function App() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetchEvents()
      .then((data) => setEvents(data.items))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  const [selectedEvent, setSelectedEvent] = useState(null)
  const [selectedId, setSelectedId] = useState(null)
  const [detailsLoading, setDetailsLoading] = useState(false)
  const [detailsError, setDetailsError] = useState(null)
  const detailsRequest = useRef(null)
  const [favorites, setFavorites] = useState([])
  const [joinedEvents, setJoinedEvents] = useState([])

  useEffect(() => () => detailsRequest.current?.abort(), [])

  async function openEvent(id) {
    detailsRequest.current?.abort()
    const controller = new AbortController()
    detailsRequest.current = controller
    setSelectedId(id)
    setSelectedEvent(null)
    setDetailsError(null)
    setDetailsLoading(true)

    try {
      const event = await fetchEventDetails(id, { signal: controller.signal })
      if (!controller.signal.aborted) setSelectedEvent(event)
    } catch (err) {
      if (!controller.signal.aborted) setDetailsError(err.message)
    } finally {
      if (!controller.signal.aborted) setDetailsLoading(false)
    }
  }

  function toggleEvent(setItems, id) {
    setItems((items) => items.includes(id)
      ? items.filter((item) => item !== id)
      : [...items, id])
  }

  function closeEvent() {
    const id = selectedId
    detailsRequest.current?.abort()
    setSelectedId(null)
    setSelectedEvent(null)
    setDetailsLoading(false)
    setDetailsError(null)
    requestAnimationFrame(() => document.getElementById(`event-${id}`)?.focus())
  }

  return (
    <div className="app">
      <MainHeader />

      <div className="layout">
        <aside className="sidebar">
          <Menu />
          <img className="sidebar__mountains" src={mountains} alt="Вместе интереснее" />
        </aside>

        <main className="content">
          {selectedId !== null ? (
            detailsLoading || detailsError ? (
              <section className="event-details" aria-label="Подробности мероприятия" aria-busy={detailsLoading}>
                <button className="event-details__back" type="button" onClick={closeEvent}>← К мероприятиям</button>
                {detailsLoading && <p role="status">Загрузка мероприятия...</p>}
                {detailsError && (
                  <>
                    <p role="alert">Не удалось загрузить карточку: {detailsError}</p>
                    <button className="event-details__back" type="button" onClick={() => openEvent(selectedId)}>Попробовать снова</button>
                  </>
                )}
              </section>
            ) : selectedEvent && (
            <EventDetails
              key={selectedEvent.id}
              event={selectedEvent}
              favorite={favorites.includes(selectedEvent.id)}
              joined={joinedEvents.includes(selectedEvent.id)}
              onBack={closeEvent}
              onFavorite={() => toggleEvent(setFavorites, selectedEvent.id)}
              onJoin={() => toggleEvent(setJoinedEvents, selectedEvent.id)}
            />
            )
          ) : (
            <>
            <FeedControls />
            <div className="events-list">
            {loading && <p>Загрузка мероприятий...</p>}
            {error && <p>Не удалось загрузить ленту: {error}</p>}
            {!loading && !error && events.length === 0 && (
              <p>Пока нет ни одного мероприятия</p>
            )}
            {events.map((event) => (
              <EventCard key={event.id} {...event} onOpen={() => openEvent(event.id)} />
            ))}
            </div>
            </>
          )}
        </main>
      </div>
    </div>
  )
}

export default App
