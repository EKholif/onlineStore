package com.onlineStoreCom.entity.customer;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.AuthenticationType;
import com.onlineStoreCom.entity.address.AbstractAddressWithCountry;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer extends AbstractAddressWithCountry {




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



    public String getRestPasswordToken() {
        return restPasswordToken;
    }

    public void setRestPasswordToken(String restPasswordToken) {
        this.restPasswordToken = restPasswordToken;
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