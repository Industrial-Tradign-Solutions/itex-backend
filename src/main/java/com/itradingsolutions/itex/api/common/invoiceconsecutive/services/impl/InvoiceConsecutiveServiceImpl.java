package com.itradingsolutions.itex.api.common.invoiceconsecutive.services.impl;

import com.itradingsolutions.itex.api.common.invoiceconsecutive.models.entities.InvoiceConsecutiveFree;
import com.itradingsolutions.itex.api.common.invoiceconsecutive.models.entities.InvoiceConsecutiveSequence;
import com.itradingsolutions.itex.api.common.invoiceconsecutive.models.enums.InvoiceConsecutiveType;
import com.itradingsolutions.itex.api.common.invoiceconsecutive.repositories.IInvoiceConsecutiveFreeRepository;
import com.itradingsolutions.itex.api.common.invoiceconsecutive.repositories.IInvoiceConsecutiveSequenceRepository;
import com.itradingsolutions.itex.api.common.invoiceconsecutive.services.IInvoiceConsecutiveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Default implementation of {@link IInvoiceConsecutiveService} using a high-water counter per type
 * plus a free list for DRAFT gap reuse.
 *
 * <p>Invariant: at any time {@code used = {1..currentValue} \ freeList}, so {@code MIN(freeList)} is
 * exactly the first missing number. Every DRAFT mutation (allocate/release) first acquires the
 * pessimistic write lock on the DRAFT counter row, which serializes them and keeps the invariant and
 * the "lowest gap first" ordering intact under concurrency.</p>
 */
@Service
@AllArgsConstructor
public class InvoiceConsecutiveServiceImpl implements IInvoiceConsecutiveService {

    private final IInvoiceConsecutiveSequenceRepository sequenceRepository;
    private final IInvoiceConsecutiveFreeRepository freeRepository;

    @Override
    @Transactional
    public long generateDraft() {
        InvoiceConsecutiveSequence sequence = lockSequence(InvoiceConsecutiveType.DRAFT);

        Optional<Long> reused = freeRepository.findMinNumber();
        if (reused.isPresent()) {
            freeRepository.deleteById(reused.get());
            return reused.get();
        }

        return increment(sequence);
    }

    @Override
    @Transactional
    public long generateFinal() {
        InvoiceConsecutiveSequence sequence = lockSequence(InvoiceConsecutiveType.FINAL);
        return increment(sequence);
    }

    @Override
    @Transactional
    public void releaseDraft(long number) {
        // Serialize against generateDraft by holding the DRAFT counter lock while touching the free list.
        lockSequence(InvoiceConsecutiveType.DRAFT);

        if (!freeRepository.existsById(number)) {
            freeRepository.save(new InvoiceConsecutiveFree(number, ZonedDateTime.now()));
        }
    }

    private long increment(InvoiceConsecutiveSequence sequence) {
        long next = sequence.getCurrentValue() + 1;
        sequence.setCurrentValue(next);
        return next;
    }

    private InvoiceConsecutiveSequence lockSequence(InvoiceConsecutiveType type) {
        return sequenceRepository.findByTypeForUpdate(type)
                .orElseThrow(() -> new IllegalStateException(
                        "Invoice consecutive sequence not initialized for type " + type));
    }
}
