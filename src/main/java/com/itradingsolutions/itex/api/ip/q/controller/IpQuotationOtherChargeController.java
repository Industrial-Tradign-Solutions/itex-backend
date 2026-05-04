package com.itradingsolutions.itex.api.ip.q.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.q.models.request.IpQuotationOtherChargeRequest;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationOtherChargeService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller for managing Other Charges in IP Quotations.
 * <p>
 * Base URL: {@code /itex/api/ip/q/{id_quotation}/other_charges}
 * </p>
 * <p>
 * All operations require {@code UPDATE_IP_QUOTATIONS} permission and automatically
 * register history entries (ADD_OTHER_CHARGE, UPDATE_OTHER_CHARGE, REMOVE_OTHER_CHARGE).
 * </p>
 */
@RestController
@RequestMapping("/ip/q/{id_quotation}/other_charges")
@Validated
@AllArgsConstructor
public class IpQuotationOtherChargeController extends CommonController {

    private final IIpQuotationHistoryService qHistoryService;
    private final IIpQuotationOtherChargeService otherChargeService;
    private final IpQuotationOtherChargeMapper otherChargeMapper;

    /**
     * Adds a new other charge to the quotation.
     * <p>
     * Validates that the quotation is in CREATED status and that the description is unique.
     * Automatically registers history with ADD_OTHER_CHARGE action.
     * </p>
     *
     * @param idQuotation the quotation ID
     * @param request the other charge data (description and value)
     * @return the created other charge
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationOtherChargeResponse>> addOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestBody @Valid IpQuotationOtherChargeRequest request
    ) {
        var dto = otherChargeMapper.requestToDTO(request);
        var resp = otherChargeService.create(dto, idQuotation);
        qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.ADD_OTHER_CHARGE, null, resp, idQuotation);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.q.other-charges.created"),
                        otherChargeMapper.dtoToResponse(resp)
                )
        );
    }

    /**
     * Updates an existing other charge.
     * <p>
     * Validates that the quotation is in CREATED status and that the description is unique.
     * Automatically registers history with UPDATE_OTHER_CHARGE action.
     * </p>
     *
     * @param idQuotation the quotation ID
     * @param idOtherCharge the other charge ID
     * @param request the updated other charge data
     * @return the updated other charge
     */
    @PutMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationOtherChargeResponse>> editOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_other_charge") UUID idOtherCharge,
            @RequestBody @Valid IpQuotationOtherChargeRequest request
    ) {
        var oldOtherCharge = otherChargeService.get(idOtherCharge, idQuotation);
        var dto = otherChargeMapper.requestToDTO(request);
        var resp = otherChargeService.update(dto, idOtherCharge, idQuotation);
        qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.UPDATE_OTHER_CHARGE, oldOtherCharge, resp, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.q.other-charges.updated"),
                        otherChargeMapper.dtoToResponse(resp)
                )
        );
    }

    /**
     * Retrieves an other charge by its ID.
     *
     * @param idQuotation the quotation ID
     * @param idOtherCharge the other charge ID
     * @return the other charge details
     */
    @GetMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<IpQuotationOtherChargeResponse> getOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_other_charge") UUID idOtherCharge
    ) {
        var otherCharge = otherChargeService.get(idOtherCharge, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(otherChargeMapper.dtoToResponse(otherCharge));
    }

    /**
     * Removes an other charge from the quotation.
     * <p>
     * Validates that the quotation is in CREATED status.
     * Automatically registers history with REMOVE_OTHER_CHARGE action.
     * </p>
     *
     * @param idQuotation the quotation ID
     * @param idOtherCharge the other charge ID
     * @return the ID of the removed other charge
     */
    @DeleteMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<UUID>> removeOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_other_charge") UUID idOtherCharge
    ) {
        var otherCharge = otherChargeService.get(idOtherCharge, idQuotation);
        otherChargeService.remove(idOtherCharge, idQuotation);
        qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.REMOVE_OTHER_CHARGE, null, otherCharge, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.q.other-charges.removed"),
                        idOtherCharge
                )
        );
    }
}
