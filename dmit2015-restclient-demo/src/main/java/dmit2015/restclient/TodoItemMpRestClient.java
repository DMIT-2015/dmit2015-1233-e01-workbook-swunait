package dmit2015.restclient;

import jakarta.enterprise.context.RequestScoped;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * The baseUri for the web MpRestClient be set in either microprofile-config.properties (recommended)
 * or in this file using @RegisterRestClient(baseUri = "http://server/path").
 * <p>
 * To set the baseUri in microprofile-config.properties:
 * 1) Open src/main/resources/META-INF/microprofile-config.properties
 * 2) Add a key/value pair in the following format:
 * package-name.ClassName/mp-rest/url=baseUri
 * For example:
 * package-name:    dmit2015.restclient
 * ClassName:       TodoItemMpRestClient
 * baseUri:         http://localhost:8080/contextName
 * The key/value pair you need to add is:
 * <code>
 * dmit2015.restclient.TodoItemMpRestClient/mp-rest/url=http://localhost:8080/contextName
 * </code>
 * <p>
 * To use the client interface from an environment does support CDI, add @Inject and @RestClient before the field declaration such as:
 * <code>
 *
 * @Inject
 * @RestClient private TodoItemMpRestClient _todoitemMpRestClient;
 * </code>
 * <p>
 * To use the client interface from an environment that does not support CDI, you can use the RestClientBuilder class to programmatically build an instance as follows:
 * <code>
 * URI apiURI = new URI("http://sever/contextName");
 * TodoItemMpRestClient _todoitemMpRestClient = RestClientBuilder.newBuilder().baseUri(apiURi).build(TodoItemMpRestClient.class);
 * </code>
 */
@RequestScoped
@RegisterProvider(RestApiResponseMapper.class)
@RegisterRestClient(baseUri = "http://localhost:8090/restapi/TodoItemsDto")
public interface TodoItemMpRestClient {

    @POST
    Response create(TodoItem newTodoItem);

    @GET
    List<TodoItem> findAll();

    @GET
    @Path("/{id}")
    TodoItem findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    TodoItem update(@PathParam("id") Long id, TodoItem updatedTodoItem);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") Long id);

}