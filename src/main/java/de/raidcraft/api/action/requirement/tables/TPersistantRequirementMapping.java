package de.raidcraft.api.action.requirement.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Getter
@Setter
@Entity
@Table(name = "raidcraft_persistant_requirement_mappings")
public class TPersistantRequirementMapping {

    @Id
    private int id;
    @ManyToOne
    @Column(name = "requirement_id")
    private TPersistantRequirement requirement;
    private String mappedKey;
    private String mappedValue;
}
