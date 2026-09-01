package com.itradingsolutions.itex.api.partners.clients.schedulers;

import com.itradingsolutions.itex.api.common.email.model.enums.MailTemplates;
import com.itradingsolutions.itex.api.common.email.service.IMailService;
import com.itradingsolutions.itex.api.masters.department.models.enums.Departments;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientMissingInfo;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.api.partners.common.models.dto.PartnerContactDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ClientSchedule {

    private final IClientService clientService;
    private final IMailService mailService;

    @Value("${itex.notifications.client.email}")
    private String sentEmail;

    @Value("${itex.notifications.client.name}")
    private String sentName;

    /*
    Funcion para desbloquear todos los clientes en la noche y no tener clientes bloqueados para nadie el dia siguiente
    */
    @Scheduled(cron = "30 50 23 * * *")
    private void cronUnlockClients() {
        var listClients = clientService.listAllOpenClients(null);
        listClients.forEach(client -> clientService.unlockClient(client.getId()));
    }

    /*
     * Funcion para notificar que clientes estan en prospecto
     * Solo envia si hay algun prospecto
     * */
    @Scheduled(cron = "0 0 5 * * 3")
    private void sendNotificationsProspects() {
        var listClients = clientService.listAllByStatus(ClientStatus.PROSPECT)
            .stream()
            .map(clientDTO -> new ClientMissingInfo(null, clientDTO))
            .toList();
        if (!listClients.isEmpty())
            sendMail(
                listClients,
                "Clients Notification Prospects",
                "The following clients are in prospect status: ",
                sentEmail,
                sentName
            );
    }

    /*
     * Funcion para notificar errores en los clientes
     * 1. Notificacion de AccountRep IP
     * */
    @Scheduled(cron = "0 0 5 * * 3")
    private void sendActiveClientNotification() {
        var listClients = clientService.listAllByStatus(ClientStatus.ACTIVE);
        sendNotificationClientNotAssignedToAccountRepByDep(listClients, Departments.IP);
        //sendNotificationClientNotAssignedToAccountRepByDep(listClients, Departments.ACC);
        sendNotificationClientWhitMissingInfo(listClients);
    }


    private void sendNotificationClientNotAssignedToAccountRepByDep(List<ClientDTO> clients, Departments department) {
        var filteredClients = clients.stream()
                .filter(client -> client.getInfoByDepartment() != null && client.getInfoByDepartment().stream()
                        .anyMatch(info -> info.getDepartment() != null
                                && Objects.equals(info.getDepartment().getId(), department.getDepartmentId())
                                && info.getAccountRep() == null))
                .map(clientDTO -> new ClientMissingInfo(null, clientDTO))
                .toList();
        if (!filteredClients.isEmpty())
            sendMail(
                filteredClients,
                "Notification of clients not assigned to a account rep",
                "The following customers do not have an assigned account representative for the ".concat(department.getName()).concat(" department:"),
                sentEmail,
                sentName
            );
    }

    private void sendNotificationClientWhitMissingInfo(List<ClientDTO> clients) {
        var clientsWithMissingInfo = getClientsWhitMissingInfo(clients);
        if (!clientsWithMissingInfo.isEmpty())
            sendMail(
                clientsWithMissingInfo,
        "Notification of Clients with missing information",
        "The following clients do not have complete information: ",
                sentEmail,
                sentName
            );
    }

    private List<ClientMissingInfo> getClientsWhitMissingInfo(List<ClientDTO> clients) {
        List<ClientMissingInfo> listMissingInfo = new ArrayList<>();
        for (var client : clients) {
            List<String> errors = new ArrayList<>();
            if (client.getAddress() == null || client.getAddress().isBlank())
                errors.add("Missing address");

            if (client.getCity() == null)
                errors.add("Missing city");

            int notContacts = 0;
            for (var info: client.getInfoByDepartment()) {
                boolean hasActiveContacts = info.getListContacts() != null &&
                        info.getListContacts().stream().anyMatch(PartnerContactDTO::isActive);

                if (!hasActiveContacts) {
                    UUID deptId = (info.getDepartment() != null) ? info.getDepartment().getId() : null;

                    if (Objects.equals(deptId, Departments.IP.getDepartmentId()))
                        errors.add(Departments.IP.getName() + " require contacts");

                    notContacts++;
                    continue;
                }
                String deptName = "DEPARTMENT: " + (info.getDepartment() != null ? info.getDepartment().getName() : "Unknown");
                for (var contact: info.getListContacts()) {
                    String contactName = "CONTACT: " + (contact.getName() != null ? contact.getName() : "No Name");
                    if (contact.getEmail() == null || contact.getEmail().isBlank())
                        errors.add(deptName + " | " + contactName + " | ERROR: Missing email");
                    if (contact.getListPhones().isEmpty())
                        errors.add(deptName + " | " + contactName + " | ERROR: No phone numbers associated");

                    for (var phone: contact.getListPhones()) {
                        if (phone.getCountryCode() == null || phone.getCountryCode().isBlank())
                            errors.add(deptName + " | " + contactName + " | PHONE: " + phone.getFullPhone() + " | ERROR: Missing country code");

                        if (phone.getPhoneNumber() == null || phone.getPhoneNumber().isBlank()) {
                            errors.add(deptName + " | " + contactName + " | PHONE: " + phone.getFullPhone() + " | ERROR: Missing phone number");
                        }
                    }
                }
            }
            if (notContacts == client.getInfoByDepartment().size())
                errors.add("Client has no contacts");

            if (!errors.isEmpty())
                listMissingInfo.add(new ClientMissingInfo(errors, client));
        }
        return listMissingInfo;
    }

    private void sendMail(List<ClientMissingInfo> clientMissingInfos, String subject, String message, String userMail, String userFullName) {
        var clientsTemplate = getClientsTemplate(clientMissingInfos);
        Map<String, Object> data = new HashMap<>();
        data.put("listClients", clientsTemplate);
        data.put("message", message);
        data.put("name", userFullName);
        log.info("Sent mail to {} whit subject {}", userMail, subject);
        mailService.sendTemplate(userMail, subject, data, false, MailTemplates.CLIENT_NOTIFICATION);
    }

    @NonNull
    private static String getClientsTemplate(List<ClientMissingInfo> clientMissingInfos) {
        var clientsTemplate = "";
        for (ClientMissingInfo client : clientMissingInfos) {
            if (client.errors()== null || client.errors().isEmpty()) {
                clientsTemplate = clientsTemplate
                        .concat("<li style=\"margin-bottom: 12px;\"><p style=\"line-height: 140%; text-align: left; font-size: 15px; color: #1a1a1a; font-weight: bold;\">")
                        .concat(client.client().getCode())
                        .concat(" - ")
                        .concat(client.client().getName())
                        .concat("</p></li>");
            } else {
                clientsTemplate = clientsTemplate
                        .concat("<li style=\"margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #e0e0e0;\"><p style=\"line-height: 140%; text-align: left; font-size: 15px; color: #1a1a1a; font-weight: bold;\">")
                        .concat(client.client().getCode())
                        .concat(" - ")
                        .concat(client.client().getName())
                        .concat("</p>")
                        .concat("<ul style=\"margin-top: 6px; padding-left: 20px;\">");
                for(var error: client.errors()) {
                    boolean isRequired = error.contains("require contacts");
                    String errorColor = isRequired ? "#b71c1c" : error.contains("PHONE") ? "#e65100" : "#d32f2f";
                    String bgColor = isRequired ? "#ffebee" : "transparent";
                    String fontWeight = isRequired ? "bold" : "normal";
                    String padding = isRequired ? "6px 10px" : "0";
                    String borderRadius = isRequired ? "4px" : "0";
                    clientsTemplate = clientsTemplate
                            .concat("<li><p style=\"line-height: 140%; text-align: left; font-size: 13px; color: ")
                            .concat(errorColor)
                            .concat("; margin-bottom: 4px; font-weight: ")
                            .concat(fontWeight)
                            .concat("; background-color: ")
                            .concat(bgColor)
                            .concat("; padding: ")
                            .concat(padding)
                            .concat("; border-radius: ")
                            .concat(borderRadius)
                            .concat(";\">")
                            .concat(error)
                            .concat("</p></li>");
                }
                clientsTemplate = clientsTemplate
                        .concat("</ul></li>");
            }
        }
        return clientsTemplate;
    }
}
