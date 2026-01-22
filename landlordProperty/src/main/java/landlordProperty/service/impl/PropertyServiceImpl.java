package landlordProperty.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import landlordProperty.MongoClientProvider;
import landlordProperty.model.Property;
import landlordProperty.service.PropertyService;

@Component(service = PropertyService.class)
public class PropertyServiceImpl implements PropertyService {

    @Reference
    private MongoClientProvider mongoClientProvider;

    // Helper to get the collection freshly every time
    private MongoCollection<Document> getCollection() {
        MongoClient client = mongoClientProvider.getClient();
        MongoDatabase database = client.getDatabase("rentspotter_db");
        return database.getCollection("properties");
    }

    @Override
    public void addProperty(Property property) {
        Document doc = new Document();
        if (property.getLandlordId() != null) {
            try {
                doc.append("landlordId", new ObjectId(property.getLandlordId()));
            } catch (IllegalArgumentException e) {
                doc.append("landlordId", property.getLandlordId());
            }
        }

        if (property.getName() != null) {
            doc.append("name", property.getName());
        }
        if (property.getType() != null) {
            doc.append("type", property.getType());
        }
        if (property.getAddress() != null) {
            doc.append("address", property.getAddress());
        }
        if (property.getLocation() != null) {
            doc.append("location", property.getLocation());
        }
        if (property.getPostcode() != null) {
            doc.append("postcode", property.getPostcode());
        }

        if (property.getBedroom() != null) {
            doc.append("bedroom", property.getBedroom());
        }
        if (property.getBathroom() != null) {
            doc.append("bathroom", property.getBathroom());
        }
        if (property.getFurnishing() != null) {
            doc.append("furnishing", property.getFurnishing());
        }
        if (property.getParking() != null) {
            doc.append("parking", property.getParking());
        }
        if (property.getFloorLevel() != null) {
            doc.append("floorLevel", property.getFloorLevel());
        }
        if (property.getBuildUpSize() != null) {
            doc.append("buildUpSize", property.getBuildUpSize());
        }
        if (property.getPrice() != null) {
            doc.append("price", property.getPrice());
        }

        if (property.getFacilities() != null) {
            doc.append("facilities", property.getFacilities());
        }
        if (property.getAccessibility() != null) {
            doc.append("accessibility", property.getAccessibility());
        }
        if (property.getDescription() != null) {
            doc.append("description", property.getDescription());
        }

        if (property.getCoverPhoto() != null) {
            doc.append("coverPhoto", property.getCoverPhoto());
        }
        if (property.getPhotos() != null) {
            doc.append("photos", property.getPhotos());
        }

        getCollection().insertOne(doc);

        property.setId(doc.getObjectId("_id").toString());
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
    public List<Property> getPropertiesByLandlord(String landlordId) {
        List<Property> properties = new ArrayList<>();
        Bson filter;

        try {
            filter = Filters.eq("landlordId", new ObjectId(landlordId));
        } catch (IllegalArgumentException e) {
            filter = Filters.eq("landlordId", landlordId);
        }

        for (Document doc : getCollection().find(filter)) {
            properties.add(mapDocumentToProperty(doc));
        }

        return properties;
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
            System.err.println("Invalid ObjectId format: " + id);
        }

        return Optional.empty();
    }

