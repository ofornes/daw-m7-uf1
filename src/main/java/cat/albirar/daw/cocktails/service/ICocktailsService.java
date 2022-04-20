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
package cat.albirar.daw.cocktails.service;

import java.util.List;
import java.util.Optional;

import cat.albirar.daw.cocktails.models.CocktailDetailBean;

/**
 * Remote cocktail service.
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 1.0.0
 */
public interface ICocktailsService {
	/**
	 * Cerca tots els cocktails.
	 * @return La llista de cocktails
	 */
	public List<CocktailDetailBean> findAll();
	/**
	 * Cerca el cocktail per id.
	 * @param id L'id del cocktail
	 * @return El cocktail, si es troba o {@link Optional#empty()} si no existeix pas.
	 */
	public Optional<CocktailDetailBean> findById(String id);
}
