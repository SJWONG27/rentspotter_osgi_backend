package tenantApplication.repository.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import tenantApplication.MongoClientProvider;
import tenantApplication.model.Application;
import tenantApplication.repository.TenantApplicationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component(service = TenantApplicationRepository.class)
public class TenantApplicationRepositoryImpl implements TenantApplicationRepository {

    @Reference
    private MongoClientProvider mongoClientProvider;

    private MongoCollection<Document> getCollection() {
        MongoClient client = mongoClientProvider.getClient();
        MongoDatabase database = client.getDatabase("rentspotter_db");
        return database.getCollection("applications");
    }

    private Application mapDocumentToApplication(Document doc) {
        Application app = new Application();
        app.setId(doc.getObjectId("_id").toString());
        app.setTenantId(doc.getString("tenantId"));
        app.setPropertyId(doc.getString("propertyId"));
        app.setStatus(Application.ApplicationStatus.valueOf(doc.getString("status")));
        app.setApplicationDate(doc.getDate("applicationDate"));
        app.setMonthlyIncome(doc.getDouble("monthlyIncome"));
        app.setOccupation(doc.getString("occupation"));
        app.setMessage(doc.getString("message"));
        return app;
    }

    private Document mapApplicationToDocument(Application app) {
        return new Document("tenantId", app.getTenantId())
                .append("propertyId", app.getPropertyId())
                .append("status", app.getStatus().name())
                .append("applicationDate", app.getApplicationDate())
                .append("monthlyIncome", app.getMonthlyIncome())
                .append("occupation", app.getOccupation())
                .append("message", app.getMessage());
    }

    @Override
    public void save(Application app) {
        Document doc = mapApplicationToDocument(app);
        getCollection().insertOne(doc);
        app.setId(doc.getObjectId("_id").toString());
    }

    @Override
    public void update(Application app) {
        if (app.getId() != null) {
            try {
                ObjectId objId = new ObjectId(app.getId());
                getCollection().replaceOne(Filters.eq("_id", objId), mapApplicationToDocument(app));
            } catch (IllegalArgumentException e) {
                // Ignore invalid ID
            }
        }
    }

    @Override
    public Optional<Application> findById(String id) {
        try {
            ObjectId objId = new ObjectId(id);
            Document doc = getCollection().find(Filters.eq("_id", objId)).first();
            return doc != null ? Optional.of(mapDocumentToApplication(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Application> findByTenantId(String tenantId) {
        List<Application> apps = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("tenantId", tenantId))) {
            apps.add(mapDocumentToApplication(doc));
        }
        return apps;
    }

    @Override
    public Optional<Application> findPendingApplication(String tenantId, String propertyId) {
        Document doc = getCollection().find(Filters.and(
                Filters.eq("tenantId", tenantId),
                Filters.eq("propertyId", propertyId),
                Filters.eq("status", Application.ApplicationStatus.PENDING.name())
        )).first();
        return doc != null ? Optional.of(mapDocumentToApplication(doc)) : Optional.empty();
    }

    @Override
    public void delete(String id) {
        try {
            ObjectId objId = new ObjectId(id);
            getCollection().deleteOne(Filters.eq("_id", objId));
        } catch (IllegalArgumentException e) {
            // Ignore
        }
    }
}
