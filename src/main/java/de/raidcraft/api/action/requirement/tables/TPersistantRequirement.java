package de.raidcraft.api.action.requirement.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
@Getter
@Setter
@Entity
@Table(name = "raidcraft_peristant_requirements")
public class TPersistantRequirement {

    @Id
    private int id;
    private String plugin;
    private String requirement;
    private UUID uuid;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "requirement_id")
    private List<TPersistantRequirementMapping> mappings = new ArrayList<>();
}
