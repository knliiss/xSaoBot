package dev.knalis.sao_telegram_bot.service.crud.intrf;

import dev.knalis.sao_telegram_bot.exception.EntityException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrudService<T, ID> {

    JpaRepository<T, ID> getRepo();

    default T save(T entity) {
        return getRepo().save(entity);
    }

    default void delete(ID id) throws EntityException.EntityNotFoundException {
        if (!existsById(id)) {
            throw new EntityException.EntityNotFoundException("Entity with id " + id + " not found");
        }
        getRepo().deleteById(id);
    }

    default T update(T entity) {
        return getRepo().save(entity);
    }

    default T findById(ID id) {
        return getRepo().findById(id).orElse(null);
    }

    default boolean existsById(ID id) {
        return getRepo().existsById(id);
    }

    default List<T> findAll() {
        return getRepo().findAll();
    }
}
