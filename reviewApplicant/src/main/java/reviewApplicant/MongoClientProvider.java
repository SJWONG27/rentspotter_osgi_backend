package reviewApplicant;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;


@Component(
        service = MongoClientProvider.class
)
@Designate(ocd = Config.class)
public class MongoClientProvider {

    private MongoClient mongoClient;

    @Activate
    public void activate(Config config) {
        String uri = config.mongo_uri();
        this.mongoClient = MongoClients.create(uri);
        System.out.println("Connected to MongoDB at " + uri);
    }

    @Deactivate
    public void deactivate() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }

    public MongoClient getClient() {
        return mongoClient;
    }
}