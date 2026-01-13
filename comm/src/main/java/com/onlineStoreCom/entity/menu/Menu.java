package com.onlineStoreCom.entity.menu;

import com.onlineStoreCom.entity.articals.Article;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "menus", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"title", "tenant_id"}),
		@UniqueConstraint(columnNames = {"alias", "tenant_id"})
})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Menu extends IdBasedEntity {

	@Enumerated(EnumType.ORDINAL)
	private MenuType type;

	@Column(nullable = false, length = 128)
	private String title;

	@Column(nullable = false, length = 256)
	private String alias;

	private int position;

	private boolean enabled;

	@ManyToOne
	@JoinColumn(name = "article_id")
	private Article article;

	public MenuType getType() {
		return type;
	}

	public void setType(MenuType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	@Override
	public String toString() {
		return "Menu [id=" + id + ", type=" + type + ", title=" + title + ", position=" + position + "]";
	}
}
