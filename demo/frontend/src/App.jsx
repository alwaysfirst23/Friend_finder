import MainHeader from './components/MainHeader'
import EventCard from './components/EventCard'
import { events } from '../tests/data/events'
import './App.css'
import Menu from './components/Menu'

function App() {
  return (
    <>
      <MainHeader />

      <div className="layout">
        <aside className="sidebar">
          <Menu />
        </aside>

        <main className="content">
          <div className="feed-controls">
            {/* тут будут: вкладки, фильтр, создать событие */}
          </div>

          <div className="events-list">
            {events.map((event) => (
              <EventCard key={event.id} {...event} />
            ))}
          </div>
        </main>
      </div>
    </>
  )
}

export default App