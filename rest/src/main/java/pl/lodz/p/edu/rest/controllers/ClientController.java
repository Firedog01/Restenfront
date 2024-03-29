package pl.lodz.p.edu.rest.controllers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

import jakarta.persistence.NoResultException;
import jakarta.transaction.TransactionalException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pl.lodz.p.edu.data.model.DTO.users.ClientDTO;
import pl.lodz.p.edu.rest.authentication.JwtUtilities;
import pl.lodz.p.edu.rest.exception.AuthenticationFailureException;
import pl.lodz.p.edu.rest.exception.IllegalModificationException;
import pl.lodz.p.edu.rest.exception.ConflictException;
import pl.lodz.p.edu.rest.managers.UserManager;
import pl.lodz.p.edu.data.model.users.Client;
import pl.lodz.p.edu.rest.repository.DataFaker;

import java.text.ParseException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.Response.Status.*;

@Path("/clients")
//@RequestScoped
public class ClientController {

    Logger logger = Logger.getLogger(ClientController.class.getName());

    @Inject
    private UserManager userManager;

    @Inject
    private UserControllerMethods userControllerMethods;

    @Inject
    private JwtUtilities jwtUtilities;

    protected ClientController() {}

    // create
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CLIENT", "EMPLOYEE", "ADMIN"})
    public Response addClient(@Valid ClientDTO clientDTO) {
        try {
            Client client = new Client(clientDTO);
            userManager.registerClient(client);
            return Response.status(CREATED).entity(client).build();
        } catch(ConflictException e) {
            return Response.status(CONFLICT).build();
        } catch(TransactionalException e) {
            return Response.status(CONFLICT).build();
        } catch(NullPointerException e) {
            return Response.status(BAD_REQUEST).build();
        }
    }

    // read
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CLIENT", "EMPLOYEE", "ADMIN"})
    public Response searchClients(@QueryParam("login") String login) {
        logger.info(login);
        return userControllerMethods.searchUser("Client", login);
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CLIENT", "EMPLOYEE", "ADMIN"})
    public Response getUserByUuid(@PathParam("uuid") UUID entityId) {
        return userControllerMethods.getSingleUser("Client", entityId);
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CLIENT", "EMPLOYEE", "ADMIN"})
    public Response getUserByLogin(@PathParam("login") String login) {
        return userControllerMethods.getSingleUser("Client", login);
    }

    // update
    @PUT
    @Path("/{entityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CLIENT", "EMPLOYEE", "ADMIN"})
    public Response updateClient(@PathParam("entityId") UUID entityId, @HeaderParam("IF-MATCH") String ifMatch,
                                 @Valid ClientDTO clientDTO) {
        JsonObject jsonDTO = new JsonObject();
        jsonDTO.addProperty("login", clientDTO.getLogin());
        try {
            jwtUtilities.verifySingedLogin(ifMatch, String.valueOf(jsonDTO));
        } catch (ParseException | AuthenticationFailureException | JOSEException e) {
            return Response.status(BAD_REQUEST).build();
        }
        try {
            userManager.updateClient(entityId, clientDTO);
            return Response.status(OK).entity(clientDTO).build();
        } catch (IllegalModificationException e) {
            return Response.status(BAD_REQUEST).build();
        } catch(TransactionalException e) { // login modification
            return Response.status(BAD_REQUEST).build();
        } catch(NoResultException e) {
            return Response.status(NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{entityId}/activate")
    @RolesAllowed({"CLIENT", "EMPLOYEE", "ADMIN"})
    public Response activateUser(@PathParam("entityId") UUID entityId) {
        return userControllerMethods.activateUser("Client", entityId);
    }

    @PUT
    @Path("/{entityId}/deactivate")
    @RolesAllowed({"CLIENT", "EMPLOYEE", "ADMIN"})
    public Response deactivateUser(@PathParam("entityId") UUID entityId) {
        return userControllerMethods.deactivateUser("Client", entityId);
    }


    // ========= other

    @POST
    @Path("/addFake")
    @Produces(MediaType.APPLICATION_JSON)
    public Client addFakeClient() {
        Client c = DataFaker.getClient();
        logger.log(Level.INFO, c.toString());
        try {
            userManager.registerClient(c);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return c;
    }
}
