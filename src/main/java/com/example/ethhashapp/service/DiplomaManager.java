package com.example.ethhashapp.service;

import com.example.ethhashapp.entities.Diploma;
import com.example.ethhashapp.repository.DiplomaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yann39
 * @since 1.0.0
 */
@Service
public class DiplomaManager {

    private final DiplomaRepository diplomaRepository;

    public DiplomaManager(DiplomaRepository diplomaRepository) {
        this.diplomaRepository = diplomaRepository;
    }

    /**
     * Get all diplomas from the database.
     *
     * @return A list of {@link Diploma} elements representing the diplomas
     */
    public List<Diploma> getAll() {
        final List<Diploma> result = new ArrayList<>();
        diplomaRepository.findAll().forEach(result::add);
        return result;
    }

    /**
     * Check if the diploma represented by the specified {@code diplomaId} is used.
     *
     * @param diplomaId The {@link Diploma} ID
     * @return {@code true} if the diploma is used, {@code false} if not
     */
    public boolean isUsed(long diplomaId) {
        return diplomaRepository.isUsed(diplomaId);
    }

    /**
     * Save or update the specified {@code diploma} data.
     *
     * @param diploma The {@link Diploma} object to save or update
     */
    public void saveOrUpdate(Diploma diploma) {
        diplomaRepository.save(diploma);
    }

    /**
     * Remove the specified {@code diploma} from the database.
     *
     * @param diploma The {@link Diploma} to delete
     */
    public void delete(Diploma diploma) {
        diplomaRepository.delete(diploma);
    }

}