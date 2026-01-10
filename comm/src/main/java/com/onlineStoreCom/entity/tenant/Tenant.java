package com.onlineStoreCom.entity.tenant;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "tenants")
public class Tenant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 45)
    private String name;

    @Column(nullable = false, unique = true, length = 45)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public Tenant() {
    }

    public Tenant(String name, String code, TenantStatus status, Date createdAt) {
        this.name = name;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Tenant parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private java.util.Set<Tenant> children = new java.util.HashSet<>();

    public Tenant getParent() {
        return parent;
    }

    public void setParent(Tenant parent) {
        this.parent = parent;
    }

    public java.util.Set<Tenant> getChildren() {
        return children;
    }

    public void setChildren(java.util.Set<Tenant> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
