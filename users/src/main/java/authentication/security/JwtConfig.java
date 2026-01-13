package authentication.security;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "JWT Configuration")
public @interface JwtConfig {
    @AttributeDefinition(name = "JWT Secret Key")
    String jwt_secret() default "supersecretkey123456789supersecretkey";
}
