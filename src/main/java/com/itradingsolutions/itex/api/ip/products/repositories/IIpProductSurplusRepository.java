package com.itradingsolutions.itex.api.ip.products.repositories;

import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductSurplusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IIpProductSurplusRepository extends JpaRepository<IpProductSurplusEntity, UUID> {
}
