package com.onlineStore.admin.knowledge;

public class KnowledgeVersion {
    private String version;
    private String previousVersion;
    private String updatedAt;
    private boolean backwardCompatible;

    public KnowledgeVersion() {
    }

    public KnowledgeVersion(String version, String previousVersion, String updatedAt,
                            boolean backwardCompatible) {
        this.version = version;
        this.previousVersion = previousVersion;
        this.updatedAt = updatedAt;
        this.backwardCompatible = backwardCompatible;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isBackwardCompatible() {
        return backwardCompatible;
    }

    public void setBackwardCompatible(boolean backwardCompatible) {
        this.backwardCompatible = backwardCompatible;
    }

    @Override
    public String toString() {
        return "v" + version;
    }
}
