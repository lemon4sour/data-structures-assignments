package HashTablePackage;

public class HashTable<K,V> {
    private TableEntry[] entryTable;

    int entryCount;
    private final float LOAD_FACTOR;
    private final CollisionHandling COLLISION_HANDLING;

    private int collisionCount = 0;

    public float LOAD_FACTOR() {
        return LOAD_FACTOR;
    }

    public CollisionHandling COLLISION_HANDLING() {
        return COLLISION_HANDLING;
    }

    static class TableEntry<X,Y> {
        boolean isDeleted;
        X key;
        Y value;
    }

    public HashTable(int initialSize, float loadFactor, CollisionHandling collisionHandling) {
        entryTable = new TableEntry[initialSize];
        entryCount = 0;
        this.LOAD_FACTOR = loadFactor;
        this.COLLISION_HANDLING = collisionHandling;
        collisionCount = 0;
    }

    public void setEntry(int index, TableEntry entry) {
        entryTable[index] = entry;
    }

    public TableEntry getEntry(int index) {
        return entryTable[index];
    }

    public int getEntrySize() {
        return entryTable.length;
    }

    public TableEntry[] getEntryTable() {
        return entryTable;
    }

    public int handleCollision(int oldIndex, int initialIndex) {
        collisionCount++;
        if (COLLISION_HANDLING == CollisionHandling.LinearProbe)
            return linearProbe(oldIndex);
        else
            return doubleHash(oldIndex, initialIndex);
    }

    private int linearProbe(int index) {
        return (index + 1);
    }

    private final int PRIME_NUMBER = 31;
    private int doubleHash(int index, int initialIndex) {
        return (index + (PRIME_NUMBER - (initialIndex % PRIME_NUMBER)));
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public enum CollisionHandling {
        LinearProbe,DoubleHash
    }
}

