package authentication;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "MongoDB Config")
public @interface Config {
    @AttributeDefinition(name = "Mongo URI")
    String mongo_uri() default "mongodb+srv://22004857_db_user:PzMSBD13MjtnG3KI@cluster0.dknkkoi.mongodb.net/test?appName=Cluster0";
}