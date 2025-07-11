package com.onlineStoreCom.entity.setting.state;


import com.onlineStoreCom.entity.setting.Country.Country;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "states")
public class State extends IdBasedEntity {

    @Column(nullable = false, length = 45)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public State() {

    }

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "State [id=" + id + ", name=" + name + "]";
    }


}
