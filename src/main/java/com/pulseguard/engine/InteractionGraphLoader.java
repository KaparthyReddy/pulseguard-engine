package com.pulseguard.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseguard.datastructures.DrugInteractionGraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads seed drug-interaction data from a JSON resource into a
 * DrugInteractionGraph, so interaction data is config-driven rather
 * than hardcoded.
 */
public class InteractionGraphLoader {

    public static class InteractionRecord {
        public String drugA;
        public String drugB;
        public String severity;
        public String note;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public DrugInteractionGraph loadFromResource(String resourcePath) {
        DrugInteractionGraph graph = new DrugInteractionGraph();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            List<InteractionRecord> records = mapper.readValue(in, mapper.getTypeFactory()
                    .constructCollectionType(List.class, InteractionRecord.class));
            for (InteractionRecord record : records) {
                graph.addInteraction(record.drugA, record.drugB,
                        DrugInteractionGraph.InteractionSeverity.valueOf(record.severity), record.note);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load drug interaction seed data", e);
        }
        return graph;
    }
}
