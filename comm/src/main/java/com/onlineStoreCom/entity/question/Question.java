package com.onlineStoreCom.entity.question;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import com.onlineStoreCom.entity.users.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "questions")
public class Question extends IdBasedEntity {

    @Column(name = "question_content", nullable = false, length = 500)
    private String questionContent;

    @Column(name = "answer", length = 1000)
    private String answer;

    @Column(name = "votes")
    private int votes;

    @Column(name = "approved")
    private boolean approved;

    @Column(name = "ask_time")
    private Date askTime;

    @Column(name = "answer_time")
    private Date answerTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asker_id")
    private Customer asker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerer_id")
    private User answerer;

    @Transient
    private boolean upvotedByCurrentCustomer;

    @Transient
    private boolean downvotedByCurrentCustomer;

    public Question() {
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Date getAskTime() {
        return askTime;
    }

    public void setAskTime(Date askTime) {
        this.askTime = askTime;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getAsker() {
        return asker;
    }

    public void setAsker(Customer asker) {
        this.asker = asker;
    }

    public User getAnswerer() {
        return answerer;
    }

    public void setAnswerer(User answerer) {
        this.answerer = answerer;
    }

    public boolean isAnswered() {
        return this.answer != null && !this.answer.isEmpty();
    }

    public boolean isUpvotedByCurrentCustomer() {
        return upvotedByCurrentCustomer;
    }

    public void setUpvotedByCurrentCustomer(boolean upvotedByCurrentCustomer) {
        this.upvotedByCurrentCustomer = upvotedByCurrentCustomer;
    }

    public boolean isDownvotedByCurrentCustomer() {
        return downvotedByCurrentCustomer;
    }

    public void setDownvotedByCurrentCustomer(boolean downvotedByCurrentCustomer) {
        this.downvotedByCurrentCustomer = downvotedByCurrentCustomer;
    }
}
