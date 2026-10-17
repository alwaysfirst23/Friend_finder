import './CardTopic.css'
import { getTopicIcon } from '../utils/icons'
import calendarIcon from '../assets/icons/calendar-icon-white.svg'
import personsIcon from '../assets/icons/persons-icon.svg'

function CardTopic({ topic, date, members }) {
    return(
        <header className = 'card-header'>
            <div className='card-topic'>
                <div className="card-meta__item">
                    <img src={getTopicIcon(topic)} alt=''/>
                    <span>{topic}</span>
                </div>
            </div>

            <div className='card-meta'>
                <div className="card-meta__item">
                    <img src={calendarIcon} alt=''/>
                    <span>{date}</span>
                </div>
                <div className="card-meta__item">
                    <img src={personsIcon} alt=''/>
                    <span>{members}</span>
                </div>
            </div>
        </header>
    )
}

export default CardTopic