package tenantApplication.service.impl;

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
import tenantApplication.service.TenantApplicationService;
import landlordProperty.service.PropertyService;
import landlordProperty.model.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component(service = TenantApplicationService.class)
public class TenantApplicationServiceImpl implements TenantApplicationService {

    @Reference
    private MongoClientProvider mongoClientProvider;

    @Reference
    private PropertyService propertyService;

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

    @Override
    public List<Property> getAvailableProperties(Double maxPrice, String type, String furnishing) {
        // We defer to PropertyService, which handles listings
        return propertyService.filterProperties(maxPrice, type, furnishing);
    }

    @Override
    public Property getPropertyDetails(String propertyId) {
        return propertyService.getPropertyById(propertyId).orElse(null);
    }

    @Override
    public Application submitApplication(String tenantId, String propertyId, Double monthlyIncome, String occupation, String message) {
        // Validation: Property exists?
        Property prop = getPropertyDetails(propertyId);
        if (prop == null) {
            throw new RuntimeException("Property not found");
        }

        Application app = new Application(tenantId, propertyId, monthlyIncome, occupation, message);
        
        Document doc = new Document("tenantId", app.getTenantId())
                .append("propertyId", app.getPropertyId())
                .append("status", app.getStatus().name())
                .append("applicationDate", app.getApplicationDate())
                .append("monthlyIncome", app.getMonthlyIncome())
                .append("occupation", app.getOccupation())
                .append("message", app.getMessage());

        getCollection().insertOne(doc);
        app.setId(doc.getObjectId("_id").toString());
        
        return app;
    }

    @Override
    public List<Application> getTenantApplications(String tenantId) {
        List<Application> apps = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("tenantId", tenantId))) {
            apps.add(mapDocumentToApplication(doc));
        }
        return apps;
    }

    @Override
    public Application cancelApplication(String applicationId, String tenantId) {
        try {
            ObjectId objId = new ObjectId(applicationId);
            Document doc = getCollection().find(Filters.eq("_id", objId)).first();
            if (doc == null) throw new RuntimeException("Application not found");
            
            Application app = mapDocumentToApplication(doc);
            if (!app.getTenantId().equals(tenantId)) throw new RuntimeException("Unauthorized");
            if (app.getStatus() != Application.ApplicationStatus.PENDING) throw new RuntimeException("Cannot cancel non-pending application");

            getCollection().updateOne(Filters.eq("_id", objId), new Document("$set", new Document("status", Application.ApplicationStatus.CANCELLED.name())));
            app.setStatus(Application.ApplicationStatus.CANCELLED);
            return app;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ID");
        }
    }

    @Override
    public void deleteApplication(String applicationId, String tenantId) {
        try {
            ObjectId objId = new ObjectId(applicationId);
            Document doc = getCollection().find(Filters.eq("_id", objId)).first();
            if (doc != null) {
                Application app = mapDocumentToApplication(doc);
                if (!app.getTenantId().equals(tenantId)) throw new RuntimeException("Unauthorized");
                if (app.getStatus() != Application.ApplicationStatus.REJECTED) throw new RuntimeException("Only rejected applications can be deleted");

                getCollection().deleteOne(Filters.eq("_id", objId));
            }
        } catch (IllegalArgumentException e) {
             // Invalid ID
        }
    }
}
