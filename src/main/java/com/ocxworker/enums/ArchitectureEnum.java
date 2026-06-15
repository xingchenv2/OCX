package com.ocxworker.enums;

import lombok.Generated;

public enum ArchitectureEnum {
    ARM("ARM", "VM.Standard.A1.Flex"),
    AMD("AMD", "VM.Standard.E2.1.Micro");

    private final String architecture;
    private final String shape;

    public static String getShape(String architecture) {
        for (ArchitectureEnum e : values()) {
            if (e.getArchitecture().equalsIgnoreCase(architecture)) {
                return e.getShape();
            }
        }

        return ARM.getShape();
    }

    @Generated
    public String getArchitecture() {
        return this.architecture;
    }

    @Generated
    public String getShape() {
        return this.shape;
    }

    @Generated
    private ArchitectureEnum(final String architecture, final String shape) {
        this.architecture = architecture;
        this.shape = shape;
    }
}
