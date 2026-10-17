import EventCard from './components/EventCard'
import MainHeader from './components/MainHeader'
import { events } from '../tests/data/events'
import './App.css'

function App() {
  return (
    <>
      <MainHeader />
      
      <div className="events-list">
        {events.map((event) => (
          <EventCard key={event.id} {...event} />
        ))}
      </div>
    </>
  )
}

export default App