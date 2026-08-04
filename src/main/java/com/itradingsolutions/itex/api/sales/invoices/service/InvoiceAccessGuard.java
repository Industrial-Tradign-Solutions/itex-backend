package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceAccessDeniedException;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Single point of enforcement for the Invoice guide's scoping rule (guía §12): without
 * {@link ModuleAction#VIEW_ALL_INVOICE}, a user only sees/operates invoices where
 * {@code salesRep} is themselves, no exceptions. Every Invoice read/write flow that filters or
 * checks access delegates here instead of re-implementing the rule.
 */
@Component
@RequiredArgsConstructor
public class InvoiceAccessGuard extends UtilServiceAbs {

    private final IUserService userService;

    public boolean canViewAll() {
        return validateAction(userService.getUserAuthenticated(), ModuleAction.VIEW_ALL_INVOICE);
    }

    /**
     * Empty specification if the user can view all invoices; otherwise always restricts to
     * {@code salesRep = authenticated user}.
     */
    public Specification<InvoiceEntity> scope() {
        if (canViewAll())
            return Specification.where(null);

        UUID userId = userService.getUserAuthenticated().getId();
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("salesRep").get("id"), userId);
    }

    public void assertCanAccess(InvoiceEntity invoice) {
        if (canViewAll())
            return;

        var userId = userService.getUserAuthenticated().getId();
        if (invoice.getSalesRep() == null || !invoice.getSalesRep().getId().equals(userId))
            throw new InvoiceAccessDeniedException(simpleMessage("sales.invoice.access-denied"));
    }

    /**
     * Mutations (open-lock EDIT, update header, child add/edit/delete) are only allowed to the
     * assigned sales rep. {@link #assertCanAccess} already gates visibility; this narrows it further
     * so VIEW_ALL_INVOICE does not accidentally grant write access to foreign invoices.
     */
    public void assertCanMutate(InvoiceEntity invoice) {
        var userId = userService.getUserAuthenticated().getId();
        if (invoice.getSalesRep() == null || !invoice.getSalesRep().getId().equals(userId))
            throw new InvoiceAccessDeniedException(simpleMessage("sales.invoice.access-denied"));
    }
}
