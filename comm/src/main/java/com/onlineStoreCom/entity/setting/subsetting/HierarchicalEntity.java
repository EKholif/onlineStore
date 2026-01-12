package com.onlineStoreCom.entity.setting.subsetting;

import com.onlineStoreCom.entity.BaseIdEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@MappedSuperclass
public abstract class HierarchicalEntity<T extends BaseIdEntity> extends BaseIdEntity {

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIDs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", columnDefinition = "INT")
    private T parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("name asc")
    private Set<T> children = new HashSet<>();

    @Transient
    private boolean hasChildren;

    public String getAllParentIDs() {
        return allParentIDs;
    }

    public void setAllParentIDs(String allParentIDs) {
        this.allParentIDs = allParentIDs;
    }

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public Set<T> getChildren() {
        return children;
    }

    public void setChildren(Set<T> children) {
        this.children = children;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
