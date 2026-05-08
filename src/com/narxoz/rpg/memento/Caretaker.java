package com.narxoz.rpg.memento;

import com.narxoz.rpg.combatant.HeroMemento;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Stores hero snapshots for the Chronomancer's Vault rewind mechanic.
 *
 * This class intentionally sits in a different package from {@link HeroMemento}
 * so it can only treat mementos as opaque values. Notice we never call any
 * getter on HeroMemento here — we only push, pop and peek.
 */
public class Caretaker {

    private final Deque<HeroMemento> history = new ArrayDeque<>();

    /**
     * Saves a snapshot to the caretaker history.
     *
     * @param memento the snapshot to store
     */
    public void save(HeroMemento memento) {
        if (memento == null) {
            throw new IllegalArgumentException("Cannot save a null memento");
        }
        history.push(memento);
    }

    /**
     * Removes and returns the most recent snapshot.
     *
     * @return the latest stored snapshot, or null if history is empty
     */
    public HeroMemento undo() {
        if (history.isEmpty()) {
            return null;
        }
        return history.pop();
    }

    /**
     * Returns the most recent snapshot without removing it.
     *
     * @return the latest stored snapshot, or null if history is empty
     */
    public HeroMemento peek() {
        return history.peek();
    }

    /**
     * Reports how many snapshots are stored.
     *
     * @return the number of saved snapshots
     */
    public int size() {
        return history.size();
    }
}
