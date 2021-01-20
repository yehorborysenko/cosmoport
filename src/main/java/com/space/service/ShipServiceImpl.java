package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public Page<Ship> getShips(Specification<Ship> specification, Pageable sortedBy) {
        return shipRepository.findAll(specification, sortedBy);
    }

    @Override
    public void saveShip(Ship ship) {
        ship.setRating(countRating(ship.getSpeed(), ship.isUsed(), ship.getProdDate()));
        shipRepository.save(ship);
    }

    @Override
    public Ship getShip(long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteShip(long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public Integer getShipCount(Specification<Ship> specification) {
        return shipRepository.findAll(specification).size();
    }

    @Override
    public Ship updateShip(Ship ship, long id) {
        Double rating = countRating(ship.getSpeed(), ship.isUsed(), ship.getProdDate());
        ship.setRating(rating);

        return shipRepository.save(ship);
    }

    private double countRating(double speed, boolean isUsed, Date date) {
        double k = isUsed ? 0.5 : 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        double rating = (80 * speed * k) / (3019 - year + 1);

        return Math.round(rating * 100.0) / 100.0;
    }

    public boolean ifShipExists(Long id) {
        return shipRepository.existsById(id);
    }

    @Override
    public Specification<Ship> selectByName(String name) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (name == null) {
                    return null;
                }
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }

    @Override
    public Specification<Ship> selectByPlanet(String planet) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (planet == null) {
                    return null;
                }
                return criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
            }
        };
    }

    @Override
    public Specification<Ship> selectByShipType(ShipType shipType) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (shipType == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("shipType"), shipType);
            }
        };
    }

    @Override
    public Specification<Ship> selectByProdDate(Long after, Long before) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (after == null && before == null) {
                    return null;
                }
                if (after == null) {
                    Date tempDate = new Date(before);
                    return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), tempDate);
                }
                if (before == null) {
                    Date tempDate = new Date(after);
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), tempDate);
                }
//                Calendar beforeCalendar = new GregorianCalendar();
//                beforeCalendar.setTime(new Date(before));
//                beforeCalendar.set(Calendar.HOUR, 0);
//                beforeCalendar.add(Calendar.MILLISECOND, -1);

                Date tempAfter = new Date(after);
                Date tempBefore = new Date(before);

                return criteriaBuilder.between(root.get("prodDate"), tempAfter, tempBefore);
            }
        };
    }

    @Override
    public Specification<Ship> selectByUsed(Boolean isUsed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (isUsed == null) {
                    return null;
                }
                if (isUsed) {
                    return criteriaBuilder.isTrue(root.get("isUsed"));
                } else {
                    return criteriaBuilder.isFalse(root.get("isUsed"));
                }
            }
        };
    }

    @Override
    public Specification<Ship> selectBySpeed(Double minSpeed, Double maxSpeed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minSpeed == null && maxSpeed == null) {
                    return null;
                }
                if (minSpeed == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed);
                }
                if (maxSpeed == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed);
                }
                return criteriaBuilder.between(root.get("speed"), minSpeed, maxSpeed);
            }
        };
    }

    @Override
    public Specification<Ship> selectByCrew(Integer minCrew, Integer maxCrew) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minCrew == null && maxCrew == null) {
                    return null;
                }
                if (minCrew == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrew);
                }
                if (maxCrew == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrew);
                }
                return criteriaBuilder.between(root.get("crewSize"), minCrew, maxCrew);
            }
        };
    }

    @Override
    public Specification<Ship> selectByRating(Double minRating, Double maxRating) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (maxRating == null && minRating == null) {
                    return null;
                }
                if (minRating == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);
                }
                if (maxRating == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
                }
                return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
            }
        };
    }

}
