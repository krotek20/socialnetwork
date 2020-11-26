package socialnetwork.domain;

public interface Entity<ID> {
    void setID(ID id);

    ID getID();
}