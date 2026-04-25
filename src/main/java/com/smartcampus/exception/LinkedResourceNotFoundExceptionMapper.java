package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("error", "Linked Resource Not Found");
        errorBody.put("status", 422);
        errorBody.put("message", exception.getMessage());

        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorBody)
                .build();
    }
}