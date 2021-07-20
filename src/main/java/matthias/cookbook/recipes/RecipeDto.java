package matthias.cookbook.recipes;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class RecipeDto {

    @NotBlank
    @Max(1020)
    private String name;
    @Max(255)
    private String description;
    private List<IngredientDto> ingredients;
}
