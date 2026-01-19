package tenantApplication.repository;

import tenantApplication.model.Application;
import java.util.List;
import java.util.Optional;

public interface TenantApplicationRepository {
    void save(Application application);
    void update(Application application);
    Optional<Application> findById(String id);
    List<Application> findByTenantId(String tenantId);
    Optional<Application> findPendingApplication(String tenantId, String propertyId);
    void delete(String id);
}
