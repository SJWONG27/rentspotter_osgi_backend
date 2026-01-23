package rentalHistoryAnalytic.service;

public interface RatingValidationService {
    boolean isValidComment(String comment);
    boolean isSpamming(String userId);
    boolean isSentimentConsistent(int score, String comment);
}


