import app from './app';
import mongoose from 'mongoose';

const PORT = 4000;
const MONGO_URI = 'mongodb://127.0.0.1:27017/reviews-service'

mongoose.connect(MONGO_URI)
    .then(() => {
        console.log('connect to MongoDB');
        app.listen(PORT, ()=> {
            console.log(`reviews service running on port ${PORT}`);
        });
    })
    .catch(err => console.error('mongoDB connecting error:', err));
