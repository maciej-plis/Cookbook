package matthias.cookbook.test_common

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository

import java.lang.reflect.Field

import static java.util.Arrays.stream
import static java.util.Optional.ofNullable
import static java.util.UUID.randomUUID

class MongoInMemoryRepository<T, ID> implements MongoRepository<T, ID> {

    private static final String IMPLEMENT_WHEN_YOU_NEED_IT = "Implement when you need it"

    Map<ID, T> inMemory = new HashMap<>()

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save)
        return entities.toList()
    }

    @Override
    <S extends T> S save(S entity) {
        inMemory.put(getIdGenerateIfEmpty(entity), entity)
        return entity
    }

    @Override
    Optional<T> findById(ID id) {
        return ofNullable(inMemory.get(id))
    }

    @Override
    boolean existsById(ID id) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    List<T> findAll() {
        return inMemory.values().toList()
    }

    @Override
    Iterable<T> findAllById(Iterable<ID> ids) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    long count() {
        return inMemory.size()
    }

    @Override
    void deleteById(ID id) {
        inMemory.remove(id)
    }

    @Override
    void delete(T entity) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    void deleteAllById(Iterable<? extends ID> ids) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    void deleteAll(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    void deleteAll() {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    List<T> findAll(Sort sort) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    Page<T> findAll(Pageable pageable) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    <S extends T> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    <S extends T> List<S> insert(Iterable<S> entities) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    <S extends T> S insert(S entity) {
        inMemory.put(getIdGenerateIfEmpty(entity), entity)
        return entity
    }

    @Override
    <S extends T> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    <S extends T> long count(Example<S> example) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    @Override
    <S extends T> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException(IMPLEMENT_WHEN_YOU_NEED_IT)
    }

    private <S extends T> ID getIdGenerateIfEmpty(S entity) {
        Field idField = getIdField(entity)

        try {
            idField.setAccessible(true)
            if (idField.get(entity) == null) {
                idField.set(entity, randomUUID().toString())
            }
            return (ID) idField.get(entity)
        } catch (NullPointerException e) {
            throw new IllegalStateException("Couldn't find id field", e)
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Couldn't generate id", e)
        }
    }

    private <S extends T> Field getIdField(S entity) {
        return stream(entity.getClass().getDeclaredFields())
                .filter(field -> {
                    stream(field.getDeclaredAnnotations())
                            .anyMatch(annotation -> annotation.annotationType() == Id.class)
                })
                .findAny()
                .orElse(null)
    }
}
