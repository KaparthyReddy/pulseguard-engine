package com.pulseguard.datastructures;

import java.util.*;

/**
 * Undirected graph modeling known drug-drug interactions.
 * Nodes are drug identifiers; edges carry a severity label.
 */
public class DrugInteractionGraph {

    public enum InteractionSeverity { MINOR, MODERATE, MAJOR, CONTRAINDICATED }

    public static class Edge {
        public final String otherDrug;
        public final InteractionSeverity severity;
        public final String note;

        Edge(String otherDrug, InteractionSeverity severity, String note) {
            this.otherDrug = otherDrug;
            this.severity = severity;
            this.note = note;
        }
    }

    private final Map<String, List<Edge>> adjacency = new HashMap<>();

    public void addDrug(String drugId) {
        adjacency.putIfAbsent(drugId, new ArrayList<>());
    }

    public void addInteraction(String drugA, String drugB, InteractionSeverity severity, String note) {
        addDrug(drugA);
        addDrug(drugB);
        adjacency.get(drugA).add(new Edge(drugB, severity, note));
        adjacency.get(drugB).add(new Edge(drugA, severity, note));
    }

    public List<Edge> getDirectInteractions(String drugId) {
        return adjacency.getOrDefault(drugId, Collections.emptyList());
    }

    public Optional<Edge> getInteraction(String drugA, String drugB) {
        return getDirectInteractions(drugA).stream()
                .filter(e -> e.otherDrug.equals(drugB))
                .findFirst();
    }

    public boolean hasDirectInteraction(String drugA, String drugB) {
        return getInteraction(drugA, drugB).isPresent();
    }

    /**
     * BFS shortest-path between two drugs through the interaction graph —
     * useful for flagging indirect risk via a shared pathway.
     */
    public List<String> findShortestInteractionPath(String start, String target) {
        if (!adjacency.containsKey(start) || !adjacency.containsKey(target)) {
            return Collections.emptyList();
        }
        Map<String, String> cameFrom = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(target)) {
                return reconstructPath(cameFrom, start, target);
            }
            for (Edge edge : adjacency.get(current)) {
                if (!visited.contains(edge.otherDrug)) {
                    visited.add(edge.otherDrug);
                    cameFrom.put(edge.otherDrug, current);
                    queue.add(edge.otherDrug);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<String> reconstructPath(Map<String, String> cameFrom, String start, String target) {
        LinkedList<String> path = new LinkedList<>();
        String node = target;
        while (!node.equals(start)) {
            path.addFirst(node);
            node = cameFrom.get(node);
        }
        path.addFirst(start);
        return path;
    }

    public int drugCount() { return adjacency.size(); }
}
