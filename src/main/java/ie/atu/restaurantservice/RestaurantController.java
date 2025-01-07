package ie.atu.restaurantservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    public RestaurantController(RestaurantRepository restaurantRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
    }

    @GetMapping("/readRestaurant")
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getMenu/{restaurantId}")
    public List<MenuItem> getMenuItems(@PathVariable String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId.toLowerCase().replaceAll(" ", ""));
    }

    @PostMapping("/create")
    public Restaurant createRestaurant(@Valid @RequestBody Restaurant restaurant) {
        restaurant.setRestaurantId(restaurant.getTitle().toLowerCase().replaceAll(" ", ""));
        return restaurantRepository.save(restaurant);
    }

    @PostMapping("/addMenu")
    public MenuItem addMenu(@Valid @RequestBody MenuItem menuItem) {
        return menuRepository.save(menuItem);
    }

    //for reviewService
    @GetMapping("/{id}")
    public ResponseEntity<String> getRestaurantNameById(@PathVariable String id) {
        // Find restaurant by ID
        Optional<Restaurant> restaurant = restaurantRepository.findById(Long.valueOf(id));

        // Return the restaurant name if found, or 404 Not Found
        return restaurant.map(r -> ResponseEntity.ok(r.getTitle()))
                .orElse(ResponseEntity.notFound().build());
    }

}
