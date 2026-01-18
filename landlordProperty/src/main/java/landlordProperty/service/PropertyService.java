package landlordProperty.service;

import landlordProperty.model.Property;
import java.util.List;
import java.util.Optional;

public interface PropertyService {
    List<Property> getAllProperties();
    List<Property> filterProperties(Double maxPrice, String type, String furnishing);
    Optional<Property> getPropertyById(String id);
}
