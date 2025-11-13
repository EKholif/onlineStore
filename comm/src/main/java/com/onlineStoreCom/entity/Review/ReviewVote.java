package com.onlineStoreCom.entity.Review;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Transient;

/**
 * Represents a vote by a customer on a review.
 * <p>
 * A vote can be either an upvote (+1) or a downvote (-1).
 * This entity links the {@link Customer} with the {@link Review} they voted on.
 * </p>
 */
@Entity
@Table(name = "reviews_votes")
public class ReviewVote extends IdBasedEntity {

	private static final int VOTE_UP_POINT = 1;
	private static final int VOTE_DOWN_POINT = -1;

	/** The vote value: +1 for upvote, -1 for downvote. */
	private int votes;

	/** The customer who cast the vote. */
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	/** The review that was voted on. */
	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review;

	// ---------------- Getters & Setters ----------------

	public int getVotes() { return votes; }
	public void setVotes(int votes) { this.votes = votes; }

	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) { this.customer = customer; }

	public Review getReview() { return review; }
	public void setReview(Review review) { this.review = review; }

	// ---------------- Utility Methods ----------------

	/**
	 * Casts an upvote.
	 */
	public void voteUp() { this.votes = VOTE_UP_POINT; }

	/**
	 * Casts a downvote.
	 */
	public void voteDown() { this.votes = VOTE_DOWN_POINT; }

	@Override
	public String toString() {
		return "ReviewVote [votes=" + votes + ", customer=" + customer.getFullName()
				+ ", review=" + review.getId() + "]";
	}

	/**
	 * Checks if this vote is an upvote.
	 *
	 * @return true if upvote, false otherwise
	 */
	@Transient
	public boolean isUpvoted() { return this.votes == VOTE_UP_POINT; }

	/**
	 * Checks if this vote is a downvote.
	 *
	 * @return true if downvote, false otherwise
	 */
	@Transient
	public boolean isDownvoted() { return this.votes == VOTE_DOWN_POINT; }
}
