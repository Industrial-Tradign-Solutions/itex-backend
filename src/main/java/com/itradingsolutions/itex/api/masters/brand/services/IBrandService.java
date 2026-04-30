package com.itradingsolutions.itex.api.masters.brand.services;

import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandDTO;
import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandSupplierDTO;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandEntity;
import com.itradingsolutions.itex.api.masters.brand.models.filters.BrandFilter;
import com.itradingsolutions.itex.api.masters.brand.models.requests.AssignBrandSupplierListRequest;
import com.itradingsolutions.itex.api.masters.brand.models.requests.BrandRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBrandService {

    BrandEntity findEntityById(UUID id, boolean validateActive);
    BrandDTO create(BrandRequest request);
    List<BrandDTO> createList(List<BrandRequest> request);
    BrandDTO findById(UUID id, boolean validateActive);
    Page<BrandDTO> listAll(Pageable pageable, BrandFilter filters);
    void disable(UUID id);
    void enable(UUID id);
    String convertBrandName(String brandName);
    Optional<BrandDTO> findByName(String brandName);
    List<String> getListBrands(MultipartFile file);
    List<BrandDTO> listAllBrands();
    void assignBrandSupplier(UUID id, UUID supplierId);
    void assignBrandSupplierList(AssignBrandSupplierListRequest requests);
    List<BrandSupplierDTO> listSuppliersByBrandId(UUID brandId);
    void unAssignSupplier(UUID supplierId, UUID brandId);
}
