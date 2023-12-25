package com.onlineStoreCom.entity.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name="role")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role {

         @Id
         @GeneratedValue(strategy = GenerationType.IDENTITY)
         @Column (name= "id")
         private Long id;

         @Column(name= "name",length =40,nullable = false,unique = false )
         private String name;
         @Column(name= "descrption",length =150,nullable = false)
         private String descrption;

    public Role() {

    }

    @Lob
    @Basic(fetch=LAZY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption;
    }

    public Role(String name, String descrption) {
        this.name = name;
        this.descrption = descrption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(descrption, role.descrption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, descrption);
    }

    @Override
    public String toString() {
        return this.name;

    }
}
