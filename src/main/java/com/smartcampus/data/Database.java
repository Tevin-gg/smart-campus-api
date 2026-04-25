package com.smartcampus.data;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    public static Map<String, Room> rooms = new HashMap<>();
    public static Map<String, Sensor> sensors = new HashMap<>();
    public static Map<String, List<SensorReading>> sensorReadings = new HashMap<>();

    static {
        Room room1 = new Room("LAB-101", "Computer Lab 101", 40);
        Room room2 = new Room("LIB-201", "Library Study Area", 25);

        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);

        Sensor sensor1 = new Sensor("CO2-001", "CO2", "ACTIVE", 420.5, "LAB-101");
        Sensor sensor2 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 23.8, "LIB-201");

        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);

        room1.getSensorIds().add(sensor1.getId());
        room2.getSensorIds().add(sensor2.getId());

        List<SensorReading> sensor1History = new ArrayList<>();
        sensor1History.add(new SensorReading("READ-001", System.currentTimeMillis(), 420.5));

        List<SensorReading> sensor2History = new ArrayList<>();
        sensor2History.add(new SensorReading("READ-002", System.currentTimeMillis(), 23.8));

        sensorReadings.put(sensor1.getId(), sensor1History);
        sensorReadings.put(sensor2.getId(), sensor2History);
    }

    private Database() {
    }
}