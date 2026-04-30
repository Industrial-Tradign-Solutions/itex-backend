package com.itradingsolutions.itex.api.masters.location.models.responses;

import com.itradingsolutions.itex.api.common.models.responses.ListsResponses;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ListsCitiesResponse extends ListsResponses<BasicCityResponse> {
}
