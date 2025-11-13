package com.onlineStoreCom.entity.Review;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;
import java.util.Date;

/**
 * Represents a review left by a customer for a product.
 * <p>
 * Contains headline, comment, rating, votes, review timestamp, and references
 * to the associated product and customer.
 * </p>
 */
@Entity
@Table(name = "reviews")
public class Review extends IdBasedEntity {

	/** The title or headline of the review. */
	@Column(length = 128, nullable = false)
	private String headline;

	/** The body comment of the review. */
	@Column(length = 300, nullable = false)
	private String comment;

	/** Rating given by the customer (e.g., 1-5). */
	private int rating;

	/** Total votes on this review (can be upvotes/downvotes). */
	private int votes;

	/** The time when the review was submitted. */
	@Column(nullable = false)
	private Date reviewTime;

	/** The product associated with this review. */
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	/** The customer who wrote the review. */
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	/** Indicates if the current logged-in customer upvoted this review. */
	@Transient
	private boolean upvotedByCurrentCustomer;

	/** Indicates if the current logged-in customer downvoted this review. */
	@Transient
	private boolean downvotedByCurrentCustomer;

	// ---------------- Constructors ----------------

	public Review() { }

	public Review(Integer id) { this.id = id; }

	// ---------------- Getters & Setters ----------------

	public String getHeadline() { return headline; }
	public void setHeadline(String headline) { this.headline = headline; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }

	public int getRating() { return rating; }
	public void setRating(int rating) { this.rating = rating; }

	public int getVotes() { return votes; }
	public void setVotes(int votes) { this.votes = votes; }

	public Date getReviewTime() { return reviewTime; }
	public void setReviewTime(Date reviewTime) { this.reviewTime = reviewTime; }

	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }

	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) { this.customer = customer; }

	public boolean isUpvotedByCurrentCustomer() { return upvotedByCurrentCustomer; }
	public void setUpvotedByCurrentCustomer(boolean upvotedByCurrentCustomer) { this.upvotedByCurrentCustomer = upvotedByCurrentCustomer; }

	public boolean isDownvotedByCurrentCustomer() { return downvotedByCurrentCustomer; }
	public void setDownvotedByCurrentCustomer(boolean downvotedByCurrentCustomer) { this.downvotedByCurrentCustomer = downvotedByCurrentCustomer; }

	// ---------------- Utility Methods ----------------

	@Override
	public String toString() {
		return "Review [headline=" + headline + ", rating=" + rating + ", reviewTime=" + reviewTime
				+ ", product=" + product.getShortName() + ", customer=" + customer.getFullName() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Review other = (Review) obj;
		return id != null && id.equals(other.id);
	}
}
