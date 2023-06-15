package io.github.palexdev.enbmanager.frontend.components.misc;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This model class can be used by UI components that show a list of items to allow/keep track of their selection.
 *
 * @param <T> the kind of items contained by the selection model
 */
@SuppressWarnings("unchecked")
public class SelectionModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            clearSelection();
        }
    };
    private final MapProperty<Integer, T> selection = new SimpleMapProperty<>(getMap());
    private boolean allowMultipleSelection = true;

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Clears the selection by setting it to an empty map.
     */
    public void clearSelection() {
        selection.set(getMap());
    }

    /**
     * Removes the given index from the selection map.
     */
    public void deselectIndex(int index) {
        selection.remove(index);
    }

    /**
     * Removes the given item from the selection map, note that this is slower than removing by index.
     */
    public void deselectItem(T item) {
        selection.values().remove(item);
    }

    /**
     * Removes all the specified indexes from the selection map, done
     * by creating a temporary map, updating it and then replacing the
     * selection.
     */
    public void deselectIndexes(int... indexes) {
        ObservableMap<Integer, T> tmp = getMap(selection);
        for (int index : indexes) {
            tmp.remove(index);
        }
        selection.set(tmp);
    }

    /**
     * Removes all the specified items from the selection map, done by creating a temporary map, updating it and finally
     * replacing the selection. Note that this is slower than removal by index.
     */
    public void deselectItems(T... items) {
        ObservableMap<Integer, T> tmp = getMap(selection);
        for (T item : items) {
            tmp.values().remove(item);
        }
        selection.set(tmp);
    }

    /**
     * If multiple selection is allowed adds the given index (and the retrieved item) to the selection map,
     * otherwise creates a new temporary map containing only the given index-item entry and replaces the selection.
     */
    public void updateSelection(int index) {
        T item = getItems().get(index);
        if (allowMultipleSelection) {
            selection.put(index, item);
        } else {
            ObservableMap<Integer, T> map = getMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    /**
     * If multiple selection is allowed adds the given item (and the retrieved index) to the selection map,
     * otherwise creates a new temporary map containing only the given index-item entry and replaces the selection.
     * Note that this is slower than adding by index.
     */
    public void updateSelection(T item) {
        int index = getItems().indexOf(item);
        if (allowMultipleSelection) {
            selection.put(index, item);
        } else {
            ObservableMap<Integer, T> map = getMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    /**
     * If multiple selection is allowed adds all the given indexes to the selection
     * (and the retrieved items), otherwise replaces the selection with the first index given in the list.
     */
    public void updateSelectionByIndexes(List<Integer> indexes) {
        if (indexes.isEmpty()) return;
        if (allowMultipleSelection) {
            Set<Integer> indexesSet = new LinkedHashSet<>(indexes);
            Map<Integer, T> newSelection = indexesSet.stream().collect(Collectors.toMap(
                i -> i,
                getItems()::get,
                (t, t2) -> t2,
                LinkedHashMap::new
            ));
            selection.putAll(newSelection);
        } else {
            int index = indexes.get(0);
            T item = getItems().get(index);
            ObservableMap<Integer, T> map = getMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    /**
     * If multiple selection is allowed adds all the given items to the selection
     * (and the retrieved indexes), otherwise replaces the selection with the first item given in the list.
     * Note that this is slower than adding by index.
     */
    public void updateSelectionByItems(List<T> items) {
        if (items.isEmpty()) return;
        if (allowMultipleSelection) {
            Set<Integer> indexesSet = items.stream()
                .mapToInt(items::indexOf)
                .boxed()
                .collect(Collectors.toSet());
            Map<Integer, T> newSelection = indexesSet.stream().collect(Collectors.toMap(
                i -> i,
                items::get
            ));
            selection.putAll(newSelection);
        } else {
            T item = items.get(0);
            int index = items.indexOf(item);
            ObservableMap<Integer, T> map = getMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    /**
     * This is responsible for expanding the selection towards the given index.
     * There are 4 cases to consider:
     * <p> 1) The selection is empty: the new selection will go from [0 to index]
     * <p> 2) The minimum selected index is equal to the given index: the new selection will just be [index]
     * <p> 3) The given index is lesser than the minimum index: the new selection will go from [index to min]
     * <p> 4) The given index is greater than the minimum index: the new selection will go from [min to index]
     */
    public void expandSelection(int index) {
        if (selection.isEmpty()) {
            replaceSelection(IntegerRange.expandRangeToArray(0, index));
            return;
        }

        int min = selection.keySet().stream().min(Integer::compareTo).orElse(-1);
        if (index == min) {
            replaceSelection(index);
            return;
        }

        if (index < min) {
            replaceSelection(IntegerRange.expandRangeToArray(index, min));
        } else {
            replaceSelection(IntegerRange.expandRangeToArray(min, index));
        }
    }

    /**
     * If multiple selection is allowed replaces the selection with all the given indexes
     * (and the retrieved items), otherwise replaces the selection with the first given index.
     */
    public void replaceSelection(Integer... indexes) {
        ObservableMap<Integer, T> newSelection = getMap();
        if (allowMultipleSelection) {
            newSelection.putAll(
                Arrays.stream(indexes).collect(Collectors.toMap(
                    i -> i,
                    getItems()::get
                ))
            );
        } else {
            int index = indexes[0];
            newSelection.put(index, getItems().get(index));
        }
        selection.set(newSelection);
    }

    /**
     * If multiple selection is allowed replaces the selection with all the given items
     * (and the retrieved indexes), otherwise replaces the selection with the first given item.
     * Note that this is slower than replacing by index.
     */
    public void replaceSelection(T... items) {
        ObservableMap<Integer, T> newSelection = getMap();
        if (allowMultipleSelection) {
            newSelection.putAll(
                Arrays.stream(items).collect(Collectors.toMap(
                    getItems()::indexOf,
                    item -> item
                ))
            );
        } else {
            T item = items[0];
            newSelection.put(getItems().indexOf(item), item);
        }
        selection.set(newSelection);
    }

    /**
     * Builds a new observable hash map backed by a {@link LinkedHashMap}.
     */
    protected ObservableMap<Integer, T> getMap() {
        return FXCollections.observableMap(new LinkedHashMap<>());
    }

    /**
     * Builds a new observable hash map backed by a {@link LinkedHashMap}, initialized with the given map.
     */
    protected ObservableMap<Integer, T> getMap(Map<Integer, T> map) {
        return FXCollections.observableMap(new LinkedHashMap<>(map));
    }

    //================================================================================
    // Getters
    //================================================================================
    public ObservableList<T> getItems() {
        return items.get();
    }

    /**
     * Specifies the {@link ObservableList} of items on which the model operates.
     * <p>
     * When the list changes, the selection is cleared, {@link #clearSelection()}.
     */
    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    /**
     * @return the {@link ObservableMap} containing the selection
     */
    public ObservableMap<Integer, T> getSelection() {
        return selection;
    }

    public ReadOnlyIntegerProperty sizeProperty() {return selection.sizeProperty();}

    public ReadOnlyBooleanProperty emptyProperty() {return selection.emptyProperty();}

    /**
     * @return the {@link Set} containing the selected indexes
     */
    public Set<Integer> getSelectedIndexes() {
        return selection.keySet();
    }

    /**
     * @return the selected indexes but in an unmodifiable list
     */
    public List<Integer> getSelectedIndexesList() {
        return List.copyOf(selection.keySet());
    }

    /**
     * @return the collection containing the selected items
     */
    public Collection<T> getSelectedItems() {
        return selection.values();
    }

    /**
     * @return the selected items but in an unmodifiable list
     */
    public List<T> getSelectedItemsList() {
        return List.copyOf(selection.values());
    }

    /**
     * Shortcut to get the first selected index, can be useful especially when the model is set
     * to single selection mode.
     */
    public Integer getSelectedIndex() {
        try {
            List<Integer> indexes = getSelectedIndexesList();
            return indexes.get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Shortcut to get the first selected item, can be useful especially when the model is set
     * to single selection mode.
     */
    public T getSelectedItem() {
        try {
            List<T> items = getSelectedItemsList();
            return items.get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Specifies whether the model allows to select multiple [index, item] pairs.
     */
    public boolean doesAllowMultipleSelection() {
        return allowMultipleSelection;
    }

    /**
     * Sets the selection behavior of this model to be multiple (true) or
     * single (false).
     * <p>
     * When this is set to false, the selection is also cleared!
     *
     * @see #doesAllowMultipleSelection()
     */
    public void setAllowMultipleSelection(boolean allowMultipleSelection) {
        if (!allowMultipleSelection) clearSelection();
        this.allowMultipleSelection = allowMultipleSelection;
    }
}
