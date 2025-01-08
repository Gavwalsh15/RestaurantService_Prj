package ie.atu.restaurantservice;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @NotBlank(message = "Restaurant ID is required.")
    private String restaurantId;

    @NotEmpty(message = "Order must contain at least one menu item.")
    private List<OrderItem> foodSelected;

    @NotBlank(message = "Customer name is required.")
    private String username;

    private String customerAddress;

    private OrderStatus status;

    @Positive(message = "Total price must be greater than zero.")
    private double totalPrice;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        DELIVERED,
        CANCELLED
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem {

        @NotBlank(message = "Item name is required.")
        private String name;

        @Positive(message = "Price must be greater than zero.")
        @Min(value = 0, message = "Price cannot be negative.") // Optional, depending on your requirements
        private double price;
    }
}
