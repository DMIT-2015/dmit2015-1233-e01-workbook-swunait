package dmit2015.restclient;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.omnifaces.util.Messages;

import java.util.HashMap;
import java.util.Map;

public class RestApiResponseMapper implements ResponseExceptionMapper<WebApplicationException> {
    @Override
    public WebApplicationException toThrowable(Response response) {
        String responseBodyJson = response.readEntity(String.class);
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> responseBodyMap = jsonb.fromJson(responseBodyJson, new HashMap<String,String>(){}.getClass().getGenericSuperclass());
        responseBodyMap.forEach(Messages::addError);
        return null;
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status == 400;
    }
}