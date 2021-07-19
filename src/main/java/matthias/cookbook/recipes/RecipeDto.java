package matthias.cookbook.recipes;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipeDto {


    private String name;
    private String description;
    private List<Ingredient> ingredients;
}
