package rentalHistoryAnalytic.repository.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rentalHistoryAnalytic.MongoClientProvider;
import rentalHistoryAnalytic.model.RentalRecord;
import rentalHistoryAnalytic.repository.RentalRecordRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component(service = RentalRecordRepository.class)
public class RentalRecordRepositoryImpl implements RentalRecordRepository {

    @Reference
    private MongoClientProvider mongoClientProvider;

    private MongoCollection<Document> getCollection() {
        MongoClient client = mongoClientProvider.getClient();
        MongoDatabase database = client.getDatabase("rentspotter_db");
        return database.getCollection("rental_records");
    }

    private RentalRecord mapDocumentToRentalRecord(Document doc) {
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setId(doc.getObjectId("_id").toString());
        rentalRecord.setTenantId(doc.getString("tenantId"));
        rentalRecord.setLandlordId(doc.getString("landlordId"));
        rentalRecord.setPropertyId(doc.getString("propertyId"));
        rentalRecord.setPropertyName(doc.getString("propertyName"));
        rentalRecord.setStartDate(doc.getString("startDate"));
        rentalRecord.setEndDate(doc.getString("endDate"));
        rentalRecord.setRentalAmount(doc.getDouble("rentalAmount"));
        return rentalRecord;
    }

    private Document mapRentalRecordToDocument(RentalRecord rentalRecord) {
        return new Document("tenantId", rentalRecord.getTenantId())
                .append("landlordId", rentalRecord.getLandlordId())
                .append("propertyId", rentalRecord.getPropertyId())
                .append("propertyName", rentalRecord.getPropertyName())
                .append("startDate", rentalRecord.getStartDate())
                .append("endDate", rentalRecord.getEndDate())
                .append("rentalAmount", rentalRecord.getRentalAmount());
    }

    @Override
    public void save(RentalRecord rentalRecord) {
        Document doc = mapRentalRecordToDocument(rentalRecord);
        getCollection().insertOne(doc);
        rentalRecord.setId(doc.getObjectId("_id").toString());
    }

    @Override
    public List<RentalRecord> findByTenantId(String tenantId) {
        List<RentalRecord> rentalRecords = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("tenantId", tenantId))) {
            rentalRecords.add(mapDocumentToRentalRecord(doc));
        }
        return rentalRecords;
    }

    @Override
    public List<RentalRecord> findByLandlordId(String landlordId) {
        List<RentalRecord> rentalRecords = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("landlordId", landlordId))) {
            rentalRecords.add(mapDocumentToRentalRecord(doc));
        }
        return rentalRecords;
    }

    @Override
    public Optional<RentalRecord> findById(String id) {
        try {
            ObjectId objId = new ObjectId(id);
            Document doc = getCollection().find(Filters.eq("_id", objId)).first();
            return doc != null ? Optional.of(mapDocumentToRentalRecord(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
