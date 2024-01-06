/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Team;
import com.barraiser.onboarding.interview.jobrole.dal.TeamDAO;
import com.barraiser.onboarding.interview.jobrole.dal.TeamRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class TeamMapper {

	private TeamRepository teamRepository;

	public Team toTeam(final TeamDAO teamDAO) {
		return Team.builder()
				.id(teamDAO.getId())
				.atsId(teamDAO.getAtsId())
				.creationSource(teamDAO.getCreationSource())
				.description(teamDAO.getDescription())
				.name(teamDAO.getName())
				.sourceMeta(teamDAO.getCreationSourceMeta())
				.partnerId(teamDAO.getPartnerId())
				.build();
	}

	public List<Team> toTeams(final List<String> teamIds) {
		if (teamIds != null && !teamIds.isEmpty()) {
			return this.teamRepository.findAllByIdIn(teamIds)
					.stream().map(this::toTeam)
					.collect(Collectors.toList());
		}

		return new ArrayList<>();
	}
}
