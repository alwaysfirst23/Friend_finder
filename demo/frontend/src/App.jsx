import EventCard from './components/EventCard'
import { events } from '../tests/data/events'
import './App.css'

function App() {
  return (
    <div className="events-list">
      {events.map((event) => (
        <EventCard key={event.id} {...event} />
      ))}
    </div>
  )
}

export default App