package com.niraj.jsonparsing.data;

import java.util.ArrayList;

public class UserInfo {
	
	ArrayList<UserData> userDatalist = new ArrayList<UserData>();
	
	public void setData(String id, String loginName){
		userDatalist.add(new UserData(id, loginName));
	}
	
	public ArrayList<UserData> getData(){
		return userDatalist;
	}
	
	public class UserData{
		
		public String mId;
		public String mLoginName;
		
		public UserData(String id, String loginName){
			this.mId = id;
			this.mLoginName = loginName;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof UserData)) {
			    return false;
			  }
			UserData delUser = (UserData) o;
			  return mId.equals(delUser.mId);
		}
	}

}
