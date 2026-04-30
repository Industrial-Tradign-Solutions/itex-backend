package com.itradingsolutions.itex.api.ip.products.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReplaceIpProductRequest {

    @NotNull(message = "Product ID is Required")
    private UUID productId;

    @NotNull(message = "New Product ID is Required")
    private UUID newProductId;
}
