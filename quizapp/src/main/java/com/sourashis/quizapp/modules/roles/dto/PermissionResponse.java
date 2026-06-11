package com.sourashis.quizapp.modules.roles.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {
    private Integer id;
    private String name;
    private String description;
}
