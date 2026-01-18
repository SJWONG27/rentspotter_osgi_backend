package landlordProperty.service.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import landlordProperty.MongoClientProvider;
import landlordProperty.model.Property;
import landlordProperty.service.PropertyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(service = PropertyService.class)
public class PropertyServiceImpl implements PropertyService {

    @Reference
    private MongoClientProvider mongoClientProvider;

    private MongoCollection<Document> getCollection() {
        MongoClient client = mongoClientProvider.getClient();
        MongoDatabase database = client.getDatabase("rentspotter_db");
        return database.getCollection("properties");
    }

    private Property mapDocumentToProperty(Document doc) {
        Property p = new Property();
        
        // Handle _id 
        Object idObj = doc.get("_id");
        if (idObj != null) {
            p.setId(idObj.toString());
        }

        // Handle landlordId which might be an ObjectId reference
        Object landlordIdObj = doc.get("landlordId");
        if (landlordIdObj != null) {
            p.setLandlordId(landlordIdObj.toString());
        }

        p.setName(doc.getString("name"));
        p.setType(doc.getString("type"));
        p.setAddress(doc.getString("address"));
        p.setLocation(doc.getString("location"));
        p.setPostcode(doc.getString("postcode"));
        p.setBedroom(doc.getString("bedroom"));
        p.setBathroom(doc.getString("bathroom"));
        p.setFurnishing(doc.getString("furnishing"));
        p.setParking(doc.getString("parking"));
        p.setFloorLevel(doc.getString("floorLevel"));
        p.setBuildUpSize(doc.get("buildUpSize", Number.class) != null ? doc.get("buildUpSize", Number.class).intValue() : null);
        p.setPrice(doc.get("price", Number.class) != null ? doc.get("price", Number.class).doubleValue() : null);
        p.setFacilities(doc.getString("facilities"));
        p.setAccessibility(doc.getString("accessibility"));
        p.setDescription(doc.getString("description"));
        p.setCoverPhoto(doc.getString("coverPhoto"));
        p.setPhotos(doc.getList("photos", String.class));
        return p;
    }

    @Override
    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        for (Document doc : getCollection().find()) {
            properties.add(mapDocumentToProperty(doc));
        }
        return properties;
    }

    @Override
    public List<Property> filterProperties(Double maxPrice, String type, String furnishing) {
        List<Property> all = getAllProperties();
        return all.stream()
                .filter(p -> maxPrice == null || (p.getPrice() != null && p.getPrice() <= maxPrice))
                .filter(p -> type == null || type.isEmpty() || (p.getType() != null && p.getType().equalsIgnoreCase(type)))
                .filter(p -> furnishing == null || furnishing.isEmpty() || (p.getFurnishing() != null && p.getFurnishing().equalsIgnoreCase(furnishing)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Property> getPropertyById(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            Document doc = getCollection().find(Filters.eq("_id", objectId)).first();
            if (doc != null) {
                return Optional.of(mapDocumentToProperty(doc));
            }
        } catch (IllegalArgumentException e) {
            // Invalid ObjectId format
        }
        return Optional.empty();
    }
}
