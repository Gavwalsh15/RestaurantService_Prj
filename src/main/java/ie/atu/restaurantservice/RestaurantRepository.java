package ie.atu.restaurantservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // Use Optional to handle the possibility of a missing entity
    Optional<Restaurant> findByRestaurantId(String restaurantId);
}

