package com.onlineStoreCom.entity.setting.state;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;

public class StateDTO  extends IdBasedEntity {



	private String name;
	public StateDTO() {
		
	}
	
	public StateDTO(Integer id, String name) {
		this.id = id;
		this.name = name;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
