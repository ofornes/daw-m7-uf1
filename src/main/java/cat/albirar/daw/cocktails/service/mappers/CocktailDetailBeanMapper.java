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
package cat.albirar.daw.cocktails.service.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import cat.albirar.daw.cocktails.models.CocktailDetailBean;
import cat.albirar.daw.cocktails.service.db.ConstantsCocktailsDb;

/**
 * Mapador de JDBC a {@link CocktailDetailBean}.
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 0.1.4
 */
@Component
public class CocktailDetailBeanMapper implements RowMapper<CocktailDetailBean> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CocktailDetailBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CocktailDetailBean.builder()
				.id(Integer.toString(rs.getInt(ConstantsCocktailsDb.ID)))
				.name(rs.getString(ConstantsCocktailsDb.NAME))
				.urlThumb(rs.getString(ConstantsCocktailsDb.THUMB))
				.ingredients(rs.getString(ConstantsCocktailsDb.INGREDIENTS))
				.instruccions(rs.getString(ConstantsCocktailsDb.INSTRUCTIONS))
				.build()
				;
	}

}
