package matthias.cookbook.test_common

import matthias.cookbook.TestSpecification
import spock.lang.Subject

class MongoInMemoryRepositoryTest extends TestSpecification {

    @Subject
    MongoInMemoryRepository<TestEntity, String> mongoRepository = new MongoInMemoryRepository<>()

    def "should save entity and keep original id"() {
        given:
            TestEntity entity = new TestEntity(id: sampleId)

        when:
            mongoRepository.save(entity)

        then:
            with(mongoRepository.inMemory.values()) {
                size() == 1
                getAt(0) == entity
            }
    }

    def "should save entity and generate id when null"() {
        given:
            TestEntity entity = new TestEntity()

        when:
            mongoRepository.save(entity)

        then:
            with(mongoRepository.inMemory.values()) {
                size() == 1
                getAt(0).id != null
                getAt(0) == entity
            }
    }

    def "should update existing entity when id already exists"() {
        given:
            TestEntity oldEntity = new TestEntity(id: sampleId)
            mongoRepository.inMemory.put(sampleId, oldEntity)
            TestEntity updatedEntity = new TestEntity(id: sampleId)

        when:
            mongoRepository.save(updatedEntity)

        then:
            with(mongoRepository.inMemory.values()) {
                size() == 1
                !contains(oldEntity)
                contains(updatedEntity)
            }
    }

    def "should save all entities and generate id when null"() {
        given:
            List<TestEntity> entities = [
                    new TestEntity(id: sampleId),
                    new TestEntity(),
                    new TestEntity(),
            ]

        when:
            mongoRepository.saveAll(entities)

        then:
            with(mongoRepository.inMemory.values()) {
                size() == 3
                it.containsAll(entities)
                it.every(entity -> entity.getId() != null)
            }
    }

    def "should return entity with given id"() {
        given:
            TestEntity entity = new TestEntity(id: sampleId)
            mongoRepository.inMemory.put(sampleId, entity)

        when:
            Optional<TestEntity> result = mongoRepository.findById(sampleId)

        then:
            result.isPresent()
            result.get() == entity
    }

    def "should return all entities"() {
        given:
            List<TestEntity> entities = [
                    new TestEntity(id: sampleId),
                    new TestEntity(id: "sampleId2"),
                    new TestEntity(id: "sampleId3")
            ]
            mongoRepository.inMemory.putAll(entities.collectEntries({ [it.id, it] }))

        expect:
            with(mongoRepository.findAll()) {
                it.size() == 3
                it.containsAll(entities)
            }
    }

    def "should return empty optional when entity doesn't exist"() {
        expect:
            !mongoRepository.findById(sampleId).isPresent()
    }

    def "should return entities count"() {
        given:
            mongoRepository.saveAll([
                    new TestEntity(),
                    new TestEntity()
            ])

        expect:
            mongoRepository.count() == 2
    }

    def "should delete entity by id"() {
        given:
            TestEntity toDelete = new TestEntity(id: sampleId)
            mongoRepository.saveAll([
                    toDelete,
                    new TestEntity()
            ])

        when:
            mongoRepository.deleteById(sampleId)

        then:
            with(mongoRepository.inMemory.values()) {
                size() == 1
                !contains(toDelete)
            }
    }

    def "should insert new entity"() {
        given:
            TestEntity entity = new TestEntity()

        when:
            mongoRepository.insert(entity)

        then:
            with(mongoRepository.inMemory.values()) {
                size() == 1
                getAt(0).id != null
                getAt(0) == entity
            }
    }
}
