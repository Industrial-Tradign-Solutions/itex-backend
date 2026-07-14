package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargesQuotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IIpPurchaseOrderOtherChargesQuotationRepository extends JpaRepository<IpPurchaseOrderOtherChargesQuotationEntity, UUID> {
}
