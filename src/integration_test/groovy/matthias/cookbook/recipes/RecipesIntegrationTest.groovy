package matthias.cookbook.recipes

import matthias.cookbook.IntegrationTestSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.ResultActions

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipesIntegrationTest extends IntegrationTestSpecification {

    @Autowired
    RecipesRepository recipesRepository

    def cleanup() {
        recipesRepository.deleteAll()
    }

    def "should return 200 (OK) and list of recipes"() {
        given:
            recipesRepository.insert([
                    new RecipeEntity(
                            name: "recipe 1 name",
                            description: "recipe 1 description",
                            ingredients: [new Ingredient(name: "recipe 1 ingredient")]
                    ),
                    new RecipeEntity(
                            name: "recipe 2 name",
                            description: ""
                    )
            ])

        when:
            ResultActions result = mvc.perform(get("/recipes"))
                    .andDo(print())

        then:
            with(result) {
                andExpect(status().isOk())
                andExpect(jsonPath('$[*]', hasSize(2)))
                andExpect(jsonPath('$[0].id').isNotEmpty())
                andExpect(jsonPath('$[0].name').value("recipe 1 name"))
                andExpect(jsonPath('$[0].description').value("recipe 1 description"))
                andExpect(jsonPath('$[0].ingredients[*]', hasSize(1)))
                andExpect(jsonPath('$[0].ingredients[0].name').value("recipe 1 ingredient"))
                andExpect(jsonPath('$[0].createdAt').isNotEmpty())
                andExpect(jsonPath('$[1].id').isNotEmpty())
                andExpect(jsonPath('$[1].name').value("recipe 2 name"))
                andExpect(jsonPath('$[1].description', emptyString()))
                andExpect(jsonPath('$[1].ingredients[*]', hasSize(0)))
                andExpect(jsonPath('$[1].createdAt').isNotEmpty())
            }

        and:
            recipesRepository.count() == 2
    }

    def "should return 200 (OK) and recipe with given id"() {
        given:
            sampleId = recipesRepository.insert(new RecipeEntity(
                    name: "recipe name",
                    description: "recipe description",
                    ingredients: [new Ingredient(name: "recipe ingredient")]
            )).id

        when:
            ResultActions result = mvc.perform(get("/recipes/$sampleId"))
                    .andDo(print())

        then:
            with(result) {
                andExpect(status().isOk())
                andExpect(jsonPath('$.id').value(sampleId))
                andExpect(jsonPath('$.name').value("recipe name"))
                andExpect(jsonPath('$.description').value("recipe description"))
                andExpect(jsonPath('$.ingredients', hasSize(1)))
                andExpect(jsonPath('$.ingredients[0].name').value("recipe ingredient"))
                andExpect(jsonPath('$.createdAt').isNotEmpty())
            }

        and:
            recipesRepository.count() == 1
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

        when:
            ResultActions result = mvc.perform(post("/recipes")
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))
                    .andDo(print())

        then:
            with(result) {
                andExpect(status().isCreated())
                andExpect(jsonPath('$.id', notNullValue()))
                andExpect(jsonPath('$.name').value("created recipe name"))
                andExpect(jsonPath('$.description').value("created recipe description"))
                andExpect(jsonPath('$.ingredients', hasSize(1)))
                andExpect(jsonPath('$.ingredients[0].name').value("created recipe ingredient"))
                andExpect(jsonPath('$.createdAt').isNotEmpty())
            }

        and:
            recipesRepository.count() == 1
            with(recipesRepository.findAll().get(0)) {
                id != null
                name == "created recipe name"
                description == "created recipe description"
                ingredients.size() == 1
                with(ingredients.get(0)) {
                    name == "created recipe ingredient"
                }
                createdAt != null
            }
    }

    def "should return 200 (OK), update recipe and return it"() {
        given:
            Map requestBody = [
                    name       : "updated recipe name",
                    description: "updated recipe description",
                    ingredients: [
                            [name: "updated recipe ingredient"]
                    ]
            ]

            sampleId = recipesRepository.insert(new RecipeEntity(
                    name: "recipe name",
                    description: "recipe description",
                    ingredients: [
                            new Ingredient(name: "recipe ingredient")
                    ]
            )).id

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
                andExpect(jsonPath('$.createdAt').isNotEmpty())
            }

        and:
            recipesRepository.count() == 1
            with(recipesRepository.findAll().get(0)) {
                id == sampleId
                name == "updated recipe name"
                description == "updated recipe description"
                ingredients.size() == 1
                with(ingredients.get(0)) {
                    name == "updated recipe ingredient"
                }
                createdAt != null
            }
    }

    def "should return 204 (NO_CONTENT) and delete recipe with given id"() {
        given:
            println(recipesRepository.findAll())
            recipesRepository.insert(new RecipeEntity(
                    id: sampleId,
                    name: "recipe name",
                    description: "recipe description",
                    ingredients: [
                            new Ingredient(name: "recipe ingredient")
                    ]
            ))

        when:
            ResultActions result = mvc.perform(delete("/recipes/$sampleId"))
                    .andDo(print())

        then:
            result.andExpect(status().isNoContent())

        and:
            recipesRepository.count() == 0
    }
}