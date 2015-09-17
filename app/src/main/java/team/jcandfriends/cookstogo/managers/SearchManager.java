package team.jcandfriends.cookstogo.managers;

import java.util.ArrayList;

public interface SearchManager<E> {

    void add(E item);

    ArrayList<E> getAll();

    void deleteAll();

}
