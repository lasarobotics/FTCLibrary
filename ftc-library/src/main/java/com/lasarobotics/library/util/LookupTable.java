package com.lasarobotics.library.util;

import java.util.Hashtable;

/**
 * Implements a variable LUT.
 */
public class LookupTable<T> {
    private Hashtable<String, T> table;

    /**
     * Instantiate a lookup table for variables.
     */
    public LookupTable() {
        table = new Hashtable<String, T>();
    }

    /**
     * Create a clone from another Hashtable.
     *
     * @param other Another Hashtable.
     */
    public LookupTable(Hashtable<String, T> other) {
        table = other;
    }

    /**
     * Create a clone based on another LookupTable.
     *
     * @param other Another LookupTable of the same type.
     */
    public LookupTable(LookupTable<T> other) {
        table = other.getTable();
    }

    /**
     * Set the value of an item in the table, or create if new.
     *
     * @param id    The ID of the item in the LUT.
     * @param value The value to set the id to.
     */
    public void setValue(String id, T value) {
        table.put(id, value);
    }

    /**
     * Get the value of an id in the LUT.
     *
     * @param id The ID of the item to retrieve.
     * @return The value of the item at the id.
     */
    public T getValue(String id) {
        return table.get(id);
    }

    /**
     * Remove a value from the table at a specific ID.
     *
     * @param id The ID of an item in the table.
     */
    public void deleteValue(String id) {
        table.remove(id);
    }

    /**
     * The count of items in the table.
     *
     * @return The count of items in the table.
     */
    public int count() {
        return table.size();
    }

    /**
     * Gets the underlying Hashtable instance.
     *
     * @return The underlying Hashtable.
     */
    protected Hashtable<String, T> getTable() {
        return table;
    }


}
