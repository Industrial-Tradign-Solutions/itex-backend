package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvalidInvoiceTransitionException;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Preconditions a draft must meet before it can become an official document (invoicing guide §4):
 * at least one product, a total that is worth billing, and a Client whose data is complete enough
 * to print — the same question {@link InvoiceClientValidator} asks at creation time, re-asked here
 * because the Client may have been edited in the meantime.
 *
 * <p>Products are read off {@code invoice.getProducts()}, the IP line items, matching what
 * {@link InvoiceAmountCalculator#productsTotal} does today; a second department persisting its
 * lines elsewhere needs both to go through the department resolver.</p>
 */
@Component
@RequiredArgsConstructor
public class InvoiceIssueValidator extends UtilServiceAbs {

    private final InvoiceClientValidator clientValidator;

    public void validate(InvoiceEntity invoice) {
        if (invoice.getProducts() == null || invoice.getProducts().isEmpty())
            throw new InvalidInvoiceTransitionException(simpleMessage("sales.invoice.issue-no-products"));

        var total = invoice.getTotalAmount();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidInvoiceTransitionException(simpleMessage("sales.invoice.issue-zero-total"));

        clientValidator.validate(invoice.getClient(), invoice.getClientContact());
    }
}
