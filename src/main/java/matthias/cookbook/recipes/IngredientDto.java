package matthias.cookbook.recipes;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;

@Getter
@Setter
public class IngredientDto {

    @Max(128)
    private String name;
}
