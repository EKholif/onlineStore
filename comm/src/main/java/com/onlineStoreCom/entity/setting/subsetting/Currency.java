package com.onlineStoreCom.entity.setting.subsetting;

import jakarta.persistence.*;

@Entity
@Table(name = "currencies")
@org.hibernate.annotations.Filter(name = "tenantFilter", condition = "(tenant_id = :tenantId or tenant_id = 0 or tenant_id is null)")
public class Currency extends IdBasedEntity {

	@Column(nullable = false, length = 64)
	private String name;

	@Column(nullable = false, length = 3)
	private String symbol;

	@Column(nullable = false, length = 4)
	private String code;

	public Currency() {

	}

	public Currency(String name, String symbol, String code) {
		super();
		this.name = name;
		this.symbol = symbol;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return name + " - " + code + " - " + symbol;
	}

}
