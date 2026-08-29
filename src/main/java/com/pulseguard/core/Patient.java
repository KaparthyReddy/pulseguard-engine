package com.pulseguard.core;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Patient {
    private final String id;
    private final String name;
    private final double weightKg;
    private final double heightCm;
    private final Set<String> allergies = new HashSet<>();
    private final Set<String> activeMedicationIds = new HashSet<>();

    public Patient(String id, String name, double weightKg, double heightCm) {
        if (weightKg <= 0 || heightCm <= 0) {
            throw new IllegalArgumentException("Weight and height must be positive");
        }
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.weightKg = weightKg;
        this.heightCm = heightCm;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getWeightKg() { return weightKg; }
    public double getHeightCm() { return heightCm; }

    /** Mosteller formula for body surface area. */
    public double getBodySurfaceAreaM2() {
        return Math.sqrt((heightCm * weightKg) / 3600.0);
    }

    public void addAllergy(String allergen) { allergies.add(allergen.toLowerCase()); }
    public boolean isAllergicTo(String substance) { return allergies.contains(substance.toLowerCase()); }
    public Set<String> getAllergies() { return new HashSet<>(allergies); }

    public void addActiveMedication(String medicationId) { activeMedicationIds.add(medicationId); }
    public void removeActiveMedication(String medicationId) { activeMedicationIds.remove(medicationId); }
    public Set<String> getActiveMedicationIds() { return new HashSet<>(activeMedicationIds); }

    @Override
    public String toString() {
        return String.format("Patient[%s, %s, %.1fkg]", id, name, weightKg);
    }
}
