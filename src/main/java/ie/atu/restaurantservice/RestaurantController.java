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
    private final OrderRepository orderRepository;

    public RestaurantController(RestaurantRepository restaurantRepository, MenuItemService menuItemService, OrderRepository orderRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemService = menuItemService;
        this.orderRepository = orderRepository;
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
        return new ResponseEntity<>(orderRepository.save(order), HttpStatus.CREATED);
    }

    @GetMapping("/get-orders-restaurant/{restaurantId}")
    public List<Order> getRestaurantOrders(@PathVariable String restaurantId) {
        return orderRepository.findAllByRestaurantId(restaurantId);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/get-orders-customer/{username}")
    public List<Order> getCustomersOrder(@PathVariable String username) {
        return orderRepository.findAllByUsername(username);
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
