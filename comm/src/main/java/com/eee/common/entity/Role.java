package com.eee.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name="role")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role {

         @Id
         @GeneratedValue(strategy = GenerationType.IDENTITY)
         @Column (name= "id")
         private Integer id;


         @Column(name= "name",length =40,nullable = false,unique = false )
         private String name;

         @Column(name= "descrption",length =40,nullable = false)
         private String descrption;



    public Role() {

    }

//    @Lob
//    @Basic(fetch=LAZY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
    public String toString() {
        return "\n Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", descrption='" + descrption + '\'' +

                '}';
    }
}
