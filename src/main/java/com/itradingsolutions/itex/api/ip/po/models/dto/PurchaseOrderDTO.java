package com.itradingsolutions.itex.api.ip.po.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PurchaseOrderDTO extends BaseDTO {

    private String number ;
    private PurchaseOrderStatus status ;
    private Currency currency;
    private IpQuotationDTO quotation;
    private ClientDTO client;
    private ClientContactDTO clientContact;
    private String clientPoNumber;
    private SupplierDTO supplier;
    private SupplierContactDTO supplierContact;
    private String supplierPoNumber;
    private UserDTO salesRep;
    private PaymentTerms paymentTerms;
    private Integer leadTime;
    private LeadTime leadTimeType;
    private String shippingMethod;
    private String remarks;
    private String internalRemarks;
    private String shipToName;
    private String shipToAddress;
    private CityDTO shipToCity;
    private String shipToPhone;
    private String shipToContactName;
    private String shipToEmail;
    private BigDecimal salesTax;
    private String pdfUrl;
    private UserDTO openBy;
    private ZonedDateTime openAt;
    private ZonedDateTime sentAt;
    private ZonedDateTime answeredAt;
    private ZonedDateTime completeAt;
    private ZonedDateTime rejectAt;
    private List<PurchaseOrderProductDTO> products;
    private List<PurchaseOrderOtherChargeDTO> otherCharges;
    private List<PurchaseOrderOtherChargesQuotationDTO> importedQuotationCharges;
    private List<PurchaseOrderOtherChargesQuotationQrDTO> importedQuoteRequestCharges;

    public String getName() {
        return this.number;
    }

    public void setNumber(String number) {
        if (number != null)
            this.number = number.trim().toUpperCase();
    }

    public void setClientPoNumber(String clientPoNumber) {
        if (clientPoNumber != null)
            this.clientPoNumber = clientPoNumber.trim().toUpperCase();
    }

    public void setSupplierPoNumber(String supplierPoNumber) {
        if (supplierPoNumber != null)
            this.supplierPoNumber = supplierPoNumber.trim().toUpperCase();
    }

    public void setShippingMethod(String shippingMethod) {
        if (shippingMethod != null)
            this.shippingMethod = shippingMethod.trim();
    }

    public void setRemarks(String remarks) {
        if (remarks != null)
            this.remarks = remarks.trim();
    }

    public void setInternalRemarks(String internalRemarks) {
        if (internalRemarks != null)
            this.internalRemarks = internalRemarks.trim();
    }

    public void setShipToName(String shipToName) {
        if (shipToName != null)
            this.shipToName = shipToName.trim();
    }

    public void setShipToAddress(String shipToAddress) {
        if (shipToAddress != null)
            this.shipToAddress = shipToAddress.trim();
    }

    public void setShipToPhone(String shipToPhone) {
        if (shipToPhone != null)
            this.shipToPhone = shipToPhone.trim();
    }

    public void setShipToContactName(String shipToContactName) {
        if (shipToContactName != null)
            this.shipToContactName = shipToContactName.trim();
    }

    public void setShipToEmail(String shipToEmail) {
        if (shipToEmail != null)
            this.shipToEmail = shipToEmail.trim();
    }

    public void setQuotationId(UUID quotationId) {
        if (quotationId != null) {
            this.quotation = new IpQuotationDTO();
            this.quotation.setId(quotationId);
        }
    }

    public void setClientId(UUID clientId) {
        if (clientId != null) {
            this.client = new ClientDTO();
            this.client.setId(clientId);
        }
    }

    public void setClientContactId(UUID clientContactId) {
        if (clientContactId != null) {
            this.clientContact = new ClientContactDTO();
            this.clientContact.setId(clientContactId);
        }
    }

    public void setSupplierId(UUID supplierId) {
        if (supplierId != null) {
            this.supplier = new SupplierDTO();
            this.supplier.setId(supplierId);
        }
    }

    public void setSupplierContactId(UUID supplierContactId) {
        if (supplierContactId != null) {
            this.supplierContact = new SupplierContactDTO();
            this.supplierContact.setId(supplierContactId);
        }
    }

    public void setSalesRepId(UUID salesRepId) {
        if (salesRepId != null) {
            this.salesRep = new UserDTO();
            this.salesRep.setId(salesRepId);
        }
    }

    public void setShipToCityId(UUID shipToCityId) {
        if (shipToCityId != null) {
            this.shipToCity = new CityDTO();
            this.shipToCity.setId(shipToCityId);
        }
    }

    public void setOpenById(UUID openById) {
        if (openById != null) {
            this.openBy = new UserDTO();
            this.openBy.setId(openById);
        }
    }
}
