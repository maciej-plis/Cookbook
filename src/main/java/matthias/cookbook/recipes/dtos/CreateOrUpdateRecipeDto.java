package matthias.cookbook.recipes.dtos;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateOrUpdateRecipeDto {

    @Size(max = 255, message = "Recipe name cannot be longer than 255 characters")
    @NotBlank(message = "Recipe name Cannot be null or empty")
    private String name;

    @Size(max = 1020, message = "Recipe description cannot be longer than 1020 characters")
    private String description;

    @NotEmpty(message = "Recipe ingredients cannot be empty")
    private List<@Valid IngredientDto> ingredients = new ArrayList<>();
}
