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
package cat.albirar.daw.cocktails.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password encoder per a autenticació en la web de cocktails.
 * Afegeix automàticament el sufix esperat.
 * @author Octavi Forn&eacute;s <mailto:ofornes@albirar.cat[]>
 * @since 1.0.0
 */
public class CocktailsPasswordEncoder implements PasswordEncoder {
	private static final String SUFIX = "SuperSecret.";
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encode(CharSequence rawPassword) {
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance("SHA-512");
			String pwd = rawPassword.toString().concat(SUFIX);
			byte [] messageDigest = md.digest(pwd.getBytes());
			return new String(Hex.encode(messageDigest));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No hi ha implementació del hash 512!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encode(rawPassword).equalsIgnoreCase(encodedPassword);
	}

}
