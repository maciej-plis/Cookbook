package matthias.cookbook.recipes.dtos;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeDto {

    private String id;
    private String name;
    private String description;
    private List<IngredientDto> ingredients = new ArrayList<>();
    private Instant createdAt;
}
