package com.itradingsolutions.itex.api.masters.brand.services.impl;

import com.itradingsolutions.itex.api.common.storage.service.IFileService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.brand.exceptions.NotBrandActiveException;
import com.itradingsolutions.itex.api.masters.brand.exceptions.NotExistBrandException;
import com.itradingsolutions.itex.api.masters.brand.exceptions.NotImportBrandExcelException;
import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandDTO;
import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandSupplierDTO;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandEntity;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandSupplierEntity;
import com.itradingsolutions.itex.api.masters.brand.models.entities.ids.BrandSupplierId;
import com.itradingsolutions.itex.api.masters.brand.models.filters.BrandFilter;
import com.itradingsolutions.itex.api.masters.brand.models.mappers.BrandMapper;
import com.itradingsolutions.itex.api.masters.brand.models.requests.AssignBrandSupplierListRequest;
import com.itradingsolutions.itex.api.masters.brand.models.requests.BrandRequest;
import com.itradingsolutions.itex.api.masters.brand.repositories.BrandRepository;
import com.itradingsolutions.itex.api.masters.brand.repositories.BrandSupplierRepository;
import com.itradingsolutions.itex.api.masters.brand.services.IBrandService;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import lombok.RequiredArgsConstructor;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl extends UtilServiceAbs implements IBrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final IFileService fileService;
    private final BrandSupplierRepository brandSupplierRepository;
    private final ISupplierService supplierService;

    @Override
    @Transactional(readOnly = true)
    public BrandEntity findEntityById(UUID id, boolean validateActive) {
        return getBrandById(id, validateActive);
    }

    @Override
    @Transactional
    public BrandDTO create(BrandRequest request) {
        return brandMapper.entityToDto(brandRepository.save(configNewEntity(request)));
    }

    @Override
    @Transactional
    public List<BrandDTO> createList(List<BrandRequest> request) {
        List<BrandEntity> list = request.stream().map(this::configNewEntity).toList();
        return brandRepository.saveAll(list).stream().map(brandMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDTO findById(UUID brandId, boolean validateActive) {
        return brandMapper.entityToDto(getBrandById(brandId, validateActive));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> listAll(Pageable pageable, BrandFilter filters) {
        Specification<BrandEntity> spec = (filters == null ? Specification.where(null) : filters.filterBrand());
        Page<BrandEntity> resp = brandRepository.findAll(spec, pageable);
        return new PageImpl<>(resp.getContent().stream().map(brandMapper::entityToDto).toList(),resp.getPageable(),resp.getTotalElements());
    }

    @Override
    @Transactional
    public void disable(UUID brandId) {
        var brand = getBrandById(brandId, true);
        brand.setActive(false);
        brandRepository.save(brand);
    }

    @Override
    @Transactional
    public void enable(UUID brandId) {
        var brand = getBrandById(brandId, false);
        if (brand.isActive()) throw new NotBrandActiveException(simpleMessage("brand.active"));
        brand.setActive(true);
        brandRepository.save(brand);
    }

    @Override
    public String convertBrandName(String brandName) {
        var name = removeSpecialChars(brandName.replace("_", "-"));
        name = name.replace("\u00A0", " ");
        name = name.replace("\n", " ");
        name = name.replace("  ", " ");
        name = name.replace("   ", " ");
        name = normalizeText(name);
        return name.toUpperCase().trim();
    }

    @Override
    @Transactional
    public Optional<BrandDTO> findByName(String brandName) {
        return brandRepository.findByName(brandName).map(brandMapper::entityToDto);
    }

    @Override
    public List<String> getListBrands(MultipartFile file) {
        fileService.validateFileExt(file.getOriginalFilename(), new String[]{IFileService.FILE_EXT_XLS, IFileService.FILE_EXT_XLSX});
        var filePath = fileService.uploadTempFile(file);
        List<String> listBrandNames = listBrandsByExcel(filePath);
        listBrandNames.sort(String::compareTo);
        listBrandNames = listBrandNames.stream().map(this::convertBrandName).distinct().collect(Collectors.toList());
        fileService.deleteTempFile(filePath);
        return listBrandNames;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> listAllBrands() {
        var resp = brandRepository.findAll();
        return resp.stream().map(brandMapper::entityToDto).toList();
    }

    @Override
    @Transactional
    public void assignBrandSupplier(UUID id, UUID supplierId) {
        brandSupplierRepository.deleteBySupplierIdAndBrandId(supplierId, id);
        var entity = getSupplierBrand(supplierId, id);

        brandSupplierRepository.save(entity);
    }

    @Override
    @Transactional
    public void assignBrandSupplierList(AssignBrandSupplierListRequest requests) {
        List<BrandSupplierEntity> list = new ArrayList<>();
        for (UUID brandId : requests.getBrandIds()) {
            var entity = getSupplierBrand(requests.getSupplierId(), brandId);
            list.add(entity);
        }
        brandSupplierRepository.deleteAll(list);
        brandSupplierRepository.saveAll(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandSupplierDTO> listSuppliersByBrandId(UUID brandId) {
        var items = brandSupplierRepository.fetchByBrandId(brandId);
        return items.stream().map(brandMapper::entityToDto).toList();
    }

    @Override
    @Transactional
    public void unAssignSupplier(UUID supplierId, UUID brandId) {
        brandSupplierRepository.deleteBySupplierIdAndBrandId(supplierId, brandId);
    }

    private BrandSupplierEntity getSupplierBrand(UUID supplierId, UUID brandId) {
        var brand = brandRepository.findById(brandId).orElseThrow();
        var supplier = supplierService.findSupplierById(supplierId, false);
        var entity = new BrandSupplierEntity();
        entity.setSupplier(supplier);
        entity.setBrand(brand);
        var idSupplierBrand = new BrandSupplierId();
        idSupplierBrand.setSupplierId(supplierId);
        idSupplierBrand.setBrandId(brandId);
        entity.setId(idSupplierBrand);
        return entity;
    }

    private List<String> listBrandsByExcel(Path excelFile) {
        try (FileInputStream fis = new FileInputStream(excelFile.toFile())){
            List<String> listBrandNames = new ArrayList<>();
            var ext = fileService.fileExtension(excelFile);
            Workbook workbook = ext.equalsIgnoreCase(IFileService.FILE_EXT_XLSX) ? new XSSFWorkbook(fis) : new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && !cell.getStringCellValue().trim().isBlank()) {
                    listBrandNames.add(cell.getStringCellValue());
                }
            }
            return listBrandNames;
        } catch (IOException ex) {
            throw new NotImportBrandExcelException(simpleMessage("brand.error.import-excel"), ex);
        }
    }

    private BrandEntity configNewEntity(BrandRequest request) {
        BrandEntity brand = new BrandEntity();
        brand.setActive(true);
        brand.setName(convertBrandName(request.getName()));
        return brand;
    }

    private BrandEntity getBrandById(UUID brandId, boolean validateActive) {
        var brand = brandRepository.findById(brandId).orElseThrow(() ->
                new NotExistBrandException(simpleMessage("brand.not.found"))
        );
        if (validateActive && !brand.isActive())
            throw new NotBrandActiveException(simpleMessage("brand.not.active"));
        return brand;
    }
}
