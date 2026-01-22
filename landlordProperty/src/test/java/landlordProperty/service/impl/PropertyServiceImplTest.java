package landlordProperty.service.impl;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import landlordProperty.MongoClientProvider;
import landlordProperty.model.Property;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceImplTest {

    @Mock
    private MongoClientProvider mongoClientProvider;

    @Mock
    private MongoClient mongoClient;

    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private MongoCollection<Document> mongoCollection;

    @Mock
    private FindIterable<Document> findIterable;

    @Mock
    private MongoCursor<Document> mongoCursor;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    // Helper to create a dummy document
    private Document createPropertyDoc(String id, String name, double price, String type) {
        Document doc = new Document("_id", new ObjectId(id));
        doc.append("name", name);
        doc.append("price", price);
        doc.append("type", type);
        doc.append("photos", new ArrayList<>(Arrays.asList("photo1.jpg", "photo2.jpg", "photo3.jpg")));
        doc.append("coverPhoto", "photo1.jpg");
        doc.append("location", "Kuala Lumpur");
        return doc;
    }

    @BeforeEach
    void setUp() {
        // Chain the mocks: Provider -> Client -> Database -> Collection
        lenient().when(mongoClientProvider.getClient()).thenReturn(mongoClient);
        lenient().when(mongoClient.getDatabase("rentspotter_db")).thenReturn(mongoDatabase);
        lenient().when(mongoDatabase.getCollection("properties")).thenReturn(mongoCollection);
    }

    // UC-8 Upload new property
    @Test
    void addProperty_Success() {
        Property p = new Property();
        p.setName("New Condo");
        p.setPrice(2000.0);
        p.setLandlordId("507f1f77bcf86cd799439011");

        when(mongoCollection.insertOne(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.append("_id", new ObjectId());
            return null;
        });

        propertyService.addProperty(p);

        verify(mongoCollection, times(1)).insertOne(any(Document.class));

        assertNotNull(p.getId());
    }

    // For tenant module (UC-1)
    @Test
    void getAllProperties_Success() {
        Document doc1 = createPropertyDoc("507f1f77bcf86cd799439011", "Condo A", 1500.0, "Condo");
        Document doc2 = createPropertyDoc("507f1f77bcf86cd799439012", "Condo B", 2500.0, "Terrace");

        when(mongoCollection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true, true, false); // 2 items
        when(mongoCursor.next()).thenReturn(doc1, doc2);

        List<Property> result = propertyService.getAllProperties();

        assertEquals(2, result.size());
        assertEquals("Condo A", result.get(0).getName());
    }

    // UC-6 View uploaded property list
    @Test
    void getPropertiesByLandlord_Success() {
        String landlordId = "507f1f77bcf86cd799439099";
        Document doc1 = createPropertyDoc("507f1f77bcf86cd799439011", "My Condo", 1500.0, "Condo");
        doc1.append("landlordId", landlordId);

        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true, false);
        when(mongoCursor.next()).thenReturn(doc1);

        List<Property> result = propertyService.getPropertiesByLandlord(landlordId);

        assertEquals(1, result.size());
        assertEquals(landlordId, result.get(0).getLandlordId());
    }

    // UC-9 View and edit existing property details (View)
    @Test
    void getPropertyById_Found() {
        String id = "507f1f77bcf86cd799439011";
        Document doc = createPropertyDoc(id, "Target Property", 1200.0, "Apartment");

        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(doc);

        Optional<Property> result = propertyService.getPropertyById(id);

        assertTrue(result.isPresent());
        assertEquals("Target Property", result.get().getName());
    }

    @Test
    void getPropertyById_NotFound() {
        String id = "507f1f77bcf86cd799439011";
        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(null);

        Optional<Property> result = propertyService.getPropertyById(id);

        assertFalse(result.isPresent());
    }

    // UC-9 View and edit existing property details (Edit)
    @Test
    void updateProperty_Success() {
        String id = "507f1f77bcf86cd799439011";
        Property updateDetails = new Property();
        updateDetails.setPrice(1800.0); // updating price

        UpdateResult mockResult = mock(UpdateResult.class);
        when(mockResult.getModifiedCount()).thenReturn(1L);
        when(mongoCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockResult);

        boolean success = propertyService.updateProperty(id, updateDetails);

        assertTrue(success);
        verify(mongoCollection).updateOne(any(Bson.class), any(Bson.class));
    }

    // UC-10 Manage property photos (add photo)
    @Test
    void addPropertyPhotos_Success() {
        String id = "507f1f77bcf86cd799439011";
        List<String> newPhotos = Arrays.asList("new1.jpg", "new2.jpg");

        UpdateResult mockResult = mock(UpdateResult.class);
        when(mockResult.getModifiedCount()).thenReturn(1L);
        when(mongoCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockResult);

        boolean success = propertyService.addPropertyPhotos(id, newPhotos);

        assertTrue(success);
    }

    // UC-10 Manage property photos (delete photo)
    @Test
    void deletePropertyPhoto_ValidationFailure_TooFewPhotos() {
        String id = "507f1f77bcf86cd799439011";
        Document doc = createPropertyDoc(id, "Test", 1000.0, "Condo");
        doc.put("photos", new ArrayList<>(Arrays.asList("p1.jpg", "p2.jpg")));

        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(doc);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                propertyService.deletePropertyPhoto(id, "p1.jpg")
        );
        assertEquals("Cannot delete photo. A property must maintain at least 2 photos.", ex.getMessage());

        verify(mongoCollection, never()).updateOne(any(Bson.class), any(Bson.class));
    }

    @Test
    void deletePropertyPhoto_Success_RenewCoverPhoto() {
        String id = "507f1f77bcf86cd799439011";
        Document doc = createPropertyDoc(id, "Test", 1000.0, "Condo");
        // 3 photos, Cover is p1.jpg
        doc.put("photos", new ArrayList<>(Arrays.asList("p1.jpg", "p2.jpg", "p3.jpg")));
        doc.put("coverPhoto", "p1.jpg");

        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(doc);

        UpdateResult mockResult = mock(UpdateResult.class);
        when(mongoCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockResult);

        boolean success = propertyService.deletePropertyPhoto(id, "p1.jpg"); // Deleting the cover!

        assertTrue(success);
        verify(mongoCollection).updateOne(any(Bson.class), any(Bson.class));
        // check to pick p2.jpg as new cover
    }

    // UC-7 Filter properties
    @Test
    void filterProperties_Logic() {
        Document p1 = createPropertyDoc("507f1f77bcf86cd799439011", "Sunset Condo", 1000.0, "Condo"); // Cheap
        Document p2 = createPropertyDoc("507f1f77bcf86cd799439012", "Sunrise Villa", 3000.0, "Villa"); // Expensive
        Document p3 = createPropertyDoc("507f1f77bcf86cd799439013", "City Apartment", 1200.0, "Apartment"); // Cheap
        p3.append("location", "Petaling Jaya");

        when(mongoCollection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);

        when(mongoCursor.hasNext()).thenReturn(true, true, true, false);
        when(mongoCursor.next()).thenReturn(p1, p2, p3);

        // Test 1: Max Price 1500
        List<Property> cheapOnes = propertyService.filterProperties(1500.0, null, null, null);
        assertEquals(2, cheapOnes.size()); // Should match p1 and p3

        reset(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true, true, true, false);
        when(mongoCursor.next()).thenReturn(p1, p2, p3);

        // Test 2: Search "City"
        List<Property> cityOnes = propertyService.filterProperties(null, null, null, "City");
        assertEquals(1, cityOnes.size());
        assertEquals("City Apartment", cityOnes.get(0).getName());
    }

    // UC-11 Delete existing property
    @Test
    void deleteProperty_Success() {
        String id = "507f1f77bcf86cd799439011";
        Document doc = createPropertyDoc(id, "To Delete", 1000.0, "Condo");

        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(doc);

        DeleteResult mockResult = mock(DeleteResult.class);

        boolean success = propertyService.deleteProperty(id);

        verify(mongoCollection).deleteOne(any(Bson.class));
        assertTrue(success);
    }
}
