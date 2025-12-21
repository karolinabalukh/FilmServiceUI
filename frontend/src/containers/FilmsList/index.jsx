import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import Typography from 'components/Typography';
import Button from 'components/Button';
import Dialog from 'components/Dialog';
import axios from 'axios';

// Стилі для рядка таблиці
const itemStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '10px',
    borderBottom: '1px solid #eee',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
    minHeight: '50px'
};

const FilmsList = () => {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();

    const page = parseInt(searchParams.get('page') || '0');
    const filterTitle = searchParams.get('title') || '';
    const filterYear = searchParams.get('year') || '';

    const [films, setFilms] = useState([]);
    const [notification, setNotification] = useState(null);
    const [deleteDialog, setDeleteDialog] = useState({ open: false, filmId: null });
    const [deleteError, setDeleteError] = useState(null);
    const [hoveredId, setHoveredId] = useState(null);

    const fetchFilms = async () => {
        try {
            const url = 'http://localhost:8080/api/films';
            const params = { page: page, size: 10 };

            if (filterTitle) params.title = filterTitle;
            if (filterYear) params.year = filterYear;

            const response = await axios.get(url, { params });

            if (response && response.content) {
                setFilms(response.content);
            } else if (Array.isArray(response)) {
                setFilms(response);
            } else {
                setFilms([]);
            }
        } catch (err) {
            console.error('Помилка отримання фільмів', err);
        }
    };

    useEffect(() => {
        fetchFilms();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    const updateSearch = (key, value) => {
        setSearchParams(prev => {
            const newParams = new URLSearchParams(prev);
            if (value) newParams.set(key, value);
            else newParams.delete(key);
            if (key !== 'page') newParams.set('page', 0);
            return newParams;
        });
    };

    const handleDeleteClick = (e, id) => {
        e.stopPropagation();
        setDeleteError(null);
        // Відкриваємо діалог
        setDeleteDialog({ open: true, filmId: id });
    };

    const confirmDelete = async () => {
        setDeleteError(null);
        try {
            await axios.delete(`http://localhost:8080/api/films/${deleteDialog.filmId}`);

            setFilms(films.filter(f => f.id !== deleteDialog.filmId));

            setNotification("Фільм успішно видалено!");
            setTimeout(() => setNotification(null), 3000);

            setDeleteDialog({ open: false, filmId: null });

        } catch (err) {
            console.error(err);
            // 3.2. Якщо помилка - залишаємо відкритим і показуємо текст
            setDeleteError("Не вдалося видалити. Можливо, фільм використовується.");
        }
    };

    const closeDialog = () => {
        setDeleteDialog({ open: false, filmId: null });
    };

    return (
        <div style={{ padding: '20px', position: 'relative' }}>

            {/* Повідомлення про успіх */}
            {notification && (
                <div style={{
                    position: 'fixed',
                    top: '80px',
                    left: '50%',
                    transform: 'translateX(-50%)',
                    background: '#28a745',
                    color: 'white',
                    padding: '10px 20px',
                    borderRadius: '5px',
                    boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                    zIndex: 2000,
                    fontWeight: 'bold'
                }}>
                    ✅ {notification}
                </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <Typography variant="h4">Фільми</Typography>
                <Button onClick={() => navigate('/films/create')}>
                    + Додати фільм
                </Button>
            </div>

            {/* Фільтри */}
            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', background: '#f5f5f5', padding: '15px', borderRadius: '8px', flexWrap: 'wrap' }}>
                <input
                    placeholder="Пошук за назвою..."
                    value={filterTitle}
                    onChange={(e) => updateSearch('title', e.target.value)}
                    style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px', flex: 1 }}
                />
                <input
                    type="number"
                    placeholder="Рік..."
                    value={filterYear}
                    onChange={(e) => updateSearch('year', e.target.value)}
                    style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px', width: '100px' }}
                />
                <Button onClick={() => setSearchParams({})}>
                    Скинути
                </Button>
            </div>

            {/* Список */}
            <div>
                {films.length > 0 ? films.map(film => (
                    <div
                        key={film.id}
                        style={{
                            ...itemStyle,
                            backgroundColor: hoveredId === film.id ? '#f0f0f0' : 'transparent'
                        }}
                        onClick={() => navigate(`/films/${film.id}`)}
                        onMouseEnter={() => setHoveredId(film.id)}
                        onMouseLeave={() => setHoveredId(null)}
                    >
                        <div>
                            <strong style={{fontSize: '1.1rem'}}>{film.title}</strong>
                            <div style={{color: '#666', marginTop: '5px'}}>
                                <span>{film.year} рік</span>
                                <span style={{marginLeft: '20px'}}>⭐ {film.rating}</span>
                            </div>
                        </div>

                        {/* 3.2. Кнопка "Урна" з'являється при наведенні */}
                        {hoveredId === film.id && (
                            <button
                                onClick={(e) => handleDeleteClick(e, film.id)}
                                style={{
                                    cursor: 'pointer',
                                    backgroundColor: '#dc3545',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    padding: '6px 12px',
                                    fontWeight: 'bold',
                                    boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
                                }}
                            >
                                🗑
                            </button>
                        )}
                    </div>
                )) : (
                    <div style={{textAlign: 'center', padding: '40px', color: '#999'}}>
                        Фільмів не знайдено
                    </div>
                )}
            </div>

            <div style={{display: 'flex', justifyContent: 'center', gap: '15px'}}>
            <Button disabled={page === 0} onClick={() => updateSearch('page', page - 1)}>
                    ← Назад
                </Button>
                <span style={{ alignSelf: 'center', fontWeight: 'bold' }}>
                    Сторінка {page + 1}
                </span>
                <Button onClick={() => updateSearch('page', page + 1)}>
                    Вперед →
                </Button>
            </div>

            {/* 👇👇👇 ОНОВЛЕНИЙ ДІАЛОГ З КНОПКАМИ 👇👇👇 */}
            {deleteDialog.open && (
                <Dialog
                    title=" " // Прибираємо стандартний заголовок, зробимо свій красивий
                    open={deleteDialog.open}
                    onClose={closeDialog}
                >
                    <div style={{ textAlign: 'center', padding: '10px 20px' }}>

                        {/* Велика іконка попередження */}
                        <div style={{ fontSize: '3rem', marginBottom: '15px' }}>
                            🗑️
                        </div>

                        <h3 style={{ margin: '0 0 10px 0', color: '#333' }}>
                            Видалення фільму
                        </h3>

                        <div style={{ fontSize: '1rem', color: '#666', marginBottom: '25px', lineHeight: '1.5' }}>
                            Ви впевнені, що хочете видалити цей фільм?<br />
                            <strong>Цю дію не можна буде скасувати.</strong>
                        </div>

                        {/* Блок помилки, якщо щось пішло не так */}
                        {deleteError && (
                            <div style={{
                                background: '#ffe6e6',
                                color: '#d32f2f',
                                padding: '10px',
                                borderRadius: '4px',
                                marginBottom: '20px',
                                fontSize: '0.9rem'
                            }}>
                                ⚠ {deleteError}
                            </div>
                        )}

                        {/* КНОПКИ ТАК / НІ */}
                        <div style={{ display: 'flex', justifyContent: 'center', gap: '15px' }}>
                            {/* Кнопка "Скасувати" */}
                            <button
                                onClick={closeDialog}
                                style={{
                                    padding: '10px 20px',
                                    border: '1px solid #ccc',
                                    borderRadius: '5px',
                                    background: 'white',
                                    color: '#333',
                                    cursor: 'pointer',
                                    fontSize: '1rem',
                                    transition: 'background 0.2s'
                                }}
                                onMouseOver={(e) => e.target.style.background = '#f5f5f5'}
                                onMouseOut={(e) => e.target.style.background = 'white'}
                            >
                                Скасувати
                            </button>

                            {/* Кнопка "Видалити" */}
                            <button
                                onClick={confirmDelete}
                                style={{
                                    padding: '10px 20px',
                                    border: 'none',
                                    borderRadius: '5px',
                                    background: '#dc3545', // Червоний колір небезпеки
                                    color: 'white',
                                    cursor: 'pointer',
                                    fontWeight: 'bold',
                                    fontSize: '1rem',
                                    boxShadow: '0 2px 5px rgba(220, 53, 69, 0.3)'
                                }}
                                onMouseOver={(e) => e.target.style.opacity = '0.9'}
                                onMouseOut={(e) => e.target.style.opacity = '1'}
                            >
                                Так, видалити
                            </button>
                        </div>
                    </div>
                </Dialog>
            )}
        </div>
    );
};

export default FilmsList;