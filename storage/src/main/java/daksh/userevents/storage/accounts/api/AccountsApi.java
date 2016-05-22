package daksh.userevents.storage.accounts.api;

import com.mongodb.WriteResult;

import org.mongodb.morphia.Key;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daksh.userevents.storage.accounts.constants.NetworkConstants;
import daksh.userevents.storage.accounts.db.DataSource;
import daksh.userevents.storage.accounts.model.Account;

/**
 * Created by daksh on 22-May-16.
 */

@Path(NetworkConstants.BASE_URL)
public class AccountsApi {

    @Path(NetworkConstants.CREATE_ACCOUNT)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createAccount(Account account) {
        if (account == null ||
                account.getName() == null || account.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Key<Account> key = DataSource.getInstance().createAccount(account);
        return Response.created(
                URI.create(NetworkConstants.BASE_URL + "/" + key.getId().toString())
        ).build();
    }

    @Path(NetworkConstants.GET_ACCOUNT)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam(NetworkConstants.ACCOUNT_ID) String accountId) {
        if (accountId == null || accountId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Account account = DataSource.getInstance().getAccount(accountId);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(account).build();
    }

    @Path(NetworkConstants.GET_ALL_ACCOUNTS)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllAccounts() {
        return Response.ok(DataSource.getInstance().getAllAccounts()).build();
    }

    @Path(NetworkConstants.DELETE_ACCOUNT)
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAccount(@PathParam(NetworkConstants.ACCOUNT_ID) String accountId) {
        if (accountId == null || accountId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        WriteResult writeResult = DataSource.getInstance().deleteAccount(accountId);
        if (writeResult.getN() > 0) {
            DataSource.getInstance().deleteDatabase(accountId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
