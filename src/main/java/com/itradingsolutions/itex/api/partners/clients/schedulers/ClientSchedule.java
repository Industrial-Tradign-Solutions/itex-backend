package com.itradingsolutions.itex.api.partners.clients.schedulers;

import com.itradingsolutions.itex.api.common.email.model.enums.MailTemplates;
import com.itradingsolutions.itex.api.common.email.service.IMailService;
import com.itradingsolutions.itex.api.masters.department.models.enums.Departments;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientMissingInfo;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
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
    //@Scheduled(cron = "0 0 5 * * 3")
    @Scheduled(cron = "0 42 19 * * *")
    private void sendActiveClientNotification() {
        var listClients = clientService.listAllByStatus(ClientStatus.ACTIVE);
        //sendNotificationClientNotAssignedToAccountRepByDep(listClients, Departments.IP);
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
                if (info.getListContacts() == null) {
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
    /*
    @Scheduled(cron = "0 10 5 * * 3")
    private void sendNotificationsInfo() {
        var listClients = clientService.listAllWhitMissingInfo();
        var mails = getListMails(listClients);
        var infoToMails = getMapClients(mails);
        sendNotifications(infoToMails, "Notification of Clients with missing information", "The following clients do not have complete information: ");
    }

    private static final String CLIENT_NAME = "client";
    private static final String USER_MAIL_NAME = "userMail";
    private static final String USER_FULL_NAME = "userFullName";

    private List<ClientMissingInfoEmail> getListMailsV2(List<ClientMissingInfo> listClients) {
        List<ClientMissingInfoEmail> mails = new ArrayList<>();
        listClients.forEach(clientMissingInfo -> {
            var listDepInfo = clientMissingInfo.client().getInfoByDepartment().stream().filter(info -> info.getAccountRep() != null).toList();
            if (listDepInfo.isEmpty()) {
                mails.add(new ClientMissingInfoEmail(
                    clientMissingInfo.client().getName(),
                    clientMissingInfo.client().getCode(),
                    clientMissingInfo.client().getUpdatedBy() != null ? clientMissingInfo.client().getUpdatedBy().getFullName(): clientMissingInfo.client().getCreatedBy().getFullName(),
                    clientMissingInfo.client().getUpdatedBy() != null ? clientMissingInfo.client().getUpdatedBy().getEmail(): clientMissingInfo.client().getCreatedBy().getEmail(),
                    clientMissingInfo.errors()
                ));
            } else {
                listDepInfo.forEach(depInfo -> mails.add(new ClientMissingInfoEmail(
                        clientMissingInfo.client().getName(),
                        clientMissingInfo.client().getCode(),
                        depInfo.getAccountRep().getFullName(),
                        depInfo.getAccountRep().getEmail(),
                        clientMissingInfo.errors()
                )));
            }
        });
        return mails;
    }

    private List<Map<String, String>> getListMails(List<ClientMissingInfo> listClients) {
        List<Map<String, String>> mails = new ArrayList<>();

        listClients.forEach(clientMissingInfo -> {
            var listDepInfo = clientMissingInfo.client().getInfoByDepartment().stream().filter(info -> info.getAccountRep() != null).toList();

            if (listDepInfo.isEmpty()) {
                Map<String, String> item = new HashMap<>();
                item.put(CLIENT_NAME, clientMissingInfo.client().getCode() + "-" + clientMissingInfo.client().getName());

                if (clientMissingInfo.client().getUpdatedBy() != null) {
                    item.put(USER_MAIL_NAME, clientMissingInfo.client().getUpdatedBy().getEmail());
                    item.put(USER_FULL_NAME, clientMissingInfo.client().getUpdatedBy().getFullName());
                } else {
                    item.put(USER_MAIL_NAME, clientMissingInfo.client().getCreatedBy().getEmail());
                    item.put(USER_FULL_NAME, clientMissingInfo.client().getCreatedBy().getFullName());
                }

                mails.add(item);
            } else {
                listDepInfo.forEach(depInfo -> {
                    Map<String, String> item = new HashMap<>();
                    item.put(CLIENT_NAME, clientMissingInfo.client().getCode() + "-" + clientMissingInfo.client().getName());
                    item.put(USER_MAIL_NAME, depInfo.getAccountRep().getEmail());
                    item.put(USER_FULL_NAME, depInfo.getAccountRep().getFullName());
                    mails.add(item);
                });
            }
        });
        return mails;
    }

    private Map<String, Map<String, Object>> getMapClients(List<Map<String, String>> mails) {
        return mails.stream()
                .filter(map -> map.get(USER_MAIL_NAME) != null)
                .collect(Collectors.groupingBy(
                        map -> map.get(USER_MAIL_NAME),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Map<String, Object> newMap = new HashMap<>();
                                    newMap.put(CLIENT_NAME, list.stream()
                                            .map(map -> map.get(CLIENT_NAME))
                                            .toList());
                                    newMap.put(USER_FULL_NAME, list.stream()
                                            .map(map -> map.get(USER_FULL_NAME))
                                            .findFirst()
                                            .orElse(""));
                                    return newMap;
                                }
                        )

                ));
    }

    private void sendNotifications(Map<String, Map<String, Object>> infoToMails, String subject, String message) {
        infoToMails.forEach((userMail, dataList) ->
            sendMail((List<String>) dataList.get(CLIENT_NAME), subject, message, userMail, (String) dataList.get(USER_FULL_NAME))
        );
    }

*/

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
                    String errorColor = error.contains("PHONE") ? "#e65100" : "#d32f2f";
                    clientsTemplate = clientsTemplate
                            .concat("<li><p style=\"line-height: 140%; text-align: left; font-size: 13px; color: ")
                            .concat(errorColor)
                            .concat("; margin-bottom: 4px;\">")
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
