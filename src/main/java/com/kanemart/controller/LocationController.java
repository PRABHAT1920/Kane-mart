package com.kanemart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.model.Location;
import com.kanemart.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(
                locationService.getAllLocations()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                locationService.getLocationById(id)
        );
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<Location>> getLocationsByWarehouse(
            @PathVariable Long warehouseId) {

        return ResponseEntity.ok(
                locationService.getLocationsByWarehouse(warehouseId)
        );
    }

    @PostMapping
    public ResponseEntity<Location> addLocation(
            @RequestParam Long warehouseId,
            @RequestBody Location location) {

        return ResponseEntity.ok(
                locationService.addLocation(location, warehouseId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Long id,
            @RequestBody Location location) {

        return ResponseEntity.ok(
                locationService.updateLocation(id, location)
        );
    }
}