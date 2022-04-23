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
package cat.albirar.daw.cocktails.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cat.albirar.daw.cocktails.models.CocktailDetailBean;
import cat.albirar.daw.cocktails.service.ICocktailsService;

/**
 * Main web controller.
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 0.0.1
 */
@Controller
public class MainController {
	@Autowired
	private ICocktailsService cocktailService;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("/list")
	public String getList(Model model) {
		
		model.addAttribute("cocktails", cocktailService.findAll());
		return "list";
	}
	@GetMapping("/show/{id}")
	public String getShow(@PathVariable(name = "id", required = true) String id, Model model) {
		Optional<CocktailDetailBean> c;

		c = cocktailService.findById(id);
		if(c.isPresent()) {
			model.addAttribute("cocktail", c.get());
		}
		model.addAttribute("found", c.isPresent());
		return "show";
	}
}
