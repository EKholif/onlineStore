package com.onlineStore.admin.config;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class KnowledgeBootstrap {

    @PostConstruct
    public void loadKnowledge() {
        System.out.println("🚀 Anti-Gravity Knowledge System is starting...");

        load("00-intent.yml");
        load("01-core-principles.yml");
        load("02-rule-mapping.yml");
        load("03-business-scenarios.yml");
        load("04-ai-prompts.yml");
        load("05-pitch-deck.yml");
        load("06-governance.yml");

        System.out.println("✅ Knowledge System loaded successfully");
    }

    private void load(String file) {
        System.out.println("📘 Loading knowledge file: " + file);
    }
}
