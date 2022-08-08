package repositories;

import exceptions.ResourceAlreadyExistsException;
import exceptions.ResourceDoesNotExistException;
import models.WebAppObject;

import java.util.Optional;


public interface RepositoryInterface<T extends WebAppObject, U> {
    /**
     * @param id The id or username to identify what object to get from the database
     * @return An Optional object
     */
    Optional<T> get(U id);

    /**
     * @param obj The object to be updated
     * @throws ResourceDoesNotExistException
     */
    void update(T obj) throws ResourceDoesNotExistException;

    /**
     * Inserts an object into the database
     * @param obj The object to be inserted
     * @throws ResourceAlreadyExistsException If the object already exists in the databases
     */
    void insert(T obj) throws ResourceAlreadyExistsException;

}
