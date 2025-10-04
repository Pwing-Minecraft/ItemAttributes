package net.pwing.itemattributes.attribute;

import net.pwing.itemattributes.requirement.RequirementType;

public record AttributeRequirement<T>(RequirementType<T> type, T value) {
}
