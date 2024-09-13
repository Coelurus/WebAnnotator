module Persistence {
    exports cz.cuni.mff.vopalenf.persistence.repositories;
    exports cz.cuni.mff.vopalenf.persistence.entities;
    requires spring.data.jpa;
    requires spring.context;
    requires jakarta.persistence;
}