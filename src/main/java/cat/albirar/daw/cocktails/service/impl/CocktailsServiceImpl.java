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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.albirar.daw.cocktails.models.CocktailDetailBean;
import cat.albirar.daw.cocktails.service.ICocktailsService;

/**
 * Implementació del servei de coctails.
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 1.0.0
 */
@Service
public class CocktailsServiceImpl implements ICocktailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CocktailsServiceImpl.class);
	
	@Value("classpath:/cocktails.json")
	private Resource jsonCocktails;
	
	private List<CocktailDetailBean> cocktails = null;
	private Map<String, CocktailDetailBean> cocktailsMap = null;
	
	@PostConstruct
	private void initList() {
		ObjectMapper om;
		
		om = new ObjectMapper();
		try {
			cocktails = om.readValue(jsonCocktails.getInputStream(), new TypeReference<List<CocktailDetailBean>>(){});
			cocktailsMap = Collections.synchronizedMap(new TreeMap<>());
			for(CocktailDetailBean c : cocktails) {
				cocktailsMap.put(c.getId(), c);
			}
		} catch (IOException e) {
			LOGGER.error("No s'ha pogut carregar la llista de coctails!", e);
			throw new RuntimeException("No s'ha pogut carregar la llista de coctails!", e);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CocktailDetailBean> findAll() {
		return Collections.unmodifiableList(cocktails);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<CocktailDetailBean> findById(String id) {
		if(cocktailsMap.containsKey(id)) {
			return Optional.of(cocktailsMap.get(id));
		}
		return Optional.empty();
	}
}
