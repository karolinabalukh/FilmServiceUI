import React from 'react';
import { Routes, Route } from 'react-router-dom';
import PageContainer from './components/PageContainer';
import FilmsList from '../containers/FilmsList';
import FilmsDetails from '../containers/FilmsDetails';

const Films = () => (
    <PageContainer>
        <Routes>
            <Route path="/" element={<FilmsList />} />
            <Route path=":id" element={<FilmsDetails />} />
        </Routes>
    </PageContainer>
);

export default Films;