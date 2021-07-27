package matthias.cookbook.recipes;

import matthias.cookbook.recipes.dtos.CreateOrUpdateRecipeDto;
import matthias.cookbook.recipes.dtos.RecipeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
interface RecipesMapper {

    RecipeDto toRecipeDto(RecipeEntity recipeEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RecipeEntity fromRecipeDto(CreateOrUpdateRecipeDto recipeDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RecipeEntity updateFromDto(@MappingTarget RecipeEntity recipe, CreateOrUpdateRecipeDto recipeDto);

    List<RecipeDto> toRecipesDto(List<RecipeEntity> recipes);
}
