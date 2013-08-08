/*
   Copyright 2012 Marc Lijour
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
package tops;

import com.google.gson.Gson;

public class TOPSMember {
	private String firstname, lastname, jobtitle, branch, ministry, city, phone, email,	heardfrom, creatdate,
					chapter, leftdate, leftwhy, newsflash, topspot;
	
	public TOPSMember(String firstname, String lastname, String jobtitle, String branch, String ministry, 
			String city, String phone, String email, String heardfrom, String creatdate, String chapter,
			String leftdate, String leftwhy, String newsflash, String topspot) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.jobtitle = jobtitle;
		this.branch = branch;
		this.ministry = ministry;
		this.city = city;
		this.phone = phone;
		this.email = email;
		this.heardfrom = heardfrom;
		this.creatdate = creatdate;
		this.chapter = chapter;			
		this.leftdate = leftdate;
		this.leftwhy = leftwhy;
		this.newsflash = newsflash;
		this.topspot = topspot;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getMinistry() {
		return ministry;
	}

	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHeardfrom() {
		return heardfrom;
	}

	public void setHeardfrom(String heardfrom) {
		this.heardfrom = heardfrom;
	}

	public String getCreatdate() {
		return creatdate;
	}

	public void setCreatdate(String creatdate) {
		this.creatdate = creatdate;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	
	public String getLeftdate() {
		return leftdate;
	}

	public void setLeftdate(String leftdate) {
		this.leftdate = leftdate;
	}

	public String getLeftwhy() {
		return leftwhy;
	}

	public void setLeftwhy(String leftwhy) {
		this.leftwhy = leftwhy;
	}

	public String getNewsflash() {
		return newsflash;
	}

	public void setNewsflash(String newsflash) {
		this.newsflash = newsflash;
	}

	public String getTopspot() {
		return topspot;
	}

	public void setTopspot(String topspot) {
		this.topspot = topspot;
	}
	
	public String toString() {
    	return "TOPS member [" + firstname + ", " + lastname + ", " + jobtitle + ", " + branch + ", " + ministry
    			 + ", " + city  + ", " + phone + ", " + email + ", " + heardfrom + ", " + creatdate + ", " + chapter
    			 + ", left on: " + ((leftdate==null)?"N/A":leftdate)  
    			 + ", left because: " + ((leftwhy==null)?"N/A":leftwhy) 
    			 + ", Newflash=" + newsflash + ", TOPSpot=" + topspot + "]";
    }
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
