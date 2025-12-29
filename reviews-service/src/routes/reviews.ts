import { Router } from 'express';
import { Review } from '../models/Review';
import axios from 'axios';

const router = Router();

router.post('/', async (req, res) => {
    try {
        const {filmId, author, content, rating} = req.body;

        if (!filmId || !author || !content || !rating) {
            return res.status(400).json({ error: 'Missing required fields' });
        }
        try {
            await axios.get(`http://localhost:8080/api/films/${filmId}`);
        } catch (error) {
            return res.status(404).json({ error: 'Film with such ID does not exist' });
        }

        const newReview = new Review({filmId, author, content, rating});
        await newReview.save();
        res.status(201).json(newReview);

    } catch (err) {
        res.status(500).json({ error: 'Internal error'});
    }
});

router.get('/', async (req, res) => {
    try {
        const {filmId, size=5, from=0} = req.query;
        if (!filmId) {
            return res.status(400).json({error: 'filmId is required'});
        }
        const reviews = await Review.find({filmId: filmId as string})
            .sort({createdAt: -1})
            .skip(Number(from))
            .limit(Number(size));
        res.json(reviews);
    } catch (err) {
        res.status(500).json({error: 'internal server error'});
    }
});

router.post('/_counts', async (req, res) => {
    try{
        const {entity1Ids} = req.body;
        const counts = await Review.aggregate([
            { $match: { filmId: { $in: entity1Ids } } },
            { $group: { _id: "$filmId", count: { $sum: 1 } } }
        ]);
        const result = entity1Ids.reduce((acc: any, id: string) => {
            const found = counts.find(c => c._id === id);
            acc[id] = found ? found.count : 0;
            return acc;
        }, {} );
        res.json(result);
    } catch (err) {
        res.status(500).json({error:'internal server error'});
    }
});

export default router;