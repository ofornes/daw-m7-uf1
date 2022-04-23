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
package cat.albirar.daw.cocktails.service.db;

/**
 * Constants de la base de dades de cocktails.
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 0.1.4
 */
public final class ConstantsCocktailsDb {
	public static final String ID = "ID_DRINK";
	public static final String NAME = "NAME";
	public static final String THUMB = "THUMB";
	public static final String INGREDIENTS = "INGREDIENTS";
	public static final String INSTRUCTIONS = "INSTRUCTIONS";
	public static final String TAULA = "COCKTAILS";
	public static final String SQL_FIND_ALL = "SELECT * FROM " + TAULA;
	public static final String SQL_FIND_BY_ID = "SELECT * FROM " + TAULA + " WHERE " + ID + "=:" + ID;

	private ConstantsCocktailsDb() {
		// Per a evitar instàncies
	}
}
