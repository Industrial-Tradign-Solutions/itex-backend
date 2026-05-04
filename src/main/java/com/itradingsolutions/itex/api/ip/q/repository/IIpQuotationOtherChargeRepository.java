package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link IpQuotationOtherChargeEntity} persistence.
 * <p>
 * Provides custom queries for fetching, checking existence, and deleting other charges
 * scoped to a specific quotation.
 * </p>
 */
@Repository
public interface IIpQuotationOtherChargeRepository extends JpaRepository<IpQuotationOtherChargeEntity, UUID> {

    /**
     * Fetches an other charge by its ID and quotation ID.
     *
     * @param id the other charge ID
     * @param quotationId the quotation ID
     * @return an Optional containing the entity if found
     */
    @Query("SELECT oc FROM IpQuotationOtherChargeEntity oc WHERE oc.id = ?1 AND oc.ipQuotation.id = ?2")
    Optional<IpQuotationOtherChargeEntity> fetchOneById(UUID id, UUID quotationId);

    /**
     * Checks if an other charge with the given description already exists for a quotation.
     *
     * @param description the description to check
     * @param quotationId the quotation ID
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(oc) > 0 THEN TRUE ELSE FALSE END FROM IpQuotationOtherChargeEntity oc WHERE oc.description = ?1 AND oc.ipQuotation.id = ?2")
    boolean existsDescription(String description, UUID quotationId);

    /**
     * Checks if an other charge with the given description exists for a quotation,
     * excluding a specific other charge ID (used during updates).
     *
     * @param description the description to check
     * @param quotationId the quotation ID
     * @param otherChargeId the other charge ID to exclude
     * @return true if exists (excluding the given ID), false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(oc) > 0 THEN TRUE ELSE FALSE END FROM IpQuotationOtherChargeEntity oc WHERE oc.description = ?1 AND oc.ipQuotation.id = ?2 AND oc.id <> ?3")
    boolean existsDescription(String description, UUID quotationId, UUID otherChargeId);

    /**
     * Deletes an other charge by quotation ID and other charge ID.
     *
     * @param quotationId the quotation ID
     * @param otherChargeId the other charge ID
     */
    @Modifying
    @Query("DELETE FROM IpQuotationOtherChargeEntity oc WHERE oc.ipQuotation.id = ?1 AND oc.id = ?2")
    void deleteById(UUID quotationId, UUID otherChargeId);
}