    @Override
    public boolean updateProperty(String id, Property p) {
        try {
            ObjectId objectId = new ObjectId(id);
            Document updateFields = new Document();

            if (p.getName() != null) {
                updateFields.append("name", p.getName());
            }
            if (p.getType() != null) {
                updateFields.append("type", p.getType());
            }
            if (p.getAddress() != null) {
                updateFields.append("address", p.getAddress());
            }
            if (p.getLocation() != null) {
                updateFields.append("location", p.getLocation());
            }
            if (p.getPostcode() != null) {
                updateFields.append("postcode", p.getPostcode());
            }
            if (p.getPrice() != null) {
                updateFields.append("price", p.getPrice());
            }

            if (p.getBedroom() != null) {
                updateFields.append("bedroom", p.getBedroom());
            }
            if (p.getBathroom() != null) {
                updateFields.append("bathroom", p.getBathroom());
            }
            if (p.getFurnishing() != null) {
                updateFields.append("furnishing", p.getFurnishing());
            }
            if (p.getParking() != null) {
                updateFields.append("parking", p.getParking());
            }
            if (p.getFloorLevel() != null) {
                updateFields.append("floorLevel", p.getFloorLevel());
            }
            if (p.getBuildUpSize() != null) {
                updateFields.append("buildUpSize", p.getBuildUpSize());
            }

            if (p.getFacilities() != null) {
                updateFields.append("facilities", p.getFacilities());
            }
            if (p.getAccessibility() != null) {
                updateFields.append("accessibility", p.getAccessibility());
            }
            if (p.getDescription() != null) {
                updateFields.append("description", p.getDescription());
            }

            if (updateFields.isEmpty()) {
                return false;
            }

            UpdateResult result = getCollection().updateOne(
                    Filters.eq("_id", objectId),
                    new Document("$set", updateFields)
            );

            return result.getModifiedCount() > 0;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean setCoverPhoto(String id, String photoFileName) {
        try {
            ObjectId objectId = new ObjectId(id);

            Bson filter = Filters.and(
                    Filters.eq("_id", objectId),
                    Filters.eq("photos", photoFileName)
            );

            Bson update = Updates.set("coverPhoto", photoFileName);

            UpdateResult result = getCollection().updateOne(filter, update);

            return result.getModifiedCount() > 0;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean deletePropertyPhoto(String id, String photoFileName) {
        try {
            Optional<Property> opt = getPropertyById(id);
            if (!opt.isPresent()) {
                return false;
            }

            Property p = opt.get();
            List<String> photos = p.getPhotos();
            String currentCover = p.getCoverPhoto();

            if (photos == null || photos.size() <= 2) {
                throw new IllegalArgumentException("Cannot delete photo. A property must maintain at least 2 photos.");
            }

            if (!photos.contains(photoFileName)) {
                return false;
            }

            List<String> updatedPhotos = new ArrayList<>(photos);
            updatedPhotos.remove(photoFileName);

            String newCover = currentCover;

            if (currentCover != null && currentCover.equals(photoFileName)) {
                if (!updatedPhotos.isEmpty()) {
                    newCover = updatedPhotos.get(0);
                } else {
                    newCover = null;
                }
            }

            ObjectId objectId = new ObjectId(id);
            getCollection().updateOne(
                    Filters.eq("_id", objectId),
                    Updates.combine(
                            Updates.set("photos", updatedPhotos),
                            Updates.set("coverPhoto", newCover)
                    )
            );

            String uploadDir = System.getProperty("user.home") + "/rentspotter_uploads/";
            File file = new File(uploadDir + photoFileName);
            if (file.exists()) {
                file.delete();
            }

            return true;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addPropertyPhotos(String id, List<String> newPhotoFileNames) {
        try {
            if (newPhotoFileNames == null || newPhotoFileNames.isEmpty()) {
                return false;
            }

            ObjectId objectId = new ObjectId(id);

            UpdateResult result = getCollection().updateOne(
                    Filters.eq("_id", objectId),
                    Updates.pushEach("photos", newPhotoFileNames)
            );

            return result.getModifiedCount() > 0;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean deleteProperty(String id) {
        try {
            ObjectId objectId = new ObjectId(id);

            Document doc = getCollection().find(Filters.eq("_id", objectId)).first();

            if (doc == null) {
                return false;
            }

            List<String> photos = doc.getList("photos", String.class);
            String uploadDir = System.getProperty("user.home") + "/rentspotter_uploads/";

            if (photos != null) {
                for (String filename : photos) {
                    File file = new File(uploadDir + filename);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }

            getCollection().deleteOne(Filters.eq("_id", objectId));

            return true;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<Property> filterProperties(Double maxPrice, String type, String furnishing, String search) {
        List<Property> all = getAllProperties();

        final String searchKeyword = (search != null) ? search.toLowerCase().trim() : null;

        return all.stream()
                .filter(p -> maxPrice == null || (p.getPrice() != null && p.getPrice() <= maxPrice))
                .filter(p -> type == null || type.isEmpty() || (p.getType() != null && p.getType().equalsIgnoreCase(type)))
                .filter(p -> furnishing == null || furnishing.isEmpty() || (p.getFurnishing() != null && p.getFurnishing().equalsIgnoreCase(furnishing)))
                .filter(p -> searchKeyword == null || searchKeyword.isEmpty()
                || (p.getName() != null && p.getName().toLowerCase().contains(searchKeyword))
                || (p.getLocation() != null && p.getLocation().toLowerCase().contains(searchKeyword))
                || (p.getAddress() != null && p.getAddress().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());
    }

    private Property mapDocumentToProperty(Document doc) {
        Property p = new Property();

        Object idObj = doc.get("_id");
        if (idObj != null) {
            p.setId(idObj.toString());
        }

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

        // Handle Numbers
        p.setBuildUpSize(doc.get("buildUpSize", Number.class) != null ? doc.get("buildUpSize", Number.class).intValue() : null);
        p.setPrice(doc.get("price", Number.class) != null ? doc.get("price", Number.class).doubleValue() : null);

        p.setFacilities(doc.getString("facilities"));
        p.setAccessibility(doc.getString("accessibility"));
        p.setDescription(doc.getString("description"));
        p.setCoverPhoto(doc.getString("coverPhoto"));

        // Handle List
        List<String> photos = doc.getList("photos", String.class);
        p.setPhotos(photos != null ? photos : new ArrayList<>());

        return p;
    }
}
