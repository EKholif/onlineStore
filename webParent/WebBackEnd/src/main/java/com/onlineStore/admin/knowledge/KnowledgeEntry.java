package com.onlineStore.admin.knowledge;

import java.util.List;
import java.util.Map;

public class KnowledgeEntry {
    private String id;
    private String type; // RULE, BUSINESS, UI, etc.
    private List<String> personas; // TENANT, SUPPORT, etc.
    private Map<String, Object> content;
    private KnowledgeVersion versioning;

    public KnowledgeEntry() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPersonas() {
        return personas;
    }

    public void setPersonas(List<String> personas) {
        this.personas = personas;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    public KnowledgeVersion getVersioning() {
        return versioning;
    }

    public void setVersioning(KnowledgeVersion versioning) {
        this.versioning = versioning;
    }

    @Override
    public String toString() {
        return "KnowledgeEntry{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", version=" + versioning +
                '}';
    }
}
