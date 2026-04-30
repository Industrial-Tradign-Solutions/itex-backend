package com.itradingsolutions.itex.api.partners.clients.schedulers;

import com.itradingsolutions.itex.api.common.email.model.enums.MailTemplates;
import com.itradingsolutions.itex.api.common.email.service.IMailService;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ClientSchedule {

    private final IClientService clientService;
    private final IMailService mailService;

    @Scheduled(cron = "30 50 23 * * *")
    private void cronUnlockClients() {
        var listClients = clientService.listAllOpenClients(null);
        listClients.forEach(client -> clientService.unlockClient(client.getId()));
    }

    @Scheduled(cron = "0 0 5 * * 3")
    private void sendNotificationsProspects() {
        var listClients = clientService.listAllByStatus(ClientStatus.PROSPECT);
        var mails = getListMails(listClients);
        var infoToMails = getMapClients(mails);
        sendNotifications(infoToMails, "Clients Notification Prospects", "The following clients are in prospect status: ");
    }

    @Scheduled(cron = "0 5 5 * * 3")
    private void sendNotificationClientNotAssignedToAccountRep() {
        var listClients = clientService.listAllByStatus(ClientStatus.ACTIVE);
        List<String> clients = new ArrayList<>(listClients.size());
        for (var client : listClients) {
            boolean addClient = true;
            for (var info: client.getInfoByDepartment()) {
                if (info.getAccountRep() != null) {
                    addClient = false;
                    break;
                }
            }
            if (addClient) {
                clients.add(client.getName());
            }
        }
        if (!clients.isEmpty())
            sendMail(clients, "Notification of clients not assigned to a account rep", "The following clients do not have an assigned account rep:", "sara@itradingsolutions.com", "Sara Garcia");
    }

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

    private List<Map<String, String>> getListMails(List<ClientDTO> listClients) {
        List<Map<String, String>> mails = new ArrayList<>();

        listClients.forEach(client -> {
            var listDepInfo = client.getInfoByDepartment().stream().filter(info -> info.getAccountRep() != null).toList();

            if (listDepInfo.isEmpty()) {
                Map<String, String> item = new HashMap<>();
                item.put(CLIENT_NAME, client.getName());

                if (client.getUpdatedBy() != null) {
                    item.put(USER_MAIL_NAME, client.getUpdatedBy().getEmail());
                    item.put(USER_FULL_NAME, client.getUpdatedBy().getFullName());
                } else {
                    item.put(USER_MAIL_NAME, client.getCreatedBy().getEmail());
                    item.put(USER_FULL_NAME, client.getCreatedBy().getFullName());
                }

                mails.add(item);
            } else {
                listDepInfo.forEach(depInfo -> {
                    Map<String, String> item = new HashMap<>();
                    item.put(CLIENT_NAME, client.getName());
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

    private void sendMail(List<String> clientNames, String subject, String message, String userMail, String userFullName) {
        var clientsTemplate = "";
        for (String clientName : clientNames) {
            clientsTemplate = clientsTemplate.concat("<li><p style=\"line-height: 140%; text-align: left;\"> ").concat(clientName).concat("</p></li>");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("listClients", clientsTemplate);
        data.put("message", message);
        data.put("name", userFullName);

        mailService.sendTemplate(userMail, subject, data, false, MailTemplates.CLIENT_NOTIFICATION);
    }
}
