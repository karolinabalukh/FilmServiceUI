import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Button from 'components/Button';
import Typography from 'components/Typography';
import './index.css';

const GENRES = [
    "Action", "Comedy", "Drama", "Fantasy", "Horror",
    "Mystery", "Romance", "Thriller", "Western", "Sci-Fi", "Crime", "Animation"
];

const FilmsDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isCreateMode = id === 'create';

    const [isEditing, setIsEditing] = useState(isCreateMode);

    const [values, setValues] = useState({
        title: '',
        year: '',
        rating: '',
        genre: '',
        duration: '',
        description: '',
        directorId: ''
    });

    const [errors, setErrors] = useState({});

    const [originalValues, setOriginalValues] = useState({});
    const [message, setMessage] = useState(null);

    useEffect(() => {
        if (!isCreateMode) {
            fetchFilmData();
        }
        else {
            setValues(prev => ({ ...prev, genre: GENRES[0] }));
        }
    }, [id]);

    const fetchFilmData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/films/${id}`);
            const data = response;

            const dirId = data.director && data.director.id ? data.director.id : '';

            const formData = {
                title: data.title || '',
                year: data.year || '',
                rating: data.rating || '',
                genre: data.genre || GENRES[0],
                duration: data.duration || '',
                description: data.description || '',
                directorId: dirId
            };

            setValues(formData);
            setOriginalValues(formData);
        } catch (error) {
            console.error("Помилка завантаження:", error);
            setMessage({ type: 'error', text: 'Не вдалося завантажити дані фільму' });
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setValues(prev => ({ ...prev, [name]: value }));

        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: null }));
        }
    };

    const handleEditClick = () => {
        setIsEditing(true);
        setMessage(null);
    };

    const handleCancelClick = () => {
        if (isCreateMode) {
            navigate(-1);
        } else {
            setValues(originalValues);
            setIsEditing(false);
            setErrors({});
            setMessage(null);
        }
    };

    const validate = () => {
        const newErrors = {};
        const currentYear = new Date().getFullYear();

        //Назва
        if (!values.title || values.title.trim() === '') {
            newErrors.title = "Назва не може бути порожньою";
        }

        //Рік
        if (!values.year) {
            newErrors.year = "Вкажіть рік";
        } else if (values.year < 1895 || values.year > currentYear + 5) {
            newErrors.year = `Рік має бути між 1895 та ${currentYear + 5}`;
        }

        //Рейтинг
        if (values.rating === '' || values.rating === null) {
            newErrors.rating = "Вкажіть рейтинг";
        } else if (values.rating < 0 || values.rating > 10) {
            newErrors.rating = "Рейтинг має бути від 0 до 10";
        }
        // Режисер
        if (!values.directorId) {
            newErrors.directorId = "ID режисера обов'язковий";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSaveClick = async () => {
        if (!validate()) {
            setMessage({ type: 'error', text: 'Будь ласка, виправте помилки у формі' });
            return;
        }

        try {
            if (isCreateMode) {
                await axios.post('http://localhost:8080/api/films', values);
                navigate(-1);
            } else {
                await axios.put(`http://localhost:8080/api/films/${id}`, values);
                setOriginalValues(values);
                setIsEditing(false);
                setMessage({ type: 'success', text: 'Фільм успішно збережено!' });
                setTimeout(() => setMessage(null), 3000);
            }
        } catch (error) {
            console.error(error);
            setMessage({ type: 'error', text: 'Помилка при збереженні (серверна помилка).' });
        }
    };

    const getInputStyle = (fieldName) => ({
        width: '100%',
        padding: '8px',
        borderRadius: '4px',
        border: errors[fieldName] ? '1px solid #dc3545' : '1px solid #ccc',
        backgroundColor: errors[fieldName] ? '#fff8f8' : 'white'
    });

    return (
        <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>

            <div style={{ marginBottom: '20px' }}>
                <Button onClick={() => navigate(-1)}>← Назад до списку</Button>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <Typography variant="h4">
                    {isCreateMode ? 'Створення фільму' : `Фільм: ${values.title}`}
                </Typography>
                {!isEditing && !isCreateMode && (
                    <button onClick={handleEditClick} style={{ border: 'none', background: 'none', cursor: 'pointer', fontSize: '1.5rem' }}>✏️</button>
                )}
            </div>

            {message && (
                <div style={{
                    padding: '10px',
                    marginBottom: '20px',
                    borderRadius: '4px',
                    color: message.type === 'error' ? '#721c24' : '#155724',
                    backgroundColor: message.type === 'error' ? '#f8d7da' : '#d4edda',
                    border: `1px solid ${message.type === 'error' ? '#f5c6cb' : '#c3e6cb'}`
                }}>
                    {message.text}
                </div>
            )}

            <div style={{ background: '#f9f9f9', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>

                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>Назва</label>
                    {isEditing ? (
                        <>
                            <input
                                name="title"
                                value={values.title}
                                onChange={handleChange}
                                style={getInputStyle('title')}
                            />
                            {errors.title && <div style={{color: '#dc3545', fontSize: '0.85rem', marginTop: '5px'}}>{errors.title}</div>}
                        </>
                    ) : <span>{values.title}</span>}
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>Рік випуску</label>
                    {isEditing ? (
                        <>
                            <input
                                name="year"
                                type="number"
                                value={values.year}
                                onChange={handleChange}
                                style={getInputStyle('year')}
                            />
                            {errors.year && <div style={{color: '#dc3545', fontSize: '0.85rem', marginTop: '5px'}}>{errors.year}</div>}
                        </>
                    ) : <span>{values.year}</span>}
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>Рейтинг</label>
                    {isEditing ? (
                        <>
                            <input
                                name="rating"
                                type="number"
                                step="0.1"
                                value={values.rating}
                                onChange={handleChange}
                                style={getInputStyle('rating')}
                            />
                            {errors.rating && <div style={{color: '#dc3545', fontSize: '0.85rem', marginTop: '5px'}}>{errors.rating}</div>}
                        </>
                    ) : <span>{values.rating}</span>}
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>Жанр</label>
                    {isEditing ? (
                        <select
                            name="genre"
                            value={values.genre}
                            onChange={handleChange}
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', background: 'white' }}
                        >
                            {GENRES.map(g => (
                                <option key={g} value={g}>{g}</option>
                            ))}
                        </select>
                    ) : <span>{values.genre}</span>}
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>ID Режисера</label>
                    {isEditing ? (
                        <>
                            <input
                                name="directorId"
                                type="number"
                                placeholder="Введіть ID (напр. 1)"
                                value={values.directorId}
                                onChange={handleChange}
                                style={getInputStyle('directorId')}
                            />
                            {errors.directorId && <div style={{color: '#dc3545', fontSize: '0.85rem', marginTop: '5px'}}>{errors.directorId}</div>}
                        </>
                    ) : <span style={{color: '#666'}}>ID: {values.directorId}</span>}
                </div>

                {isEditing && (
                    <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
                        <Button onClick={handleSaveClick}>
                            {isCreateMode ? 'Створити' : 'Зберегти'}
                        </Button>
                        <button
                            onClick={handleCancelClick}
                            style={{ padding: '10px 20px', border: '1px solid #ccc', background: 'white', cursor: 'pointer', borderRadius: '4px' }}
                        >
                            Скасувати
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default FilmsDetails;