package com.itradingsolutions.itex.api.partners.clients.models.responses;



import java.util.List;
import java.util.UUID;

public record ClientContactResponse(
        UUID id,
        String name,
        String title,
        String email,
        boolean validMain,
        boolean active,
        List<ClientContactPhoneResponse>listPhones,
        String mainPhone
) {
}
