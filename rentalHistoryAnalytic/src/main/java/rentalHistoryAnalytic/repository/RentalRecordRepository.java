package rentalHistoryAnalytic.repository;

import rentalHistoryAnalytic.model.RentalRecord;

import java.util.List;
import java.util.Optional;

public interface RentalRecordRepository {
    void save(RentalRecord rentalRecord);
    List<RentalRecord> findByTenantId(String tenantId);
    List<RentalRecord> findByLandlordId(String landlordId);
    Optional<RentalRecord> findById(String id);
}

