package daksh.userevents.storage.admins.api;

import java.io.IOException;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.admins.db.AdminDao;
import daksh.userevents.storage.common.api.AuthenticationFilter;

/**
 * Created by daksh on 22-May-16.
 */

@AdminSecured
@Provider
public class AdminAuthenticationFilter extends AuthenticationFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("AdminAuthenticationFilter 1");
        handleAuthentication(requestContext, AdminNetworkConstants.ADMIN_ID, true);
        System.out.println("AdminAuthenticationFilter 2");
        System.out.println("Admin I.D. =  " + requestContext.getProperty(AdminNetworkConstants.ADMIN_ID));
    }

    @Override
    public String getIdFromToken(String token) throws NotAuthorizedException {
        return AdminDao.getInstance().getAdminIdFromAdminToken(token);
    }
}
