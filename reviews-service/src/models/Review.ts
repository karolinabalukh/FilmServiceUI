import { Schema, model } from 'mongoose';

const reviewSchema = new Schema({
    filmId: { type: String, required: true },
    author: { type: String, required: true },
    content: { type: String, required: true },
    rating: { type: Number, required: true, min: 1, max: 10 },
    createdAt: { type: Date, default: Date.now }
});

export const Review = model('Review', reviewSchema);