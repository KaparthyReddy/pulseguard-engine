package com.pulseguard.datastructures;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DrugInteractionGraphTest {

    @Test
    void addsSymmetricInteraction() {
        DrugInteractionGraph graph = new DrugInteractionGraph();
        graph.addInteraction("A", "B", DrugInteractionGraph.InteractionSeverity.MAJOR, "note");

        assertTrue(graph.hasDirectInteraction("A", "B"));
        assertTrue(graph.hasDirectInteraction("B", "A"));
    }

    @Test
    void noInteractionReturnsEmpty() {
        DrugInteractionGraph graph = new DrugInteractionGraph();
        graph.addDrug("A");
        graph.addDrug("B");
        assertFalse(graph.hasDirectInteraction("A", "B"));
    }

    @Test
    void findsShortestIndirectPath() {
        DrugInteractionGraph graph = new DrugInteractionGraph();
        graph.addInteraction("A", "B", DrugInteractionGraph.InteractionSeverity.MINOR, "n");
        graph.addInteraction("B", "C", DrugInteractionGraph.InteractionSeverity.MINOR, "n");

        List<String> path = graph.findShortestInteractionPath("A", "C");
        assertEquals(List.of("A", "B", "C"), path);
    }

    @Test
    void unknownDrugReturnsEmptyPath() {
        DrugInteractionGraph graph = new DrugInteractionGraph();
        graph.addDrug("A");
        assertTrue(graph.findShortestInteractionPath("A", "ZZZ").isEmpty());
    }
}
