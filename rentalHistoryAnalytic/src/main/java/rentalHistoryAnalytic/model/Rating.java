package rentalHistoryAnalytic.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating {
    private String id;
    private String raterId;
    private String ratedUserId;
    private Integer score;
    private String comment;
}

