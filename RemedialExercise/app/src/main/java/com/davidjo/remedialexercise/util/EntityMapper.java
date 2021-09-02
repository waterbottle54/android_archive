package com.davidjo.remedialexercise.util;

public interface EntityMapper <Entity, DomainModel> {

    DomainModel mapFromEntity(Entity entity);

    Entity mapToEntity(DomainModel domainModel);
}
