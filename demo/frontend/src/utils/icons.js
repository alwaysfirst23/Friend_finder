const modules = import.meta.glob("../assets/icons-tag/*.svg", {
  eager: true,
  query: "?url",
  import: "default",
});

const icons = {};
for (const path in modules) {
  const name = path.split("/").pop().replace(".svg", "");
  icons[name] = modules[path];
}

const topicIcons = {
    'Кафе':'cafe',
    'Клубы':'club',
    'Общение':'communication',
    'Готовка':'cooking',
    'Фестивали':'festival',
    'Рыбалка':'fishing',
    'Музеи':'museum',
    'Музыка':'music',
    'Домашние питомцы':'pets',
    'Фотосъёмки':'photography',
    'Программирование': 'programming',
    'Рестораны':'restaurant',
    'Бег':'running',
    'Шоппинг':'shopping',
    'Спорт': 'sport',
    'Учёба':'studies',
    'Театры':'theater',
    'Видео игры':'videogames',
    'Волонтёрство':'volunteering',
    'Прогулки':'walks',
}

export function getTopicIcon(topic) {
  const key = topicIcons[topic]
  return icons[key] || icons['default']   // запасная иконка
}

export default icons;