package com.onlineStoreCom.entity.section;

import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "sections_categories")
public class CategorySection extends IdBasedEntity {

	@Column(name = "category_order")
	private int categoryOrder;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	public int getCategoryOrder() {
		return categoryOrder;
	}

	public void setCategoryOrder(int categoryOrder) {
		this.categoryOrder = categoryOrder;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}
