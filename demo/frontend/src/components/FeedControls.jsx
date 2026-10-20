import { useState } from 'react'
import './FeedControls.css'
import filterIcon from '../assets/icons/filter-icon.svg'
import plusIcon from '../assets/icons/plus-icon.svg'

const tabs = [
    { id: 'companies', label: 'Компании' },
    { id: 'posters', label: 'Афиши' },
]

function FeedControls() {
    // какая вкладка выбрана сейчас
    const [activeTab, setActiveTab] = useState('companies')

    return (
        <div className='feed-controls'>
            <div className='feed-tabs'>
                {tabs.map((tab) => (
                    <button
                        key={tab.id}
                        type='button'
                        className={tab.id === activeTab ? 'feed-tab active' : 'feed-tab'}
                        aria-pressed={tab.id === activeTab}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>

            <button
                type='button'
                className='round-button round-button--light'
                aria-label='Фильтры'
            >
                <img src={filterIcon} alt='' />
            </button>

            <button
                type='button'
                className='round-button round-button--dark'
                aria-label='Создать событие'
            >
                <img src={plusIcon} alt='' />
            </button>
        </div>
    )
}

export default FeedControls