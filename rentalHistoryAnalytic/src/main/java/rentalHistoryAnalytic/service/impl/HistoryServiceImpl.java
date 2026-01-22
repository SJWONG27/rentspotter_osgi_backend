package rentalHistoryAnalytic.service.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rentalHistoryAnalytic.model.RentalRecord;
import rentalHistoryAnalytic.repository.RentalRecordRepository;
import rentalHistoryAnalytic.service.HistoryService;

import java.util.List;

@Component(service = HistoryService.class)
public class HistoryServiceImpl implements HistoryService {

    @Reference
    private RentalRecordRepository recordRepository;

    @Override
    public void saveRecord(RentalRecord record) {
        recordRepository.save(record);
    }

    @Override
    public List<RentalRecord> getTenantHistory(String tenantId) {
        return recordRepository.findByTenantId(tenantId);
    }

    @Override
    public List<RentalRecord> getLandlordPortfolio(String landlordId) {
        return recordRepository.findByLandlordId(landlordId);
    }

    @Override
    public RentalRecord getRecordById(String id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with ID: " + id));
    }
}
