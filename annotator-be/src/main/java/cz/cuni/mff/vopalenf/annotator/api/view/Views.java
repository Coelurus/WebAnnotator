package cz.cuni.mff.vopalenf.annotator.api.view;

/**
 * Defines the views used for serialization and deserialization of entities in
 * the API. These views are used to control which fields are included in the
 * JSON output. This way StackOverflow can be avoided when serializing entities
 * with circular references. For example Team contains User as leader and User
 * contains Team which contains User as leader...
 */
public interface Views {
    /**
     * View that includes all fields.
     */
    interface BothView {
    }

    /**
     * View that includes leader in team but not team in its user members.
     */
    interface ShowUsersInTeams extends BothView {
    }

    /**
     * View that includes team in its user members but not leader in team.
     */
    interface ShowTeamsInUsers extends BothView {
    }
}
