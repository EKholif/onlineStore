package com.onlineStoreCom.entity.section;


import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "sections_products")
public class ProductSection extends IdBasedEntity {

	@Column(name = "product_order")
	private int productOrder;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public int getProductOrder() {
		return productOrder;
	}

	public void setProductOrder(int productOrder) {
		this.productOrder = productOrder;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}


}
