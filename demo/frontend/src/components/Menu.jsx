import { useState } from 'react'
import './Menu.css'
import homeIcon from '../assets/icons/home-icon-green.svg'
import homeIconActive from '../assets/icons/home-icon-white.svg'
import personIcon from '../assets/icons/person-icon-green.svg'
import personIconActive from '../assets/icons/person-icon-white.svg'
import chatIcon from '../assets/icons/chat-icon-green.svg'
import chatIconActive from '../assets/icons/chat-icon-white.svg'
import calendarIcon from '../assets/icons/calendar-icon-green.svg'
import calendarIconActive from '../assets/icons/calendar-icon-white.svg'
import settingsIcon from '../assets/icons/settings-icon-green.svg'
import settingsIconActive from '../assets/icons/settings-icon-white.svg'

// Все пункты меню в одном массиве: чтобы добавить пункт, достаточно добавить сюда строку
const menuItems = [
    { id: 'home', label: 'Главная', icon: homeIcon, iconActive: homeIconActive },
    { id: 'profile', label: 'Профиль', icon: personIcon, iconActive: personIconActive },
    { id: 'chats', label: 'Чаты', icon: chatIcon, iconActive: chatIconActive },
    { id: 'events', label: 'Мои события', icon: calendarIcon, iconActive: calendarIconActive },
    { id: 'settings', label: 'Настройки', icon: settingsIcon, iconActive: settingsIconActive },
]

function Menu() {
    // id выбранного пункта; при клике он меняется, и меню перерисовывается
    const [activeId, setActiveId] = useState('home')

    return (
        <nav className='navigation'>
            {menuItems.map((item) => {
                const isActive = item.id === activeId

                return (
                    <button
                        key={item.id}
                        type='button'
                        className={isActive ? 'navigation__item active' : 'navigation__item'}
                        onClick={() => setActiveId(item.id)}
                    >
                        <span className='navigation__icon'>
                            <img src={isActive ? item.iconActive : item.icon} alt='' />
                        </span>
                        <span>{item.label}</span>
                    </button>
                )
            })}
        </nav>
    )
}

export default Menu