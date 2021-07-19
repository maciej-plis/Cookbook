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

    List<RecipeEntity> getRecipes() {
        return recipesRepository.findAll();
    }

    RecipeEntity getRecipe(String recipeId) {
        return getRecipeOrThrow(recipeId);
    }

    public void addRecipe(RecipeDto recipeDto) {
        recipesRepository.insert(recipesMapper.fromRecipeDto(recipeDto));
    }

    public void editRecipe(String recipeId, RecipeDto recipeDto) {
        RecipeEntity recipe = getRecipeOrThrow(recipeId);
        RecipeEntity updatedRecipe = recipesMapper.updateFromDto(recipe, recipeDto);
        recipesRepository.save(updatedRecipe);
    }

    public void deleteRecipe(String recipeId) {
        recipesRepository.deleteById(recipeId);
    }

    private RecipeEntity getRecipeOrThrow(String recipeId) {
        return recipesRepository.findById(recipeId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(format("Recipe with id %s doesn't exist", recipeId));
                });
    }
}
