package matthias.cookbook.recipes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
interface RecipesMapper {

    RecipeDto toRecipeDto(RecipeEntity recipeEntity);

    @Mapping(target = "id", ignore = true)
    RecipeEntity fromRecipeDto(RecipeDto recipeDto);

    @Mapping(target = "id", ignore = true)
    RecipeEntity updateFromDto(@MappingTarget RecipeEntity recipe, RecipeDto recipeDto);
}
