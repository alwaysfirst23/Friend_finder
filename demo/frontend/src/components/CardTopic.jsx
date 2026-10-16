import './CardTopic.css'
import programmingIcon from '../assets/icons/programming-icon.svg'
import calendarIcon from '../assets/icons/calendar-icon-white.svg'
import personsIcon from '../assets/icons/persons-icon.svg'

function CardTopic() {
    return(
        <hesder className = 'card-header'>
            <div className='card-topic'>
                <div className="card-meta__item">
                    <img src={programmingIcon} alt=''/>
                    <span>Программирование</span>
                </div>
            </div>

            <div className='card-meta'>
                <div className="card-meta__item">
                    <img src={calendarIcon} alt=''/>
                    <span>06.07.2026</span>
                </div>
                <div className="card-meta__item">
                    <img src={personsIcon} alt=''/>
                    <span>666</span>
                </div>
            </div>
        </hesder>
    )
}

export default CardTopic