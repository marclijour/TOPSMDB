package tops.servlet.api;

import com.google.gson.Gson;

import tops.TOPSMember;

public class MemberDataResponse {
	private Boolean status;
	private TOPSMember member;
	
	public MemberDataResponse(Boolean status, TOPSMember member) {
		this.status = status;
		this.member = member;
	}
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
