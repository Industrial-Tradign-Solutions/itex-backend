package com.itradingsolutions.itex.api.common.salesconsecutive.services.impl;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.entities.SalesConsecutiveFree;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.entities.SalesConsecutiveFreeId;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.entities.SalesConsecutiveSequence;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import com.itradingsolutions.itex.api.common.salesconsecutive.repositories.ISalesConsecutiveFreeRepository;
import com.itradingsolutions.itex.api.common.salesconsecutive.repositories.ISalesConsecutiveSequenceRepository;
import com.itradingsolutions.itex.api.common.salesconsecutive.services.ISalesConsecutiveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Default implementation of {@link ISalesConsecutiveService} using a high-water counter per type plus
 * a per-type free list for gap reuse.
 *
 * <p>Invariant (per type): at any time {@code used = {1..currentValue} \ freeList}, so
 * {@code MIN(freeList)} is exactly the first missing number. Every mutation (allocate/release) first
 * acquires the pessimistic write lock on that type's counter row, which serializes them and keeps the
 * invariant and the "lowest gap first" ordering intact under concurrency.</p>
 */
@Service
@AllArgsConstructor
public class SalesConsecutiveServiceImpl implements ISalesConsecutiveService {

    private final ISalesConsecutiveSequenceRepository sequenceRepository;
    private final ISalesConsecutiveFreeRepository freeRepository;

    @Override
    @Transactional
    public long generate(SalesConsecutiveType type) {
        SalesConsecutiveSequence sequence = lockSequence(type);

        Optional<Long> reused = freeRepository.findMinNumberByType(type);
        if (reused.isPresent()) {
            freeRepository.deleteById(new SalesConsecutiveFreeId(type, reused.get()));
            return reused.get();
        }

        long next = sequence.getCurrentValue() + 1;
        sequence.setCurrentValue(next);
        return next;
    }

    @Override
    @Transactional
    public void release(SalesConsecutiveType type, long number) {
        // Serialize against generate by holding the type's counter lock while touching the free list.
        lockSequence(type);

        SalesConsecutiveFreeId id = new SalesConsecutiveFreeId(type, number);
        if (!freeRepository.existsById(id)) {
            freeRepository.save(new SalesConsecutiveFree(id, ZonedDateTime.now()));
        }
    }

    private SalesConsecutiveSequence lockSequence(SalesConsecutiveType type) {
        return sequenceRepository.findByTypeForUpdate(type)
                .orElseThrow(() -> new IllegalStateException(
                        "Sales consecutive sequence not initialized for type " + type));
    }
}
