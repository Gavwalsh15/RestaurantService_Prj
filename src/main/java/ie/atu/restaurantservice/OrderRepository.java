package ie.atu.restaurantservice;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    public List<Order> findAllByUsername(String username);
    public List<Order> findAllByRestaurantId(String restaurantId);
}
