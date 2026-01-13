package authentication.persistence.mongo;


import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserDocument {
    @BsonId
    private String id;

    @BsonProperty("username")
    private String username;
    private String email;
    private String phonenumber;
    private String fullname;
    private String ic;
    private String password;
    private String role;
    private Double overallRating;
    private Integer numberReview;
    private Boolean verified;
    private String verificationToken;
    private java.util.Date tokenExpires;
    private String tokenEmail;
}
