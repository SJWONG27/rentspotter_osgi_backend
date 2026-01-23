package rentalHistoryAnalytic.service;

import rentalHistoryAnalytic.model.RentalRecord;

import java.util.List;

public interface HistoryService {
    List<RentalRecord> getTenantHistory(String tenantId);
    List<RentalRecord> getLandlordPortfolio(String landlordId);
    RentalRecord getRecordById(String id);
    void saveRecord(RentalRecord record);
}


