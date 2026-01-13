package authentication.security;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = JwtUtil.class)
@Designate(ocd = JwtConfig.class)
public class JwtProvider extends JwtUtil {

    @Activate
    public JwtProvider(JwtConfig config) {
        super(config.jwt_secret());
    }
}