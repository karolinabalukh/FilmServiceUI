import express from 'express';
import reviewsRouter from './routes/reviews';

const app = express();

app.use(express.json());
app.use('/api/reviews', reviewsRouter);

app.get('/health', (_, res) => {
    res.json({ status: 'OK' });
});

export default app;
