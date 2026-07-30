package com.itradingsolutions.itex.api.sales.invoices.models.filters;

import com.itradingsolutions.itex.api.common.models.enums.FilterDate;
import com.itradingsolutions.itex.api.common.util.models.filter.BaseFilter;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class FilterListInvoice extends BaseFilter<InvoiceEntity> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "createdAt", "draftNumber", "number", "status", "totalAmount", "paidAmount", "dueAt"
    );

    private Long number;
    private Long draftNumber;
    private UUID clientId;
    private String remarks;
    private InvoiceStatus status;
    private UUID salesRepId;
    private String department;
    private Boolean overdue;
    private ZonedDateTime initDueAt;
    private ZonedDateTime endDueAt;

    @Override
    public PageRequest getPageRequest(int page, int size) {
        if (getShortBy() != null && SORTABLE_FIELDS.contains(getShortBy())) {
            var direction = (getShortOrder() != null && getShortOrder() == 1) ? Sort.Direction.ASC : Sort.Direction.DESC;
            return PageRequest.of(page, size, Sort.by(direction, getShortBy()));
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Specification<InvoiceEntity> filter() {
        Specification<InvoiceEntity> spec = Specification.where(null);

        if (getNumber() != null)
            spec = spec.and(hasNumber());

        if (getDraftNumber() != null)
            spec = spec.and(hasDraftNumber());

        if (getClientId() != null)
            spec = spec.and(hasClientId());

        if (getRemarks() != null && !getRemarks().isBlank())
            spec = spec.and(hasRemarks());

        if (getStatus() != null)
            spec = spec.and(hasStatus());

        if (getSalesRepId() != null)
            spec = spec.and(hasSalesRepId());

        if (getDepartment() != null && !getDepartment().isBlank())
            spec = spec.and(hasDepartment());

        if (getOverdue() != null)
            spec = spec.and(hasOverdue());

        if (getInitDueAt() != null)
            spec = spec.and(hasInitDueAt());

        if (getEndDueAt() != null)
            spec = spec.and(hasEndDueAt());

        if (getDate() != null) {
            if (getDate().equals(FilterDate.ALL)) {
                if (getInitDate() != null)
                    spec = spec.and(hasInitDate());

                if (getEndDate() != null)
                    spec = spec.and(hasEndDate());
            } else {
                spec = spec.and(hasDate());
            }
        }

        return spec;
    }

    private Specification<InvoiceEntity> hasNumber() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("number"), getNumber());
    }

    private Specification<InvoiceEntity> hasDraftNumber() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("draftNumber"), getDraftNumber());
    }

    private Specification<InvoiceEntity> hasClientId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("client").get("id"), getClientId());
    }

    private Specification<InvoiceEntity> hasRemarks() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("remarks")), "%" + getRemarks().toLowerCase() + "%");
    }

    private Specification<InvoiceEntity> hasStatus() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), getStatus());
    }

    private Specification<InvoiceEntity> hasSalesRepId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("salesRep").get("id"), getSalesRepId());
    }

    private Specification<InvoiceEntity> hasDepartment() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("department"), getDepartment());
    }

    private Specification<InvoiceEntity> hasOverdue() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("overdue"), getOverdue());
    }

    private Specification<InvoiceEntity> hasInitDueAt() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("dueAt"), getInitDueAt());
    }

    private Specification<InvoiceEntity> hasEndDueAt() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("dueAt"), getEndDueAt());
    }
}
