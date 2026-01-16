package leaseAgreement.persistence;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import leaseAgreement.MongoClientProvider;
import leaseAgreement.api.model.LeaseAgreementModel;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component(service = LeaseAgreementRepository.class)
public class LeaseAgreementRepositoryImpl implements LeaseAgreementRepository {
    @Reference
    private MongoClientProvider mongoProvider;

    public LeaseAgreementRepositoryImpl() {}

    private MongoCollection<LeaseAgreementModel> leaseCollection;


    @Activate
    public void activate() {
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        this.leaseCollection = mongoProvider.getClient()
                .getDatabase("rentspotter_db")
                .withCodecRegistry(pojoCodecRegistry)
                .getCollection("leases", LeaseAgreementModel.class);
    }

    @Override
    public void save(LeaseAgreementModel lease) {
        leaseCollection.insertOne(lease);
    }

    @Override
    public void update(LeaseAgreementModel lease) {
        leaseCollection.replaceOne(Filters.eq("_id", lease.getId()), lease, new ReplaceOptions().upsert(true));
    }

    @Override
    public LeaseAgreementModel findById(String id) {
        if (!ObjectId.isValid(id)) return null;
        return leaseCollection.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    @Override
    public LeaseAgreementModel findByApplicationId(String applicationId) {
        return leaseCollection.find(Filters.eq("applicationId", applicationId)).first();
    }

    @Override
    public List<LeaseAgreementModel> findByTenantIdOrderByDayDesc(String tenantId) {
        return leaseCollection.find(Filters.eq("tenantId", tenantId))
                .sort(Sorts.descending("day"))
                .into(new ArrayList<>());
    }
}
