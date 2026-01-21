package com.onlineStore.admin.knowledge;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KnowledgeRegistry {

    private final Map<String, KnowledgeEntry> registry = new ConcurrentHashMap<>();

    public void register(KnowledgeEntry entry) {
        if (entry == null || entry.getId() == null) {
            throw new IllegalArgumentException("Cannot register null entry or entry without ID");
        }
        registry.put(entry.getId(), entry);
    }

    public KnowledgeEntry get(String id) {
        return registry.get(id);
    }

    public Collection<KnowledgeEntry> getAll() {
        return registry.values();
    }

    public Collection<KnowledgeEntry> getByType(String type) {
        return registry.values().stream()
                .filter(e -> type.equalsIgnoreCase(e.getType()))
                .collect(Collectors.toList());
    }

    public int getCount() {
        return registry.size();
    }

    public void clear() {
        registry.clear();
    }
}
