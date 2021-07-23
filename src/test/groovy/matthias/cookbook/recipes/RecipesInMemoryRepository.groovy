package matthias.cookbook.recipes

import matthias.cookbook.test_common.MongoInMemoryRepository

class RecipesInMemoryRepository extends MongoInMemoryRepository<RecipeEntity, String> implements RecipesRepository {

}
