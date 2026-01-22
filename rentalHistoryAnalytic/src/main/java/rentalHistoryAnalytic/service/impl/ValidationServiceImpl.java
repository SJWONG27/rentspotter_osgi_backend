package rentalHistoryAnalytic.service.impl;

import org.osgi.service.component.annotations.Component;
import rentalHistoryAnalytic.service.RatingValidationService;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = RatingValidationService.class)
public class ValidationServiceImpl implements RatingValidationService {

    // In-memory store for users' last post times
    private final Map<String, Instant> userLastPostTime = new ConcurrentHashMap<>();

    // Cooldown time: 60 seconds
    private static final long COOLDOWN_SECONDS = 60;

    // Profanity Dataset
    private static final List<String> BANNED_WORDS = Arrays.asList(
            "spam", "fake", "scam", "idiot", "stupid", "dumb", "useless"
    );

    // Sentiment Dataset
    private static final List<String> NEGATIVE_SENTIMENT_WORDS = Arrays.asList(
            "terrible", "horrible", "awful", "worst", "hate", "disaster"
    );

    @Override
    public boolean isSpamming(String userId) {
        Instant now = Instant.now();
        Instant lastPost = userLastPostTime.get(userId);
        if (lastPost == null) {
            // First time posting, record time and allow
            userLastPostTime.put(userId, now);
            return false;
        }
        // Check difference in seconds
        long secondsElapsed = now.getEpochSecond() - lastPost.getEpochSecond();
        if (secondsElapsed < COOLDOWN_SECONDS) {
            return true;
        }
        // Update time and allow
        userLastPostTime.put(userId, now);
        return false;
    }

    @Override
    public boolean isValidComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            return false;
        }
        String lowerCaseComment = comment.toLowerCase();
        // Check for profanity
        for (String word : BANNED_WORDS) {
            if (lowerCaseComment.contains(word)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSentimentConsistent(int score, String comment) {
        String lowerCaseComment = comment.toLowerCase();
        // If high score (4-5) but contains purely negative words
        if (score >= 4) {
            for (String word : NEGATIVE_SENTIMENT_WORDS) {
                if (lowerCaseComment.contains(word)) {
                    return false;
                }
            }
        }
        return true;
    }
}