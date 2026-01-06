package com.onlineStoreCom.entity.setting;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "settings", uniqueConstraints = @UniqueConstraint(columnNames = {"`key`", "tenant_id"}))
@org.hibernate.annotations.Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Setting extends IdBasedEntity {
	@Column(name = "`key`", nullable = false, length = 128)
	private String key;

    @Column(nullable = false, length = 2024)
	private String value;

    @Enumerated(EnumType.STRING)
	@Column(length = 45, nullable = false)
	private SettingCategory category;

	public Setting() {

    }

    public Setting(String key) {
		this.key = key;
	}

    public Setting(String key, String value, SettingCategory category) {
		this.key = key;
		this.value = value;
		this.category = category;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SettingCategory getCategory() {
		return category;
	}

	public void setCategory(SettingCategory category) {
		this.category = category;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Setting other = (Setting) obj;
		if (key == null) {
            return other.key == null;
        } else
            return key.equals(other.key);
    }

	@Override
	public String toString() {
		return "Setting [key=" + key + ", value=" + value + "]";
	}

}
