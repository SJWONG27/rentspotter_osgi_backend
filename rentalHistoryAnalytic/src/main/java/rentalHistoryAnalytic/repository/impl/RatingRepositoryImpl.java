package rentalHistoryAnalytic.repository.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rentalHistoryAnalytic.MongoClientProvider;
import rentalHistoryAnalytic.model.Rating;
import rentalHistoryAnalytic.repository.RatingRepository;

import java.util.ArrayList;
import java.util.List;

@Component(service = RatingRepository.class)
public class RatingRepositoryImpl implements RatingRepository {

    @Reference
    private MongoClientProvider mongoClientProvider;

    private MongoCollection<Document> getCollection() {
        MongoClient client = mongoClientProvider.getClient();
        MongoDatabase database = client.getDatabase("rentspotter_db");
        return database.getCollection("ratings");
    }

    private Rating mapDocumentToRating(Document doc) {
        Rating rating = new Rating();
        rating.setId(doc.getObjectId("_id").toString());
        rating.setRaterId(doc.getString("raterId"));
        rating.setRatedUserId(doc.getString("ratedUserId"));
        rating.setScore(doc.getInteger("score"));
        rating.setComment(doc.getString("comment"));
        return rating;
    }

    private Document mapRatingToDocument(Rating rating) {
        return new Document("raterId", rating.getRaterId())
                .append("ratedUserId", rating.getRatedUserId())
                .append("score", rating.getScore())
                .append("comment", rating.getComment());
    }

    @Override
    public Rating save(Rating rating) {
        Document doc = mapRatingToDocument(rating);
        getCollection().insertOne(doc);
        rating.setId(doc.getObjectId("_id").toString());
        return rating;
    }

    @Override
    public List<Rating> findByRatedUserId(String ratedUserId) {
        List<Rating> ratings = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("ratedUserId", ratedUserId))) {
            ratings.add(mapDocumentToRating(doc));
        }
        return ratings;
    }
}
