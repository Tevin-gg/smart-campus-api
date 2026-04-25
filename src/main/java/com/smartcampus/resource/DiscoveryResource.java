package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Map<String, Object> discover() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("api", "Smart Campus Sensor & Room Management API");
        response.put("version", "v1");
        response.put("status", "running");

        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("module", "5COSC022W");
        contact.put("owner", "Smart Campus Backend");
        response.put("contact", contact);

        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        response.put("resources", resources);

        return response;
    }
}
