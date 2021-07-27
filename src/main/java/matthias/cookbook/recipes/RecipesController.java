package matthias.cookbook.recipes;

import lombok.RequiredArgsConstructor;
import matthias.cookbook.recipes.dtos.CreateOrUpdateRecipeDto;
import matthias.cookbook.recipes.dtos.RecipeDto;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipes")
class RecipesController {

    private final RecipesService recipesService;

    @GetMapping
    public List<RecipeDto> getRecipes() {
        return recipesService.getRecipes();
    }

    @GetMapping("/{recipeId}")
    public RecipeDto getRecipe(@PathVariable String recipeId) {
        return recipesService.getRecipe(recipeId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public RecipeDto createRecipe(@RequestBody @Valid CreateOrUpdateRecipeDto recipeDto) {
        return recipesService.createRecipe(recipeDto);
    }

    @PutMapping("/{recipeId}")
    public RecipeDto updateRecipe(@PathVariable String recipeId, @RequestBody @Valid CreateOrUpdateRecipeDto recipeDto) {
        return recipesService.updateRecipe(recipeId, recipeDto);
    }

    @DeleteMapping("/{recipeId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteRecipe(@PathVariable String recipeId) {
        recipesService.deleteRecipe(recipeId);
    }
}
