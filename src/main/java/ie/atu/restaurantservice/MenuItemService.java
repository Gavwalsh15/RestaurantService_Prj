package ie.atu.restaurantservice;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenuItemService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository; // Assuming you have a repository for Restaurant

    public MenuItem saveMenuItem(MenuItem menuItem, String restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        if (restaurant.isEmpty()) {
            throw new EntityNotFoundException("Restaurant with ID " + restaurantId + " not found.");
        }
        menuItem.setRestaurant(restaurant.get());
        return menuRepository.save(menuItem);
    }


}

