package landlordProperty.service;

import java.util.List;
import java.util.Optional;

import landlordProperty.model.Property;

public interface PropertyService {

    void addProperty(Property property);

    List<Property> getAllProperties();

    List<Property> getPropertiesByLandlord(String landlordId);

    Optional<Property> getPropertyById(String id);

    boolean updateProperty(String id, Property newDetails);

    boolean setCoverPhoto(String id, String photoFileName);

    boolean deletePropertyPhoto(String id, String photoFileName);

    boolean addPropertyPhotos(String id, List<String> newPhotoFileNames);

    boolean deleteProperty(String id);

    List<Property> filterProperties(Double maxPrice, String type, String furnishing, String search);
}
