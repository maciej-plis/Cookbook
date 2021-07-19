package matthias.cookbook.recipes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipes")
public class RecipesController {

    private final RecipesService recipesService;

    @GetMapping
    public List<RecipeEntity> getRecipes() {
        return recipesService.getRecipes();
    }

    @PostMapping
    public void addRecipe(@RequestBody RecipeDto recipeDto) {
        recipesService.addRecipe(recipeDto);
    }

    @GetMapping("/{recipeId}")
    public RecipeEntity getRecipe(@PathVariable String recipeId) {
        return recipesService.getRecipe(recipeId);
    }

    @PutMapping("/{recipeId}")
    public void editRecipe(@PathVariable String recipeId, @RequestBody RecipeDto recipeDto) {
        recipesService.editRecipe(recipeId, recipeDto);
    }

    @DeleteMapping("/{recipeId}")
    public void deleteRecipe(@PathVariable String recipeId) {
        recipesService.deleteRecipe(recipeId);
    }
}
