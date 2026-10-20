import './MainHeader.css'
import Avatar from '../assets/images/avatar2.svg'
import locationIcon from '../assets/icons/location-icon-black.svg'
import searchIcon from '../assets/icons/loupe-icon-black.svg'
import logo from '../assets/logo/logo.svg'

function MainHeader() {
    return(
        <header className='main-header'>
            <img className='main-avatar' src={Avatar} alt='Аватар пользователя' />

            <div className='header-center'>
                <div className='search'>
                    <input 
                    className='search__input'
                    type='search'
                    placeholder='Поиск'
                    aria-label='Поиск'
                    />
                    <button className='search__button' type='button' aria-label='Найти'>
                        <img src={searchIcon} alt='' />
                    </button>
                </div>

            

                <div className='location'>
                    <img src={locationIcon} alt='Иконка локации' />
                    <span>Самара</span>
                </div>
            </div>

            <div className='logo'>
                <img src={logo} alt='Логотип' />
            </div>
        </header>
    )
}

export default MainHeader