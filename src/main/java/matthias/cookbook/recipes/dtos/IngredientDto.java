package matthias.cookbook.recipes.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class IngredientDto {

    @Size(max = 128, message = "Ingredient name cannot be longer than 128 characters")
    @NotBlank(message = "Ingredient name cannot be null or empty")
    private String name;
}
