package matthias.cookbook.recipes;

import lombok.RequiredArgsConstructor;
import matthias.cookbook.common.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
class RecipesService {

    private final RecipesRepository recipesRepository;
    private final RecipesMapper recipesMapper;

    List<RecipeDto> getRecipes() {
        return recipesMapper.toRecipesDto(recipesRepository.findAll());
    }

    RecipeDto getRecipe(String recipeId) {
        return recipesMapper.toRecipeDto(getRecipeOrThrow(recipeId));
    }

    RecipeDto createRecipe(RecipeDto recipeDto) {
        RecipeEntity createdRecipe = recipesRepository.insert(recipesMapper.fromRecipeDto(recipeDto));
        return recipesMapper.toRecipeDto(createdRecipe);
    }

    RecipeDto updateRecipe(String recipeId, RecipeDto recipeDto) {
        RecipeEntity recipe = getRecipeOrThrow(recipeId);
        RecipeEntity updatedRecipe = recipesRepository.save(recipesMapper.updateFromDto(recipe, recipeDto));
        return recipesMapper.toRecipeDto(updatedRecipe);
    }

    void deleteRecipe(String recipeId) {
        recipesRepository.deleteById(recipeId);
    }

    private RecipeEntity getRecipeOrThrow(String recipeId) {
        return recipesRepository.findById(recipeId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(format("Recipe with id %s doesn't exist", recipeId));
                });
    }
}
