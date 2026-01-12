package com.onlineStoreCom.entity.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.AuthenticationType;
import com.onlineStoreCom.entity.address.AbstractAddressWithCountry;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a customer in the online store system.
 * This entity extends AbstractAddressWithCountry to include address information
 * and adds customer-specific fields like email, password, and authentication
 * details.
 * It's used for user authentication, profile management, and order processing.
 */
@Entity
@Table(name = "customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer extends AbstractAddressWithCountry {

    /**
     * The unique email address of the customer, used for login
     */
    @Column(nullable = false, unique = true, length = 45)
    private String email;

    /**
     * The hashed password for customer authentication
     */
    @Column(nullable = false, length = 64)
    private String password;

    /**
     * Verification code used for email verification process
     */
    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    /**
     * Flag indicating if the customer account is enabled
     */
    private boolean enabled;

    /**
     * The date and time when the customer account was created
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * Path to the customer's profile image
     */
    @Column(name = "image", length = 225, nullable = true)
    private String image;

    /**
     * The authentication method used by the customer
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;

    /**
     * Token used for password reset functionality
     */
    @Column(name = "rest_password_token", length = 30)
    private String restPasswordToken;

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    // Ehab newAdd
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Customer() {
    }

    /**
     * Constructor with customer ID.
     *
     * @param id The unique identifier for the customer
     */
    public Customer(Integer id) {
        this.id = id;
    }

    // Getters and Setters with Javadoc

    /**
     * Gets the customer's email address.
     *
     * @return The email address of the customer
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email address.
     *
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    // ... [Other getters and setters with similar documentation]

    /**
     * Gets the filename of the customer's profile image.
     *
     * @return The filename of the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the filename of the customer's profile image.
     *
     * @param image The filename to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getRestPasswordToken() {
        return restPasswordToken;
    }

    public void setRestPasswordToken(String restPasswordToken) {
        this.restPasswordToken = restPasswordToken;
    }

    /**
     * Gets the full name of the customer by combining first and last name.
     *
     * @return The full name of the customer
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Sets the customer's full name by parsing it into first and last names.
     *
     * @param name The full name to parse and set
     */
    @Transient
    public void setFullName(String name) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            this.setFirstName(name);
            this.setLastName("");
        } else {
            String firstName = nameArray[0];
            this.setFirstName(firstName);
            String lastName = name.replaceFirst(firstName + " ", "");
            this.setLastName(lastName);
        }
    }

    /**
     * Gets the path to the customer's profile image.
     * Handles both local and remote (URL) images.
     *
     * @return The path to the customer's image
     */
    @Transient
    public String getImagePath() {
        if (this.image != null && this.image.startsWith("http")) {
            return this.image;
        }

        if (id == null || image == null)
            return "/images/bob.png";

        // AG-ASSET-PATH-004: Standardized tenant asset path
        // Returns: /tenants/{tenantId}/assets/customers/{id}/{filename}
        return "/tenants/" + this.getTenantId() + "/assets/customers/" + this.id + "/" + this.image;
    }

    // ... (rest of methods)

    /**
     * Gets the directory path for storing the customer's images.
     *
     * @return The directory path for the customer's images
     */
    @Transient
    public String getImageDir() {
        if (id == null)
            return null;

        // AG-ASSET-PATH-004: Standardized tenant asset path
        // Returns: tenants/{tenantId}/assets/customers/{id}/
        return "tenants/" + this.getTenantId() + "/assets/customers/" + this.id + "/";
    }
}