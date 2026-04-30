package com.itradingsolutions.itex.api.admin.user.schedulers;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class UserSchedule {
    private final IUserService userService;

    @Scheduled(cron = "10 50 23 * * *")
    private void cronResetPassword() {
        Iterable<UserDTO> usersActive = userService.listAllActive();
        ZonedDateTime dateNow = ZonedDateTime.now(ZoneId.of("America/New_York"));
        for (UserDTO user: usersActive)
            if (Boolean.TRUE.equals(user.getPassChanged()) && (user.getPassChangedAt() != null && user.getPassChangedAt().isBefore(dateNow.minusMonths(3))))
                userService.resetPasswordSchedule(user.getId());
    }

    @Scheduled(cron = "0 45 23 * * *")
    private void cronCloseSession() {
        userService.closeAllSessions(10);
    }

}
