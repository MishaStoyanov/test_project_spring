package net.integrio.test_project.service;

import net.integrio.test_project.dto.RolesDto;
import net.integrio.test_project.entity.Roles;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RolesConvertor {
    public List<RolesDto> fromRolesListToRoleDtoList(List<Roles> roles){
        List<RolesDto> result = new ArrayList<>();
        for (Roles role : roles){
            result.add(fromRolesToRolesDto(role));
        }
        return result;
    }

    public RolesDto fromRolesToRolesDto(Roles roles) {
        RolesDto rolesDto = new RolesDto();
        rolesDto.setId(roles.getId());
        rolesDto.setRole(roles.getRole());
        return rolesDto;
    }

}
