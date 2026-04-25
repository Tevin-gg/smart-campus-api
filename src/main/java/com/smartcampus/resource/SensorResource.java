package com.smartcampus.resource;

import com.smartcampus.data.Database;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(Database.sensors.values());

        if (type == null || type.trim().isEmpty()) {
            return sensorList;
        }

        List<Sensor> filteredList = new ArrayList<>();

        for (Sensor oneSensor : sensorList) {
            if (oneSensor.getType() != null && oneSensor.getType().equalsIgnoreCase(type)) {
                filteredList.add(oneSensor);
            }
        }

        return filteredList;
    }

    @POST
    public Response createSensor(Sensor newSensor) {
        if (newSensor == null || newSensor.getId() == null || newSensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor ID is required")
                    .build();
        }

        if (Database.sensors.containsKey(newSensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Sensor with this ID already exists")
                    .build();
        }

        if (newSensor.getRoomId() == null || newSensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("roomId is required")
                    .build();
        }

        Room linkedRoom = Database.rooms.get(newSensor.getRoomId());

        if (linkedRoom == null) {
            throw new LinkedResourceNotFoundException("The given roomId does not exist");
        }

        if (newSensor.getStatus() == null || newSensor.getStatus().trim().isEmpty()) {
            newSensor.setStatus("ACTIVE");
        }

        Database.sensors.put(newSensor.getId(), newSensor);

        if (linkedRoom.getSensorIds() == null) {
            linkedRoom.setSensorIds(new ArrayList<>());
        }

        linkedRoom.getSensorIds().add(newSensor.getId());

        return Response.status(Response.Status.CREATED)
                .entity(newSensor)
                .build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}