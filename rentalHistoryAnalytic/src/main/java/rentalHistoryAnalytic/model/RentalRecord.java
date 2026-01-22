package rentalHistoryAnalytic.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalRecord {
    private String id;
    private String tenantId;
    private String landlordId;
    private String propertyId;
    private String propertyName;
    private String startDate;
    private String endDate;
    private Double rentalAmount;
}

