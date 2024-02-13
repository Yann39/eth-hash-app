package com.example.ethhashapp.repository;

import com.example.ethhashapp.entities.Diploma;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Yann39
 * @since 1.0.0
 */
public interface DiplomaRepository extends CrudRepository<Diploma, Long> {

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Diploma d WHERE d.id = :diplomaId")
    boolean isUsed(@Param("diplomaId") long diplomaId);

}