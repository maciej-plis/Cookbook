package matthias.cookbook.recipes;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RecipesRepository extends MongoRepository<RecipeEntity, String> {

}
