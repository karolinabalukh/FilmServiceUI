import request from 'supertest';
import app from '../app';
import mongoose from 'mongoose';
import axios from 'axios';
import { Review } from '../models/Review';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Reviews API Integration Tests', () => {
    beforeAll(async () => {
        const url = 'mongodb://127.0.0.1:27017/reviews-test';
        await mongoose.connect(url);
    });

    afterEach(async () => {
        await Review.deleteMany({});
    });

    afterAll(async () => {
        await mongoose.connection.close();
    });


    it('should create a new review with status 201', async () => {
        mockedAxios.get.mockResolvedValue({ status: 200 });

        const reviewData = {
            filmId: "15",
            author: "Тестер",
            content: "Тестовий відгук",
            rating: 8
        };

        const response = await request(app)
            .post('/api/reviews')
            .send(reviewData);

        expect(response.status).toBe(201);
        expect(response.body.author).toBe(reviewData.author);
        expect(response.body).toHaveProperty('_id');
    });


    it('GET /api/reviews - should return list of reviews for specific film', async () => {
        await Review.create([
            { filmId: "15", author: "User1", content: "Good", rating: 5, createdAt: new Date('2025-01-01') },
            { filmId: "15", author: "User2", content: "Great", rating: 10, createdAt: new Date('2025-01-02') },
            { filmId: "99", author: "User3", content: "Bad", rating: 1, createdAt: new Date('2025-01-03') }
        ]);

        const response = await request(app)
            .get('/api/reviews')
            .query({ filmId: "15", size: 1, from: 0 });
        expect(response.status).toBe(200);
        expect(Array.isArray(response.body)).toBe(true);
        expect(response.body.length).toBe(1);
        expect(response.body[0].filmId).toBe("15");
        expect(response.body[0].author).toBe("User2");
    });

    it('POST /api/reviews/_counts - should return correct counts using aggregation', async () => {
        await Review.create([
            { filmId: "15", author: "A", content: "C1", rating: 5 },
            { filmId: "15", author: "B", content: "C2", rating: 4 }
        ]);

        const response = await request(app)
            .post('/api/reviews/_counts')
            .send({ entity1Ids: ["15", "99", "100"] });

        expect(response.status).toBe(200);
        expect(response.body["15"]).toBe(2);
        expect(response.body["99"]).toBe(0);
        expect(response.body["100"]).toBe(0);
    });
});