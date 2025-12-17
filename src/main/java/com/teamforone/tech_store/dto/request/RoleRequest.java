package com.teamforone.tech_store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class RoleRequest {
    private String name;
    private String description;
    private Set<String> permission;
}
