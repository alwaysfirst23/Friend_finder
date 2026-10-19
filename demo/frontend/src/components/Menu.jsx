import './Menu.css'
import homeIcon from '../assets/icons/home-icon-green.svg'
import personIcon from '../assets/icons/person-icon-green.svg'
import chatIcon from '../assets/icons/chat-icon-green.svg'
import calendarIcon from '../assets/icons/calendar-icon-green.svg'
import settingsIcon from '../assets/icons/settings-icon-green.svg'

function Menu() {
    return(
        <div className='navigation'>
            <p className='navigation__input active'>
                <img src={homeIcon} alt='' />
                <span>Главная</span>
            </p>

            <p className='navigation__input'>
                <img src={personIcon} alt='' />
                <span>Профиль</span>
            </p>

            <p className='navigation__input'>
                <img src={chatIcon} alt='' />
                <span>Чаты</span>
            </p>

            <p className='navigation__input'>
                <img src={calendarIcon} alt='' />
                <span>Мои события</span>
            </p>

            <p className='navigation__input'>
                <img src={settingsIcon} alt='' />
                <span>Настройки</span>
            </p>
        </div>
    )
}

export default Menu