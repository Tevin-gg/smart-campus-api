package com.smartcampus.resource;

import com.smartcampus.data.Database;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.exception.SensorUnavailableException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadingHistory() {
        Sensor foundSensor = Database.sensors.get(sensorId);

        if (foundSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found")
                    .build();
        }

        List<SensorReading> readingList = Database.sensorReadings.get(sensorId);

        if (readingList == null) {
            readingList = new ArrayList<>();
        }

        return Response.ok(readingList).build();
    }

    @POST
    public Response addReading(SensorReading newReading) {
        Sensor foundSensor = Database.sensors.get(sensorId);

        if (foundSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found")
                    .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(foundSensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is in maintenance and cannot accept readings");
        }

        if (newReading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading body is required")
                    .build();
        }

        if (newReading.getId() == null || newReading.getId().trim().isEmpty()) {
            newReading.setId(UUID.randomUUID().toString());
        }

        if (newReading.getTimestamp() == 0) {
            newReading.setTimestamp(System.currentTimeMillis());
        }

        List<SensorReading> readingList = Database.sensorReadings.get(sensorId);

        if (readingList == null) {
            readingList = new ArrayList<>();
            Database.sensorReadings.put(sensorId, readingList);
        }

        readingList.add(newReading);

        foundSensor.setCurrentValue(newReading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(newReading)
                .build();
    }
}