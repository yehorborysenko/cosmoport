package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ShipService {

    Page<Ship> getShips(Specification<Ship> specification, Pageable sortedBy);

    void saveShip(Ship ship);

    Ship getShip(long id);

    void deleteShip(long id);

    Integer getShipCount(Specification<Ship> specification);

    Ship updateShip(Ship ship, long id);

    Specification<Ship> selectByName(String name);

    Specification<Ship> selectByPlanet(String planet);

    Specification<Ship> selectByShipType(ShipType shipType);

    Specification<Ship> selectByProdDate(Long after, Long before);

    Specification<Ship> selectByUsed(Boolean isUsed);

    Specification<Ship> selectBySpeed(Double minSpeed, Double maxSpeed);

    Specification<Ship> selectByCrew(Integer minCrew, Integer maxCrew);

    Specification<Ship> selectByRating(Double minRating, Double maxRating);

}
