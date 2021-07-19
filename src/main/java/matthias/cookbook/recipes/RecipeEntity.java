package matthias.cookbook.recipes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "recipe")
public class RecipeEntity {

    @Id
    private String id;
    private String name;
    private String description;
    private List<Ingredient> ingredients;
}
