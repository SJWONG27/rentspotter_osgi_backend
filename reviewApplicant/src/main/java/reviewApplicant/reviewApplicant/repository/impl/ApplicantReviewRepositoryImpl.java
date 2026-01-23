package reviewApplicant.repository.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import reviewApplicant.MongoClientProvider;
import reviewApplicant.model.ApplicantReview;
import reviewApplicant.repository.ApplicantReviewRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component(service = ApplicantReviewRepository.class)
public class ApplicantReviewRepositoryImpl implements ApplicantReviewRepository {

    @Reference
    private MongoClientProvider mongoClientProvider;

    private static final String DB_NAME = "rentspotter_db";
    private static final String COLLECTION_NAME = "applicant_reviews";

    private MongoCollection<Document> getCollection() {
        MongoClient client = mongoClientProvider.getClient();
        MongoDatabase database = client.getDatabase(DB_NAME);
        return database.getCollection(COLLECTION_NAME);
    }

    @Override
    public ApplicantReview save(ApplicantReview review) {
        MongoCollection<Document> collection = getCollection();
        Document doc = toDocument(review);

        if (review.getId() == null || review.getId().isEmpty()) {
            // Insert new document
            collection.insertOne(doc);
            review.setId(doc.getObjectId("_id").toHexString());
        } else {
            // Update existing document
            collection.replaceOne(Filters.eq("_id", new ObjectId(review.getId())), doc);
        }

        return review;
    }

    @Override
    public Optional<ApplicantReview> findById(String id) {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return doc != null ? Optional.of(toApplicantReview(doc)) : Optional.empty();
    }

    @Override
    public List<ApplicantReview> findByLandlordId(String landlordId) {
        MongoCollection<Document> collection = getCollection();
        return StreamSupport.stream(
                collection.find(Filters.eq("landlordId", landlordId)).spliterator(), false).map(this::toApplicantReview)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicantReview> findByTenantId(String tenantId) {
        MongoCollection<Document> collection = getCollection();
        return StreamSupport.stream(
                collection.find(Filters.eq("tenantId", tenantId)).spliterator(), false).map(this::toApplicantReview)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ApplicantReview> findByApplicationId(String applicationId) {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find(Filters.eq("applicationId", applicationId)).first();
        return doc != null ? Optional.of(toApplicantReview(doc)) : Optional.empty();
    }

    @Override
    public Optional<ApplicantReview> findByApplicationIdAndLandlordId(String applicationId, String landlordId) {
        MongoCollection<Document> collection = getCollection();
        Document doc = collection.find(
                Filters.and(
                        Filters.eq("applicationId", applicationId),
                        Filters.eq("landlordId", landlordId)))
                .first();
        return doc != null ? Optional.of(toApplicantReview(doc)) : Optional.empty();
    }

    @Override
    public List<ApplicantReview> findAll() {
        MongoCollection<Document> collection = getCollection();
        return StreamSupport.stream(collection.find().spliterator(), false)
                .map(this::toApplicantReview)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        MongoCollection<Document> collection = getCollection();
        collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
    }

    private Document toDocument(ApplicantReview review) {
        Document doc = new Document();
        if (review.getId() != null && !review.getId().isEmpty()) {
            doc.append("_id", new ObjectId(review.getId()));
        }
        doc.append("applicationId", review.getApplicationId())
                .append("landlordId", review.getLandlordId())
                .append("tenantId", review.getTenantId())
                .append("propertyId", review.getPropertyId())
                .append("decision", review.getDecision() != null ? review.getDecision().name() : null)
                .append("feedback", review.getFeedback())
                .append("reviewDate", review.getReviewDate());
        return doc;
    }

    private ApplicantReview toApplicantReview(Document doc) {
        ApplicantReview review = new ApplicantReview();
        review.setId(doc.getObjectId("_id").toHexString());
        review.setApplicationId(doc.getString("applicationId"));
        review.setLandlordId(doc.getString("landlordId"));
        review.setTenantId(doc.getString("tenantId"));
        review.setPropertyId(doc.getString("propertyId"));

        String decisionStr = doc.getString("decision");
        if (decisionStr != null) {
            review.setDecision(ApplicantReview.ReviewDecision.valueOf(decisionStr));
        }

        review.setFeedback(doc.getString("feedback"));
        review.setReviewDate(doc.getDate("reviewDate"));
        return review;
    }
}
