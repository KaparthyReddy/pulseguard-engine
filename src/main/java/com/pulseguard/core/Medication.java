package com.pulseguard.core;

import java.util.Objects;

public class Medication {
    private final String id;
    private final String name;
    private final String drugClass;
    private final double concentrationMgPerMl;

    public Medication(String id, String name, String drugClass, double concentrationMgPerMl) {
        if (concentrationMgPerMl <= 0) {
            throw new IllegalArgumentException("Concentration must be positive");
        }
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.drugClass = Objects.requireNonNull(drugClass);
        this.concentrationMgPerMl = concentrationMgPerMl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDrugClass() { return drugClass; }
    public double getConcentrationMgPerMl() { return concentrationMgPerMl; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medication)) return false;
        Medication that = (Medication) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return name + " (" + drugClass + ", " + concentrationMgPerMl + " mg/mL)";
    }
}
