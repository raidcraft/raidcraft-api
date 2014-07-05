package de.raidcraft.api.language.table;

import com.avaje.ebean.validation.Length;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a Ebean-mapped language object model.
 */
@Getter
@Setter
@Entity
@Table(name = "rc_language")
public class TLanguage extends Model {

    /**
     * A lowercase ISO 639-2 language code.
     *
     * @see <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO 639-2 Language Code List</a>
     */
    @Id
    @Column(columnDefinition = "CHAR", unique = true, length = 3)
    @Length(min = 3)
    private String code;

    /**
     * A name for the language that is appropriate for display to the user.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "DESC", nullable = false, length = 32)
    private String name;

    public static Finder<String, TLanguage> find = new TLanguage.Finder<>(String.class, TLanguage.class, RaidCraftPlugin.class);
}
