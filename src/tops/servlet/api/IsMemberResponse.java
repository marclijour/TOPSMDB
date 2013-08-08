/*
   Copyright 2013 Marc Lijour
    This file is part of TOPSMDB.

    TOPSMDB is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
  
    TOPSMDB is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package tops.servlet.api;

import com.google.gson.Gson;

public class IsMemberResponse {
	private Boolean	found = null;

	public IsMemberResponse(Boolean found) {
		this.found = found;
	}
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
