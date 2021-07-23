package matthias.cookbook.recipes

import matthias.cookbook.TestSpecification
import matthias.cookbook.common.exceptions.EntityNotFoundException
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Unroll

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.emptyString
import static org.hamcrest.Matchers.hasSize
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipesControllerTest extends TestSpecification {

    RecipesService recipesService = Mock()

    def setup() {
        setupMvc(new RecipesController(recipesService))
    }

    def "should return 200 (OK) and list of recipes"() {
        given:
            1 * recipesService.getRecipes() >> [
                    new RecipeDto(
                            id: "recipe 1 id",
                            name: "recipe 1 name",
                            description: "recipe 1 description",
                            ingredients: [new IngredientDto(name: "recipe 1 ingredient")]
                    ),
                    new RecipeDto(
                            id: "recipe 2 id",
                            name: "recipe 2 name",
                            description: ""
                    )
            ]

        when:
            ResultActions result = mvc.perform(get("/recipes"))
                    .andDo(print())

        then:
            with(result) {
                andExpect(status().isOk())
                andExpect(jsonPath('$[*]', hasSize(2)))
                andExpect(jsonPath('$[0].id').value("recipe 1 id"))
                andExpect(jsonPath('$[0].name').value("recipe 1 name"))
                andExpect(jsonPath('$[0].description').value("recipe 1 description"))
                andExpect(jsonPath('$[0].ingredients[*]', hasSize(1)))
                andExpect(jsonPath('$[0].ingredients[0].name').value("recipe 1 ingredient"))
                andExpect(jsonPath('$[1].id').value("recipe 2 id"))
                andExpect(jsonPath('$[1].name').value("recipe 2 name"))
                andExpect(jsonPath('$[1].description', emptyString()))
                andExpect(jsonPath('$[1].ingredients[*]', hasSize(0)))
            }
    }

    def "should return 200 (OK) and recipe with given id"() {
        given:
            1 * recipesService.getRecipe(sampleId) >> new RecipeDto(
                    id: sampleId,
                    name: "recipe name",
                    description: "recipe description",
                    ingredients: [new IngredientDto(name: "recipe ingredient")]
            )

        when:
            ResultActions result = mvc.perform(get("/recipes/$sampleId"))
                    .andDo(print())

        then:
            with(result) {
                andExpect(status().isOk())
                andExpect(jsonPath('$.id').value(sampleId))
                andExpect(jsonPath('name').value("recipe name"))
                andExpect(jsonPath('$.description').value("recipe description"))
                andExpect(jsonPath('$.ingredients', hasSize(1)))
                andExpect(jsonPath('$.ingredients[0].name').value("recipe ingredient"))
            }
    }

    def "should return 404 (NOT_FOUND) when getting recipe and recipe with given id doesn't exist"() {
        given:
            1 * recipesService.getRecipe(sampleId) >> {
                throw new EntityNotFoundException("Recipe doesn't exist")
            }

        when:
            ResultActions result = mvc.perform(get("/recipes/$sampleId"))

        then:
            result.andExpect(status().isNotFound())
    }

    def "should return 201 (CREATED), create new recipe and return it"() {
        given:
            Map requestBody = [
                    name       : "created recipe name",
                    description: "created recipe description",
                    ingredients: [
                            [name: "created recipe ingredient"]
                    ]
            ]

            1 * recipesService.createRecipe(_ as RecipeDto) >> { RecipeDto recipeDto ->
                recipeDto.id = sampleId
                return recipeDto
            }

        when:
            ResultActions result = mvc.perform(post("/recipes")
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))
                    .andDo(print())

        then:
            with(result) {
                andExpect(status().isCreated())
                andExpect(jsonPath('$.id').value(sampleId))
                andExpect(jsonPath('$.name').value("created recipe name"))
                andExpect(jsonPath('$.description').value("created recipe description"))
                andExpect(jsonPath('$.ingredients', hasSize(1)))
                andExpect(jsonPath('$.ingredients[0].name').value("created recipe ingredient"))
            }
    }

    @Unroll
    def "should return 400 (BAD_REQUEST) when saving recipe and  recipe is not valid (#status)"() {
        given:
            Map requestBody = [
                    name       : recipeName,
                    description: recipeDescription,
                    ingredients: recipeIngredients
            ]

        when:
            ResultActions result = mvc.perform(post("/recipes")
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))
                    .andDo(print())

        then:
            result.andExpect(status().isBadRequest())

        where:
            recipeName | recipeDescription | recipeIngredients      || status
            ""         | "description"     | [[name: "ingredient"]] || "recipe name is empty"
            null       | "description"     | [[name: "ingredient"]] || "recipe name is null"
            "a" * 256  | "description"     | [[name: "ingredient"]] || "recipe name is to long"
            "name"     | "a" * 1021        | [[name: "ingredient"]] || "recipe description is too long"
            "name"     | "description"     | []                     || "ingredients are empty"
            "name"     | "description"     | [[name: ""]]           || "ingredient name is empty"
            "name"     | "description"     | [[name: null]]         || "ingredient name is null"
            "name"     | "description"     | [[name: "a" * 129]]    || "ingredient name is too long"
    }

    def "should return 200 (OK), update recipe with given id and return it"() {
        given:
            Map requestBody = [
                    name       : "updated recipe name",
                    description: "updated recipe description",
                    ingredients: [
                            [name: "updated recipe ingredient"]
                    ]
            ]

            1 * recipesService.updateRecipe(sampleId, _ as RecipeDto) >> { String recipeId, RecipeDto recipeDto ->
                recipeDto.id = recipeId
                return recipeDto
            }

        when:
            ResultActions result = mvc.perform(put("/recipes/$sampleId")
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))
                    .andDo(print())

        then:
            with(result) {
                andExpect(status().isOk())
                andExpect(jsonPath('$.id').value(sampleId))
                andExpect(jsonPath('$.name').value("updated recipe name"))
                andExpect(jsonPath('$.description').value("updated recipe description"))
                andExpect(jsonPath('$.ingredients', hasSize(1)))
                andExpect(jsonPath('$.ingredients[0].name').value("updated recipe ingredient"))
            }
    }

    def "should return 404 (NOT_FOUND) when updating recipe and recipe with given id doesn't exist"() {
        given:
            Map requestBody = [
                    name       : "updated recipe name",
                    description: "updated recipe description",
                    ingredients: [
                            [name: "updated recipe ingredient"]
                    ]
            ]

            1 * recipesService.updateRecipe(sampleId, _ as RecipeDto) >> {
                throw new EntityNotFoundException("Recipe doesn't exist")
            }

        when:
            ResultActions result = mvc.perform(put("/recipes/$sampleId")
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))
                    .andDo(print())

        then:
            result.andExpect(status().isNotFound())
    }

    @Unroll
    def "should return 400 (BAD_REQUEST) when updating recipe and recipe is not valid (#status)"() {
        given:
            Map requestBody = [
                    name       : recipeName,
                    description: recipeDescription,
                    ingredients: recipeIngredients
            ]

        when:
            ResultActions result = mvc.perform(put("/recipes/$sampleId")
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))
                    .andDo(print())

        then:
            result.andExpect(status().isBadRequest())

        where:
            recipeName | recipeDescription | recipeIngredients      || status
            ""         | "description"     | [[name: "ingredient"]] || "recipe name is empty"
            null       | "description"     | [[name: "ingredient"]] || "recipe name is null"
            "a" * 256  | "description"     | [[name: "ingredient"]] || "recipe name is to long"
            "name"     | "a" * 1021        | [[name: "ingredient"]] || "recipe description is too long"
            "name"     | "description"     | []                     || "ingredients are empty"
            "name"     | "description"     | [[name: ""]]           || "ingredient name is empty"
            "name"     | "description"     | [[name: null]]         || "ingredient name is null"
            "name"     | "description"     | [[name: "a" * 129]]    || "ingredient name is too long"
    }

    def "should return 204 (NO_CONTENT) and delete recipe with given id"() {
        when:
            ResultActions result = mvc.perform(delete("/recipes/$sampleId"))
                    .andDo(print())

        then:
            1 * recipesService.deleteRecipe(sampleId)
            with(result) {
                result.andExpect(status().isNoContent())
            }
    }
}