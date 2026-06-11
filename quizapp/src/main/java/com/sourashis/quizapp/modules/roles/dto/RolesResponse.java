package com.sourashis.quizapp.modules.roles.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class RolesResponse {
    private Integer id;
    private String name;
    private String description;
    private Set<String> permissions;
}
