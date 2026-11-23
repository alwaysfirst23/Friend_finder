const formatDate = (isoString) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleDateString('ru-RU')
}

export async function fetchEvents({ categoryId, typeCode, dateFrom, dateTo, search, page = 0, size = 20 } = {}) {
  const params = new URLSearchParams({ page, size })
  if (categoryId) params.set('categoryId', categoryId)
  if (typeCode) params.set('typeCode', typeCode)
  if (dateFrom) params.set('dateFrom', dateFrom)
  if (dateTo) params.set('dateTo', dateTo)
  if (search) params.set('search', search)

  const response = await fetch(`/api/events?${params.toString()}`)
  if (!response.ok) {
    throw new Error(`Ошибка загрузки мероприятий: ${response.status}`)
  }

  const data = await response.json()
  return {
    items: (data.content || []).map((e) => ({
      id: e.id,
      topic: e.categoryName,
      date: formatDate(e.startsAt),
      members: e.membersCount,
      author: e.authorNickname,
      text: e.description || e.title,
      title: e.title,
      address: e.address,
      typeCode: e.typeCode,
    })),
    totalPages: data.totalPages ?? 0,
    totalElements: data.totalElements ?? 0,
  }
}

export async function fetchEventDetails(id, { signal } = {}) {
  const response = await fetch(`/api/events/${encodeURIComponent(id)}`, { signal })
  if (!response.ok) {
    throw new Error(response.status === 404
      ? 'Мероприятие не найдено'
      : `Ошибка загрузки мероприятия: ${response.status}`)
  }

  const event = await response.json()
  return {
    id: event.id,
    topic: event.categoryName,
    author: event.authorNickname,
    title: event.title,
    text: event.description || event.title,
    description: event.description,
    date: formatDate(event.startsAt),
    time: event.startsAt ? new Date(event.startsAt).toLocaleTimeString('ru-RU', {
      hour: '2-digit', minute: '2-digit',
    }) : '',
    address: event.address,
    members: event.membersCount,
    capacity: event.maxParticipants,
    typeCode: event.typeCode,
  }
}

export async function fetchCategories() {
  const response = await fetch('/api/events/categories')
  if (!response.ok) {
    throw new Error(`Ошибка загрузки категорий: ${response.status}`)
  }
  return response.json()
}
