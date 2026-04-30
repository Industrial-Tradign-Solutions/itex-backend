package com.itradingsolutions.itex.api.ip.products.services.impl;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.products.exceptions.NotExistIpProductException;
import com.itradingsolutions.itex.api.ip.products.exceptions.NotOpenIpProductException;
import com.itradingsolutions.itex.api.ip.products.exceptions.NotOutSurplusException;
import com.itradingsolutions.itex.api.ip.products.exceptions.NotReplaceIpProductException;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpImportProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductSurplusDTO;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductEntity;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductSurplusEntity;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductSurplusType;
import com.itradingsolutions.itex.api.ip.products.models.filter.FilterListIpProducts;
import com.itradingsolutions.itex.api.ip.products.models.mappers.IpProductMapper;
import com.itradingsolutions.itex.api.ip.products.repositories.IIpProductRepository;
import com.itradingsolutions.itex.api.ip.products.repositories.IIpProductSurplusRepository;
import com.itradingsolutions.itex.api.ip.products.services.IIpProductService;
import com.itradingsolutions.itex.api.masters.brand.services.IBrandService;
import com.itradingsolutions.itex.api.masters.location.services.ICountryService;
import com.itradingsolutions.itex.api.partners.clients.exceptions.NotOpenClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IpProductServiceImpl extends UtilServiceAbs implements IIpProductService {

    private final IIpProductRepository ipProductRepository;
    private final IIpProductSurplusRepository ipProductSurplusRepository;
    private final IpProductMapper  ipProductMapper;

    private final ICountryService countryService;
    private final IBrandService brandService;
    private final IUserService userService;

    @Override
    @Transactional
    public IpProductDTO createIpProduct(IpProductDTO ipProductDTO, boolean isImported) {
        IpProductEntity entity = new IpProductEntity();
        entity.setStatus(IpProductStatus.ACTIVE);
        entity.setCreatedAt(ZonedDateTime.now(zoneId));
        if (!isImported) {
            entity.setOpenAt(ZonedDateTime.now(zoneId));
            entity.setOpenBy(userService.getUserAuthenticated());
        }
        return saveProductInfo(ipProductDTO, entity);
    }

    @Override
    @Transactional(readOnly = true)
    public IpProductDTO findIpProductById(UUID id) {
        var product = findById(id);
        return ipProductMapper.entityToDTO(product);
    }

    @Override
    @Transactional
    public IpProductDTO updateIpProductById(UUID id, IpProductDTO request) {
        var product = findById(id);
        validateSubstitutedProduct(product.getStatus());
        validateOpenProduct(product, userService.getUserAuthenticated());
        return saveProductInfo(request, product);
    }

    @Override
    @Transactional
    public IpProductDTO openAndLockIpProduct(UUID ipProductId, OpenAndLockType type) {
        var product = findById(ipProductId);
        if (product.getOpenBy() == null) {
            var user = userService.getUserAuthenticated();
            if (ipProductRepository.countByOpenUserId(user.getId()) >= maxTabsOpen)
                throw new NotOpenClientException(compositeMessage("ip.product.not-open-max", new String[]{maxTabsOpen.toString()}));
            if (type.equals(OpenAndLockType.EDIT)) {
                product.setOpenBy(user);
                product.setOpenAt(ZonedDateTime.now(zoneId));
                product = ipProductRepository.save(product);
            }
        }
        return ipProductMapper.entityToDTO(product);
    }

    @Override
    @Transactional
    public void unlockIpProduct(UUID ipProductId) {
        var product = findById(ipProductId);
        product.setOpenBy(null);
        product.setOpenAt(null);
        ipProductRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpProductDTO> listAllOpenIpProducts(String username) {
        List<IpProductEntity> list = ipProductRepository.fetchAllOpenByUsername(username);
        return list.stream().map(ipProductMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpProductDTO> listAllOpenIpProducts() {
        List<IpProductEntity> list = ipProductRepository.fetchAllOpen();
        return list.stream().map(ipProductMapper::entityToDTO).toList();
    }

    @Override
    @Transactional
    public IpProductDTO addSurplusIpProduct(UUID idProduct, IpProductSurplusDTO request) {
        var product = findById(idProduct);
        validateSubstitutedProduct(product.getStatus());
        validateOpenProduct(product, userService.getUserAuthenticated());
        IpProductSurplusEntity newSurplus = getIpProductSurplusEntity(request, product);
        newSurplus.setProduct(product);
        newSurplus.setType(IpProductSurplusType.IN);

        if (request.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            newSurplus.setQuantity(request.getQuantity().negate());
        } else {
            newSurplus.setQuantity(request.getQuantity());
        }

        if (product.getSurplus() == null) {
            product.setSurplus(new ArrayList<>());
        }
        product.getSurplus().add(ipProductSurplusRepository.save(newSurplus));

        return ipProductMapper.entityToDTO(product);
    }

    @Override
    @Transactional
    public IpProductDTO outSurplusIpProduct(UUID idProduct, IpProductSurplusDTO request) {
        var product = findById(idProduct);
        validateSubstitutedProduct(product.getStatus());
        validateOpenProduct(product, userService.getUserAuthenticated());
        IpProductSurplusEntity newSurplus = getIpProductSurplusEntity(request, product);
        newSurplus.setProduct(product);
        newSurplus.setType(IpProductSurplusType.OUT);

        if (request.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            newSurplus.setQuantity(request.getQuantity().negate());
        } else {
            newSurplus.setQuantity(request.getQuantity());
        }
        validateSurplus(product, newSurplus.getQuantity());
        if (product.getSurplus() == null) {
            product.setSurplus(new ArrayList<>());
        }
        product.getSurplus().add(ipProductSurplusRepository.save(newSurplus));

        return ipProductMapper.entityToDTO(product);
    }

    private void validateSurplus(IpProductEntity product, BigDecimal quantity) {
        if (product.getSurplus() == null) {
            throw new NotOutSurplusException(simpleMessage("ip.product.surplus.not-out"));
        }
        if (product.getSurplus().isEmpty()) {
            throw new NotOutSurplusException(simpleMessage("ip.product.surplus.not-out"));
        }
        var dto = ipProductMapper.entityToDTO(product);
        if (quantity.negate().compareTo(dto.getTotalSurplus()) > 0) {
            throw new NotOutSurplusException(simpleMessage("ip.product.surplus.not-out"));
        }
    }

    @Override
    @Transactional
    public IpProductDTO disableIpProductById(UUID id) {
        var product = findById(id);
        validateSubstitutedProduct(product.getStatus());
        validateOpenProduct(product, userService.getUserAuthenticated());
        product.setStatus(IpProductStatus.INACTIVE);
        return ipProductMapper.entityToDTO(ipProductRepository.save(product));
    }

    @Override
    @Transactional
    public IpProductDTO enableIpProductById(UUID id) {
        var product = findById(id);
        validateSubstitutedProduct(product.getStatus());
        validateOpenProduct(product, userService.getUserAuthenticated());
        product.setStatus(IpProductStatus.ACTIVE);
        return ipProductMapper.entityToDTO(ipProductRepository.save(product));
    }

    @Override
    @Transactional
    public IpProductDTO replaceProduct(UUID productId, UUID newProductId) {
        if (newProductId.equals(productId))
            throw new NotReplaceIpProductException(simpleMessage("ip.product.not-replace"));
        var product = findById(productId);
        validateSubstitutedProduct(product.getStatus());
        validateOpenProduct(product, userService.getUserAuthenticated());
        product.setStatus(IpProductStatus.SUBSTITUTED);
        product.setSubstituteProduct(findById(newProductId));
        return ipProductMapper.entityToDTO(ipProductRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IpProductDTO> listAllProducts(Pageable pageable, FilterListIpProducts filters) {
        Specification<IpProductEntity> spec = (filters == null ? Specification.where(null) : filters.filter());
        Page<IpProductEntity> resp = ipProductRepository.findAll(spec, pageable);
        return new PageImpl<>(resp.getContent().stream().map(ipProductMapper::entityToDTO).toList(),resp.getPageable(),resp.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpProductDTO> listAllActiveProducts() {
        var list = ipProductRepository.fetchAllByStatus(IpProductStatus.ACTIVE);
        return list.stream().map(ipProductMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IpProductEntity getProductById(UUID id) {
        return findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpImportProductDTO> validateImportProducts(List<IpImportProductDTO> items) {
        List<IpImportProductDTO> resp = new ArrayList<>();
        for (IpImportProductDTO item : items) {
            var newItem = ipProductMapper.clone(item);
            newItem.setImportErrors(new ArrayList<>());
            newItem.setValidImport(true);
            newItem.setSaveBrand(true);
            newItem.setSaveCoo(true);

            if (ipProductRepository.existsProductByDescription(item.getDescription(), item.getMfrReference())) {
                newItem.getImportErrors().add("This product already exists ");
                newItem.setValidImport(false);
            } else {
                var exist = resp.stream().anyMatch(p -> p.getDescription() != null && p.getMfrReference() != null
                                && p.getDescription().equalsIgnoreCase(item.getDescription()) && p.getMfrReference().equalsIgnoreCase(item.getMfrReference()));
                if (exist) {
                    newItem.getImportErrors().add("You are importing two or more products with the same description and MFR Ref");
                    newItem.setValidImport(false);
                }
            }

            if (item.getBrand() != null) {
                var brand = brandService.findByName(item.getBrand().getName());
                if (brand.isEmpty()) {
                    newItem.getImportErrors().add("This brand not exists");
                    newItem.setSaveBrand(false);
                }
            }

            if (item.getCoo() != null) {
                var coo = countryService.findByNameOrNameShort(item.getCoo().getNameShort());
                if (coo.isEmpty()) {
                    newItem.getImportErrors().add("This COO not exists");
                    newItem.setSaveCoo(false);
                }
            }

            resp.add(newItem);
        }
        return resp;
    }

    private static IpProductSurplusEntity getIpProductSurplusEntity(IpProductSurplusDTO request, IpProductEntity product) {
        IpProductSurplusEntity newSurplus = new IpProductSurplusEntity();
        newSurplus.setId(product.getId());
        newSurplus.setPrice(request.getPrice());
        newSurplus.setWhNumber(request.getWhNumber());
        newSurplus.setLocation(request.getLocation());
        return newSurplus;
    }

    private void validateSubstitutedProduct(IpProductStatus status) {
        if (status.equals(IpProductStatus.SUBSTITUTED))
            throw new NotReplaceIpProductException(simpleMessage("ip.product.not-replace"));
    }
    
    private void validateOpenProduct(IpProductEntity productEntity, UserEntity userAuthenticated) {
        if (productEntity.getOpenBy() == null)
            throw new NotOpenIpProductException(simpleMessage("ip.product.not-block"));
        if ( !productEntity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenIpProductException(compositeMessage("ip.product.not-block-by", new String[]{productEntity.getOpenBy().getFullName()}));
    }

    private IpProductEntity findById(UUID id) {
        return ipProductRepository.findById(id).orElseThrow(() -> new NotExistIpProductException(simpleMessage("ip.product.not-exist")));
    }

    private IpProductDTO saveProductInfo(IpProductDTO ipProductDTO, IpProductEntity productEntity) {

        if (ipProductDTO.getBrand() != null) {
            if (productEntity.getBrand() == null || !productEntity.getBrand().getId().equals(ipProductDTO.getBrand().getId())) {
                productEntity.setBrand(brandService.findEntityById(ipProductDTO.getBrand().getId(), true));
            }
        } else {
            productEntity.setBrand(null);
        }

        if (ipProductDTO.getCoo() != null) {
            if (productEntity.getCoo() == null || !productEntity.getCoo().getId().equals(ipProductDTO.getCoo().getId())) {
                productEntity.setCoo(countryService.findEntityById(ipProductDTO.getCoo().getId()));
            }
        } else {
            productEntity.setCoo(null);
        }

        productEntity.setDescription(ipProductDTO.getDescription());
        productEntity.setClientDescription(ipProductDTO.getClientDescription());
        productEntity.setMfrReference(ipProductDTO.getMfrReference());
        productEntity.setClientReference(ipProductDTO.getClientReference());
        productEntity.setNetWeightLbs(ipProductDTO.getNetWeightLbs());
        productEntity.setNmfc(ipProductDTO.getNmfc());
        productEntity.setFreightClass(ipProductDTO.getFreightClass());
        productEntity.setNotes(ipProductDTO.getNotes());
        productEntity.setKeywords(ipProductDTO.getKeywords());
        productEntity.setHtsScheduleBNumber(ipProductDTO.getHtsScheduleBNumber());
        productEntity.setEccn(ipProductDTO.getEccn());
        productEntity.setBattery(ipProductDTO.isBattery());
        productEntity.setHazmat(ipProductDTO.isHazmat());
        productEntity.setDualUse(ipProductDTO.isDualUse());

        var resp = ipProductRepository.save(productEntity);
        return ipProductMapper.entityToDTO(resp);
    }
}
