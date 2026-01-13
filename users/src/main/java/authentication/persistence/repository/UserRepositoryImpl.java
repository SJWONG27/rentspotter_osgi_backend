package authentication.persistence.repository;

import authentication.MongoClientProvider;
import authentication.api.model.User;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component(service = UserRepository.class)
public class UserRepositoryImpl implements UserRepository {

    @Reference
    private MongoClientProvider mongoProvider;

    // ADD THIS: You must have an empty constructor
    public UserRepositoryImpl() {
    }

    private MongoCollection<User> userCollection;

    @Activate
    public void activate() {
        // 1. Create a Registry that handles POJOs (Plain Old Java Objects)
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        // 2. Apply this registry to your database/collection
        this.userCollection = mongoProvider.getClient()
                .getDatabase("rentspotter_db")
                .withCodecRegistry(pojoCodecRegistry) // <--- CRITICAL STEP
                .getCollection("users", User.class);
    }

    @Override
    public void save(User user) {
        userCollection.insertOne(user);
    }

    @Override
    public User findById(String id) {
        return userCollection.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    @Override
    public User findByEmail(String email) {
        return userCollection.find(Filters.eq("email", email)).first();
    }

    @Override
    public User findByUsername(String username) {
        return userCollection.find(Filters.eq("username", username)).first();
    }
}