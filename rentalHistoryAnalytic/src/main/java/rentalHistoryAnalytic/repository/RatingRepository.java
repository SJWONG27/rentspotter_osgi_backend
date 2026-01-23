package rentalHistoryAnalytic.repository;
import rentalHistoryAnalytic.model.Rating;
import java.util.List;

public interface RatingRepository  {
    Rating save(Rating rating);
    List<Rating> findByRatedUserId(String ratedUserId);
}



