/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.dal.RoleDAO;
import com.barraiser.authorization.repository.RoleRepository;
import com.barraiser.commons.dto.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class RoleService {
	private RoleRepository roleRepository;

	public List<Role> getAllRoles() {
		return this.roleRepository.findAll().stream()
				.map(r -> this.toRole(r))
				.collect(Collectors.toList());
	}

	public List<Role> getPartnerRoles(final String partnerId) {
		List<RoleDAO> partnerRoles = this.roleRepository.findByPartnerId(partnerId);

		if (partnerRoles.isEmpty()) {
			partnerRoles = this.roleRepository.findByPartnerId(null);
		}

		return this.getRolesList(partnerRoles);
	}

	public Role getRoleDetails(final String roleId) {
		Optional<RoleDAO> roleDAO = this.roleRepository.findById(roleId);
		return roleDAO.map(this::toRole).orElse(null);
	}

	private List<Role> getRolesList(final List<RoleDAO> roleDAOS) {
		List<Role> roles = new ArrayList<>();
		for (RoleDAO roleDAO : roleDAOS) {
			roles.add(this.toRole(roleDAO));
		}
		return roles;
	}

	public Role toRole(final RoleDAO roleDAO) {
		return Role.builder()
				.partnerId(roleDAO.getPartnerId())
				.displayName(roleDAO.getDisplayName())
				.name(roleDAO.getName())
				.type(roleDAO.getType().getValue())
				.build();
	}
}
