package com.onlineStoreCom.entity.customer;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.AuthenticationType;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phone_number", nullable = false, length = 15)
    protected String phoneNumber;
    @Column(name = "address_line_1", nullable = false, length = 64)
    protected String addressLine1;
    @Column(name = "address_line_2", length = 64)
    protected String addressLine2;
    @Column(nullable = false, length = 45)
    protected String city;
    @Column(nullable = false, length = 45)
    protected String state;
    @Column(name = "postal_code", nullable = false, length = 10)
    protected String postalCode;

    @Column(name = "first_name", length = 85, nullable = false)

    private String firstName;
    @Column(name = "last_name", length = 85, nullable = false)

    private String lastName;
    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;
    private boolean enabled;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "image", length = 225, nullable = true)
    private String image;


    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;

    @Column(name = "rest_password_token", length = 30)
    private String restPasswordToken;



    public Customer() {
    }


    public Customer(Integer id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getRestPasswordToken() {
        return restPasswordToken;
    }

    public void setRestPasswordToken(String restPasswordToken) {
        this.restPasswordToken = restPasswordToken;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdTime=" + createdTime +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +

                ", postalCode='" + postalCode + '\'' +
                '}';
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // todo: Refactor the next in spreat class / intrface
    @Transient
    public String getImagePath() {
        if (this.image != null && this.image.startsWith("http")) {
            return this.image;
        }

        String dirName = "/customers-photos/";
        if (id  == null || image == null) return "/images" + "\\" + "bob.png";
        return dirName + this.id + '\\' + this.image;
    }
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

    @Transient
    public String getImageDir() {
        String dirName = "customers-photos\\";
        if (id == null || image == null) return "\\images \\ bob.png";
        return dirName + this.id + '\\';
    }



}