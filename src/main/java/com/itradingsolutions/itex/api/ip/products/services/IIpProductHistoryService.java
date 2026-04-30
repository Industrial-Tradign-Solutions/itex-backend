package com.itradingsolutions.itex.api.ip.products.services;

import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductHistoryDTO;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductHistoryActions;

import java.util.List;
import java.util.UUID;

public interface IIpProductHistoryService {
    void addHistory(IpProductHistoryActions action, IpProductDTO oldDto, IpProductDTO newDto);
    List<IpProductHistoryDTO> getHistoryById(UUID id);
}
