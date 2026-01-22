package rentalHistoryAnalytic.service;

import rentalHistoryAnalytic.model.Rating;

public interface RatingService {
    Rating submitRating(Rating rating);
    double getTrustScore(String userId); // Returns average score
}


