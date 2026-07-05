package com.itradingsolutions.itex.api.ip.q.service;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationHistoryDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing IP Quotation history records.
 */
public interface IIpQuotationHistoryService {

    /**
     * Records a history entry for a Quotation-level action (CREATE, UPDATE, CLONE, REJECTED, STATUS_CHANGE).
     *
     * @param action the action performed
     * @param oldDto the Quotation state before the action (null for CREATE)
     * @param newDto the Quotation state after the action
     */
    void addHistory(IpQuotationHistoryAction action, IpQuotationDTO oldDto, IpQuotationDTO newDto);

    /**
     * Records a history entry for a product-level action (ADD_PRODUCT, UPDATE_PRODUCT, REMOVE_PRODUCT).
     *
     * @param action the action performed
     * @param oldDto the product state before the action (null for ADD)
     * @param newDto the product state after the action (null for REMOVE)
     * @param quotationId the ID of the parent Quotation
     */
    void addHistoryProduct(IpQuotationHistoryAction action, IpQuotationProductDTO oldDto, IpQuotationProductDTO newDto, UUID quotationId);

    /**
     * Records a history entry for an other charge action (ADD_OTHER_CHARGE, UPDATE_OTHER_CHARGE, REMOVE_OTHER_CHARGE).
     *
     * @param action the action performed
     * @param oldDto the other charge state before the action (null for ADD)
     * @param newDto the other charge state after the action (null for REMOVE)
     * @param quotationId the ID of the parent Quotation
     */
    void addHistoryOtherCharge(IpQuotationHistoryAction action, IpQuotationOtherChargeDTO oldDto, IpQuotationOtherChargeDTO newDto, UUID quotationId);

    /**
     * Retrieves all history entries for a Quotation, ordered by most recent first.
     *
     * @param quotationId the ID of the Quotation
     * @return list of history DTOs
     */
    List<IpQuotationHistoryDTO> getHistoryById(UUID quotationId);
}
