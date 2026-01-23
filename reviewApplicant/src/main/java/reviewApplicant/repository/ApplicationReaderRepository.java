package reviewApplicant.repository;

import tenantApplication.model.Application;
import java.util.List;

public interface ApplicationReaderRepository {
    List<Application> findAll();
    List<Application> findByPropertyIds(List<String> propertyIds);
}
