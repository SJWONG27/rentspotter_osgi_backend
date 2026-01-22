package reviewApplicant.repository.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import reviewApplicant.MongoClientProvider;
import reviewApplicant.repository.ApplicationReaderRepository;
import tenantApplication.model.Application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component(service = ApplicationReaderRepository.class)
public class ApplicationReaderRepositoryImpl implements ApplicationReaderRepository {

    @Reference
    private MongoClientProvider mongoClientProvider;

    private static final String DB_NAME = "rentspotter_db";
    private static final String COLLECTION_NAME = "applications";

    private MongoCollection<Document> getCollection() {
        MongoClient client = mongoClientProvider.getClient();
        MongoDatabase database = client.getDatabase(DB_NAME);
        return database.getCollection(COLLECTION_NAME);
    }

    @Override
    public List<Application> findAll() {
        MongoCollection<Document> collection = getCollection();
        return StreamSupport.stream(collection.find().spliterator(), false)
                .map(this::toApplication)
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> findByPropertyIds(List<String> propertyIds) {
        MongoCollection<Document> collection = getCollection();
        return StreamSupport.stream(
                collection.find(Filters.in("propertyId", propertyIds)).spliterator(), false).map(this::toApplication)
                .collect(Collectors.toList());
    }

    private Application toApplication(Document doc) {
        Application app = new Application();
        app.setId(doc.getObjectId("_id").toHexString());
        app.setTenantId(doc.getString("tenantId"));
        app.setPropertyId(doc.getString("propertyId"));

        String statusStr = doc.getString("status");
        if (statusStr != null) {
            app.setStatus(Application.ApplicationStatus.valueOf(statusStr));
        }

        app.setApplicationDate(doc.getDate("applicationDate"));
        app.setMonthlyIncome(doc.getDouble("monthlyIncome"));
        app.setOccupation(doc.getString("occupation"));
        app.setMessage(doc.getString("message"));
        return app;
    }
}
