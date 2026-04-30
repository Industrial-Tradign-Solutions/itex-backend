package com.itradingsolutions.itex.api.common.util.models.filter;

import com.itradingsolutions.itex.api.common.models.enums.FilterDate;
import jakarta.persistence.criteria.Expression;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public abstract class BaseFilter<E> {

    private String shortBy;
    private Integer shortOrder;
    private FilterDate date;
    private ZonedDateTime initDate;
    private ZonedDateTime endDate;

    public PageRequest getPageRequest(int page, int size) {
        var pageRequest = PageRequest.of(page, size);
        if (getShortBy() != null && !getShortBy().isEmpty()) {
            if (getShortOrder() != null && getShortOrder() == 1) {
                pageRequest = PageRequest.of(page, size, Sort.by(getShortBy()).ascending());
            } else {
                pageRequest = PageRequest.of(page, size, Sort.by(getShortBy()).descending());
            }
        }
        return pageRequest;
    }

    private static final String DEFAULT_CREATED_AT = "createdAt";

    protected Specification<E> hasInitDate() {
        return (root, query, criteriaBuilder) -> {
            ZonedDateTime startOfDay = getInitDate().toLocalDate().atStartOfDay(getInitDate().getZone());
            return criteriaBuilder.greaterThanOrEqualTo(root.get(DEFAULT_CREATED_AT), startOfDay);
        };
    }

    protected Specification<E> hasEndDate() {
        return (root, query, criteriaBuilder) -> {
            ZonedDateTime endOfDay = getEndDate().toLocalDate()
                    .atTime(LocalTime.MAX)
                    .atZone(getEndDate().getZone());
            return criteriaBuilder.lessThanOrEqualTo(root.get(DEFAULT_CREATED_AT), endOfDay);
        };
    }

    protected Specification<E> hasDate() {
        ZonedDateTime now = ZonedDateTime.now();

        return (root, query, cb) -> {
            if (getDate().equals(FilterDate.ALL))
                return cb.conjunction();

            Expression<ZonedDateTime> createdAt = root.get(DEFAULT_CREATED_AT);

            return switch (getDate()) {
                case DAY -> {
                    ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(now.getZone());
                    ZonedDateTime endOfDay = startOfDay.plusDays(1);
                    yield cb.between(createdAt, startOfDay, endOfDay);
                }
                case MONTH -> {
                    ZonedDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay(now.getZone());
                    ZonedDateTime endOfMonth = startOfMonth.plusMonths(1);
                    yield cb.between(createdAt, startOfMonth, endOfMonth);
                }
                case YEAR -> {
                    ZonedDateTime startOfYear = now.withDayOfYear(1).toLocalDate().atStartOfDay(now.getZone());
                    ZonedDateTime endOfYear = startOfYear.plusYears(1);
                    yield cb.between(createdAt, startOfYear, endOfYear);
                }
                default -> cb.conjunction();
            };
        };
    }

}
