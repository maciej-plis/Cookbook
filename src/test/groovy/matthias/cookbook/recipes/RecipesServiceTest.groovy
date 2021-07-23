package matthias.cookbook.recipes

import matthias.cookbook.TestSpecification
import matthias.cookbook.common.exceptions.EntityNotFoundException
import spock.lang.Subject

import static org.mapstruct.factory.Mappers.getMapper

class RecipesServiceTest extends TestSpecification {

    RecipesRepository recipesRepository = new RecipesInMemoryRepository()

    @Subject
    RecipesService recipesService = new RecipesService(recipesRepository, getMapper(RecipesMapper))

    def "should return list of recipes"() {
        given:
            recipesRepository.saveAll([
                    new RecipeEntity(
                            id: "recipe 1 id",
                            name: "recipe 1 name",
                            description: "recipe 1 description",
                            ingredients: [new Ingredient(name: "recipe 1 ingredient")]
                    ),
                    new RecipeEntity(
                            id: "recipe 2 id",
                            name: "recipe 2 name",
                            description: "recipe 2 description"
                    )
            ])

        when:
            List<RecipeDto> result = recipesService.getRecipes()

        then:
            result.size() == 2
            result.containsAll(
                    new RecipeDto(
                            id: "recipe 1 id",
                            name: "recipe 1 name",
                            description: "recipe 1 description",
                            ingredients: [new IngredientDto(name: "recipe 1 ingredient")]
                    ),
                    new RecipeDto(
                            id: "recipe 2 id",
                            name: "recipe 2 name",
                            description: "recipe 2 description"
                    )
            )
    }

    def "should return recipe with given id"() {
        given:
            recipesRepository.saveAll([
                    new RecipeEntity(
                            id: sampleId,
                            name: "recipe 1 name",
                            description: "recipe 1 description",
                            ingredients: [new Ingredient(name: "recipe 1 ingredient")]
                    ),
                    new RecipeEntity(
                            id: "recipe 2 id",
                            name: "recipe 2 name",
                            description: "recipe 2 description"
                    )
            ])[0]

        when:
            RecipeDto result = recipesService.getRecipe(sampleId)

        then:
            with(result) {
                id == sampleId
                name == "recipe 1 name"
                description == "recipe 1 description"
                ingredients.size() == 1
                with(ingredients.get(0)) {
                    name == "recipe 1 ingredient"
                }
            }
    }

    def "should throw EntityNotFoundException when getting recipe and recipe with given id doesn't exist"() {
        given:
            recipesRepository.save(
                    new RecipeEntity(
                            name: "recipe 1 name",
                            description: "recipe 1 description"
                    )
            )

        when:
            recipesService.getRecipe(sampleId)

        then:
            thrown(EntityNotFoundException)
    }

    def "should create new recipe and return it"() {
        given:
            RecipeDto recipeDto = new RecipeDto(
                    name: "recipe name",
                    description: "recipe description",
                    ingredients: [
                            new IngredientDto(name: "recipe ingredient")
                    ]
            )

        when:
            RecipeDto result = recipesService.createRecipe(recipeDto)

        then:
            recipesRepository.count() == 1
            with(recipesRepository.findAll().get(0)) {
                name == "recipe name"
                description == "recipe description"
                ingredients.size() == 1
                with(ingredients.get(0)) {
                    name == "recipe ingredient"
                }
            }

        and:
            with(result) {
                id != null
                name == "recipe name"
                description == "recipe description"
                ingredients.size() == 1
                with(ingredients.get(0)) {
                    name == "recipe ingredient"
                }
            }
    }

    def "should update recipe with given id and return it"() {
        given:
            recipesRepository.save(new RecipeEntity(
                    id: sampleId,
                    name: "recipe name",
                    description: "recipe description",
                    ingredients: [
                            new Ingredient(name: "recipe ingredient")
                    ]
            ))

            RecipeDto recipeDto = new RecipeDto(
                    name: "updated recipe name",
                    description: "updated recipe description",
                    ingredients: [
                            new IngredientDto(name: "updated recipe ingredient name")
                    ]
            )

        when:
            RecipeDto result = recipesService.updateRecipe(sampleId, recipeDto)

        then:
            recipesRepository.count() == 1
            with(recipesRepository.findAll().get(0)) {
                name == "updated recipe name"
                description == "updated recipe description"
                ingredients.size() == 1
                with(ingredients.get(0)) {
                    name == "updated recipe ingredient name"
                }
            }

        and:
            with(result) {
                id != null
                name == "updated recipe name"
                description == "updated recipe description"
                ingredients.size() == 1
                with(ingredients.get(0)) {
                    name == "updated recipe ingredient name"
                }
            }
    }

    def "should throw EntityNotFoundException when updating recipe and recipe with given id doesn't exist"() {
        given:
            RecipeDto recipeDto = new RecipeDto(
                    name: "updated recipe name",
                    description: "updated recipe description",
                    ingredients: [
                            new IngredientDto(name: "updated ingredient name")
                    ]
            )

        when:
            recipesService.updateRecipe(sampleId, recipeDto)

        then:
            thrown(EntityNotFoundException)
    }

    def "should delete recipe with given id"() {
        given:
            recipesRepository.save(new RecipeEntity(
                    id: sampleId,
                    name: "recipe name",
                    description: "recipe description",
                    ingredients: [
                            new Ingredient(name: "ingredient name")
                    ]
            ))

        when:
            recipesService.deleteRecipe(sampleId)

        then:
            recipesRepository.count() == 0
    }

    def "shouldn't throw any exception when deleting recipe and recipe doesn't exist"() {
        when:
            recipesService.deleteRecipe(sampleId)

        then:
            noExceptionThrown()
            recipesRepository.count() == 0
    }
}
