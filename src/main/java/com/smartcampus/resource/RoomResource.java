package com.smartcampus.resource;

import com.smartcampus.data.Database;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import com.smartcampus.exception.RoomNotEmptyException;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(Database.rooms.values());
    }

    @POST
    public Response createRoom(Room newRoom, @Context UriInfo uriInfo) {
        if (newRoom == null || newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room ID is required")
                    .build();
        }

        if (Database.rooms.containsKey(newRoom.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Room with this ID already exists")
                    .build();
        }

        if (newRoom.getSensorIds() == null) {
            newRoom.setSensorIds(new ArrayList<>());
        }

        Database.rooms.put(newRoom.getId(), newRoom);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(newRoom.getId())
                .build();

        return Response.created(location)
                .entity(newRoom)
                .build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room foundRoom = Database.rooms.get(roomId);

        if (foundRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found")
                    .build();
        }

        return Response.ok(foundRoom).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room foundRoom = Database.rooms.get(roomId);

        if (foundRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found")
                    .build();
        }

        if (foundRoom.getSensorIds() != null && !foundRoom.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room cannot be deleted because it still has sensors assigned");
        }

        Database.rooms.remove(roomId);

        return Response.ok("Room deleted successfully").build();
    }
}