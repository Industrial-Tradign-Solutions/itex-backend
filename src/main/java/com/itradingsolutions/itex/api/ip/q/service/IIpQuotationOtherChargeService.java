package com.itradingsolutions.itex.api.ip.q.service;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargeDTO;

import java.util.UUID;

/**
 * Service interface for managing Other Charges in IP Quotations.
 * <p>
 * Provides operations for creating, updating, retrieving, and removing
 * additional charges associated with quotations.
 * </p>
 */
public interface IIpQuotationOtherChargeService {

    /**
     * Creates a new other charge for the specified quotation.
     *
     * @param request the other charge data
     * @param quotationId the quotation ID
     * @return the created other charge DTO
     * @throws com.itradingsolutions.itex.api.ip.q.exceptions.IpQuotationOtherChargeExistException if an other charge with the same description already exists for this quotation
     * @throws com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException if the quotation does not exist
     */
    IpQuotationOtherChargeDTO create(IpQuotationOtherChargeDTO request, UUID quotationId);

    /**
     * Updates an existing other charge.
     *
     * @param request the updated other charge data
     * @param otherChargeId the other charge ID
     * @param quotationId the quotation ID
     * @return the updated other charge DTO
     * @throws com.itradingsolutions.itex.api.ip.q.exceptions.IpQuotationOtherChargeExistException if another other charge with the same description already exists for this quotation
     * @throws com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException if the other charge or quotation does not exist
     */
    IpQuotationOtherChargeDTO update(IpQuotationOtherChargeDTO request, UUID otherChargeId, UUID quotationId);

    /**
     * Retrieves an other charge by its ID and quotation ID.
     *
     * @param otherChargeId the other charge ID
     * @param quotationId the quotation ID
     * @return the other charge DTO
     * @throws com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException if the other charge does not exist
     */
    IpQuotationOtherChargeDTO get(UUID otherChargeId, UUID quotationId);

    /**
     * Removes an other charge from a quotation.
     *
     * @param otherChargeId the other charge ID
     * @param quotationId the quotation ID
     * @throws com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException if the other charge does not exist
     */
    void remove(UUID otherChargeId, UUID quotationId);
}
