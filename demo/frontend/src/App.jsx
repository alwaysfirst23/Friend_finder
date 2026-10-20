import MainHeader from './components/MainHeader'
import EventCard from './components/EventCard'
import FeedControls from './components/FeedControls'
import { events } from '../tests/data/events'
import './App.css'
import Menu from './components/Menu'
import mountains from './assets/images/mount.svg'

function App() {
  return (
    <div className="app">
      <MainHeader />

      <div className="layout">
        <aside className="sidebar">
          <Menu />
          <img className="sidebar__mountains" src={mountains} alt="Вместе интереснее" />
        </aside>

        <main className="content">
          <FeedControls />

          <div className="events-list">
            {events.map((event) => (
              <EventCard key={event.id} {...event} />
            ))}
          </div>
        </main>
      </div>
    </div>
  )
}

export default App