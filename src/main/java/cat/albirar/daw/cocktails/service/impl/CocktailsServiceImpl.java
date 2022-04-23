/*
 * This file is part of "cocktails".
 * 
 * "cocktails" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "cocktails" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with calendar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2022 Octavi Fornés
 */
package cat.albirar.daw.cocktails.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import cat.albirar.daw.cocktails.models.CocktailDetailBean;
import cat.albirar.daw.cocktails.service.ICocktailsService;
import cat.albirar.daw.cocktails.service.db.ConstantsCocktailsDb;

/**
 * Implementació del servei de coctails.
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 0.0.1
 * @since 0.1.4
 */
@Service
public class CocktailsServiceImpl implements ICocktailsService {
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private RowMapper<CocktailDetailBean> mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CocktailDetailBean> findAll() {
		return namedParameterJdbcTemplate.query(ConstantsCocktailsDb.SQL_FIND_ALL, mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<CocktailDetailBean> findById(String id) {
		List<CocktailDetailBean> l;
		
		l = namedParameterJdbcTemplate.query(ConstantsCocktailsDb.SQL_FIND_BY_ID
				, new MapSqlParameterSource(ConstantsCocktailsDb.ID, Integer.valueOf(id))
				, mapper);
		if(!l.isEmpty()) {
			return Optional.of(l.get(0));
		}
		return Optional.empty();
	}
}
