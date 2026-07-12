package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderOtherChargesQuotationQrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPurchaseOrderOtherChargesQuotationQrRepository extends JpaRepository<PurchaseOrderOtherChargesQuotationQrEntity, UUID> {
}
