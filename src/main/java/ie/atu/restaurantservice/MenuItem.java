package ie.atu.restaurantservice;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "menu_items")
public class MenuItem {

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("price")
    private String price;

    @Field("restaurant_id")
    private String restaurantId; // Foreign key reference to the Restaurant in JPA
}
