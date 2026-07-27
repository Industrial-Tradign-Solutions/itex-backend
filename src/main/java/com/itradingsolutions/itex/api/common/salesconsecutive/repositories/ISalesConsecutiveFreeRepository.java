package com.itradingsolutions.itex.api.common.salesconsecutive.repositories;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.entities.SalesConsecutiveFree;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.entities.SalesConsecutiveFreeId;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISalesConsecutiveFreeRepository extends JpaRepository<SalesConsecutiveFree, SalesConsecutiveFreeId> {

    /**
     * Returns the lowest released number available for reuse for the given type, or an empty optional
     * when there are no released numbers of that type.
     *
     * @param type sequence type
     * @return the smallest released number of that type, if any
     */
    @Query("SELECT MIN(f.id.number) FROM SalesConsecutiveFree f WHERE f.id.type = ?1")
    Optional<Long> findMinNumberByType(SalesConsecutiveType type);
}
