package com.itradingsolutions.itex.api.ip.po.controller;

import com.itradingsolutions.itex.api.common.controller.CommonController;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ip/po")
@Validated
@AllArgsConstructor
public class PurchaseOrderController extends CommonController {
}
