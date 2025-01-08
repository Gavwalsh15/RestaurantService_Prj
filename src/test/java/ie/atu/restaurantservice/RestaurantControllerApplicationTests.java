package ie.atu.restaurantservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantRepository restaurantRepository;

    @MockBean
    private MenuItemService menuItemService;

    @MockBean
    private OrderRepository orderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
    }

    @Test
    void test_getAllRestaurants_ReturnListOfRestaurants() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setTitle("Test Restaurant");
        restaurant.setAddress("123 Main St");
        restaurant.setFoodType("Italian");

        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        mockMvc.perform(get("/api/restaurant/readRestaurant")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Restaurant"))
                .andExpect(jsonPath("$[0].address").value("123 Main St"))
                .andExpect(jsonPath("$[0].foodType").value("Italian"));

        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    void test_getMenuItems_ReturnMenuItemsForRestaurant() throws Exception {
        String restaurantId = "testrestaurant";
        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setName("Pizza");
        menuItem.setPrice(12.99);

        Restaurant restaurant = new Restaurant();
        restaurant.setMenuItems(List.of(menuItem));
        when(restaurantRepository.findByRestaurantId(restaurantId)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(get("/api/restaurant/getMenu/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Pizza"))
                .andExpect(jsonPath("$[0].price").value(12.99));

        verify(restaurantRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void test_createRestaurant_SaveAndReturnRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setTitle("Test Restaurant");
        restaurant.setAddress("123 Main St");
        restaurant.setImage("https://www.exampleimage.com");
        restaurant.setDescription("Test Restaurant Description");
        restaurant.setFoodType("Italian");

        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> {
            Restaurant saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        mockMvc.perform(post("/api/restaurant/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Restaurant"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.foodType").value("Italian"))
                .andExpect(jsonPath("$.image").value("https://www.exampleimage.com"))
                .andExpect(jsonPath("$.description").value("Test Restaurant Description"));

        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void test_addMenu_SaveAndReturnMenuItem() throws Exception {

        String restaurantId = "testrestaurant";
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Pasta");
        menuItem.setPrice(15.50);

        when(menuItemService.saveMenuItem(any(MenuItem.class), eq(restaurantId))).thenReturn(menuItem);

        mockMvc.perform(post("/api/restaurant/addMenu/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pasta"))
                .andExpect(jsonPath("$.price").value(15.50));

        verify(menuItemService, times(1)).saveMenuItem(any(MenuItem.class), eq(restaurantId));
    }

    @Test
    void test_addNewOrder_SaveAndReturnOrder() throws Exception {

        Order.OrderItem item1 = new Order.OrderItem("Burger", 10.50);
        Order.OrderItem item2 = new Order.OrderItem("Pizza", 15.75);

        List<Order.OrderItem> foodList = new ArrayList<>();
        foodList.add(item1);
        foodList.add(item2);

        Order order = new Order();
        order.setRestaurantId("testrestaurant");
        order.setUsername("JohnDoe");
        order.setCustomerAddress("123 Main St");
        order.setStatus(Order.OrderStatus.PENDING);
        order.setFoodSelected(foodList);
        order.setTotalPrice(item1.getPrice() + item2.getPrice());


        when(orderRepository.save(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/restaurant/addNewOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.restaurantId").value("testrestaurant"))
                .andExpect(jsonPath("$.customerAddress").value("123 Main St"))
                .andExpect(jsonPath("$.foodSelected").isArray())
                .andExpect(jsonPath("$.foodSelected[0].name").value("Burger"))
                .andExpect(jsonPath("$.foodSelected[0].price").value(10.50))
                .andExpect(jsonPath("$.foodSelected[1].name").value("Pizza"))
                .andExpect(jsonPath("$.foodSelected[1].price").value(15.75))
                .andExpect(jsonPath("$.username").value("JohnDoe"))
                .andExpect(jsonPath("$.totalPrice").value(26.25));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void test_getRestaurantOrders_ReturnOrdersForRestaurant() throws Exception {
        String restaurantId = "testrestaurant";
        Order order = new Order();
        order.setRestaurantId(restaurantId);
        order.setUsername("JohnDoe");
        order.setTotalPrice(25.00);

        when(orderRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(order));

        mockMvc.perform(get("/api/restaurant/get-orders-restaurant/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantId").value("testrestaurant"))
                .andExpect(jsonPath("$[0].username").value("JohnDoe"))
                .andExpect(jsonPath("$[0].totalPrice").value(25.00));

        verify(orderRepository, times(1)).findAllByRestaurantId(restaurantId);
    }
}
