package com.kanemart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kanemart.model.Location;
import com.kanemart.model.Warehouse;
import com.kanemart.repository.LocationRepository;
import com.kanemart.repository.WarehouseRepository;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;

    public LocationService(
            LocationRepository locationRepository,
            WarehouseRepository warehouseRepository) {

        this.locationRepository = locationRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
    }

    public List<Location> getLocationsByWarehouse(Long warehouseId) {

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        return locationRepository.findByWarehouse(warehouse);
    }

    public Location addLocation(
            Location location,
            Long warehouseId) {

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        location.setWarehouse(warehouse);

        return locationRepository.save(location);
    }

    public Location updateLocation(
            Long id,
            Location updatedLocation) {

        Location existingLocation = getLocationById(id);

        existingLocation.setZone(updatedLocation.getZone());
        existingLocation.setRack(updatedLocation.getRack());
        existingLocation.setShelf(updatedLocation.getShelf());
        existingLocation.setBin(updatedLocation.getBin());

        if (updatedLocation.getWarehouse() != null) {
            existingLocation.setWarehouse(updatedLocation.getWarehouse());
        }

        return locationRepository.save(existingLocation);
    }
}