package com.itradingsolutions.itex.api.admin.user.models.responses;

import java.util.List;

public record ListsUsersResponse(
        List<BasicUserResponse> enableUsers,
        List<BasicUserResponse> disableUsers
) {
}
