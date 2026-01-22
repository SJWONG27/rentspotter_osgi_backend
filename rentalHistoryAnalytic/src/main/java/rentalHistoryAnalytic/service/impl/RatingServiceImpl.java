package rentalHistoryAnalytic.service.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rentalHistoryAnalytic.model.Rating;
import rentalHistoryAnalytic.repository.RatingRepository;
import rentalHistoryAnalytic.service.RatingService;
import rentalHistoryAnalytic.service.RatingValidationService;

import java.util.List;
import java.util.Optional;

@Component(service = RatingService.class)
public class RatingServiceImpl implements RatingService {
    @Reference
    private RatingRepository ratingRepository;
    @Reference
    private RatingValidationService validationService;
    @Override
    public Rating submitRating(Rating ratingRequestDTO) {
        // Guard against vulgar words
        if (!validationService.isValidComment(ratingRequestDTO.getComment())) {
            throw new RuntimeException("Rating rejected: Profanity detected.");
        }
        // Guard against spamming
        if (validationService.isSpamming(ratingRequestDTO.getRaterId())) {
            throw new RuntimeException("Rating rejected: You are rating too frequently.");
        }
        // Guard against sentiment inconsistency
        if (!validationService.isSentimentConsistent(ratingRequestDTO.getScore(), ratingRequestDTO.getComment())) {
            throw new RuntimeException("Review rejected: Your comment contradicts your high rating.");
        }
        // Ensure score is strictly 1-5, clamp it if necessary
        int finalScore = Math.max(1, Math.min(5, ratingRequestDTO.getScore()));

        // Transform DTO to entity and save
        Rating rating = new Rating();
        rating.setRaterId(ratingRequestDTO.getRaterId());
        rating.setRatedUserId(ratingRequestDTO.getRatedUserId());
        rating.setComment(ratingRequestDTO.getComment());
        rating.setScore(finalScore);
        return ratingRepository.save(rating);
    }

    @Override
    public double getTrustScore(String userId) {
        // Weighted calculation
        Optional<List<Rating>> ratings = Optional.ofNullable(ratingRepository.findByRatedUserId(userId));
        if (ratings.isEmpty()) return 0.0;
        double totalScore = 0;
        for (Rating r : ratings.get()) {
            totalScore += r.getScore();
        }
        // Round to 1 decimal place
        double average = totalScore / ratings.get().size();
        return Math.round(average * 10.0) / 10.0;
    }
}