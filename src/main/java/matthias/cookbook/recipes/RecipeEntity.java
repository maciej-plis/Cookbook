package matthias.cookbook.recipes;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "recipe")
class RecipeEntity {

    @Id
    private String id;
    private String name;
    private String description;
    private List<Ingredient> ingredients = new ArrayList<>();
    @CreatedDate
    private Instant createdAt;
}
