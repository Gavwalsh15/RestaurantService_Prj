package ie.atu.restaurantservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final MenuItemService menuItemService;

    public RestaurantController(RestaurantRepository restaurantRepository, MenuItemService menuItemService) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemService = menuItemService;
    }

    @GetMapping("/readRestaurant")
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }


    @GetMapping("/getMenu/{restaurantId}")
    @CrossOrigin(origins = "http://localhost:3000")
    public List<MenuItem> getMenuItems(@PathVariable String restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findByRestaurantId(restaurantId.toLowerCase().replace(" ", ""));

        if (restaurantOptional.isPresent()) {
            return restaurantOptional.get().getMenuItems();
        } else {
            return Collections.emptyList();
        }
    }


    @PostMapping("/create")
    public Restaurant createRestaurant(@Valid @RequestBody Restaurant restaurant) {
        restaurant.setRestaurantId(restaurant.getTitle().toLowerCase().replace(" ", ""));
        return restaurantRepository.save(restaurant);
    }

    @PostMapping("/addMenu/{restaurantId}")
    public MenuItem createMenuItem(@RequestBody MenuItem menuItem, @PathVariable String restaurantId) {
        return menuItemService.saveMenuItem(menuItem, restaurantId);
    }

    @PostMapping("/addNewOrder")
    public ResponseEntity<Order> addNewOrder(@Valid @RequestBody Order order) {

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
