package matthias.cookbook.recipes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
interface RecipesMapper {

    RecipeDto toRecipeDto(RecipeEntity recipeEntity);

    @Mapping(target = "id", ignore = true)
    RecipeEntity fromRecipeDto(RecipeDto recipeDto);

    @Mapping(target = "id", ignore = true)
    RecipeEntity updateFromDto(@MappingTarget RecipeEntity recipe, RecipeDto recipeDto);

    List<RecipeDto> toRecipesDto(List<RecipeEntity> recipes);
}
