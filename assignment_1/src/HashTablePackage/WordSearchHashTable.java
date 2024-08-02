package HashTablePackage;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;

public class WordSearchHashTable implements HashTableInterface {

    private final HashFunction HASH_FUNCTION;

    private HashTable hashTable;

    public WordSearchHashTable(int initialSize, float loadFactor, HashFunction hashFunction, HashTable.CollisionHandling collisionHandling) {
        this.HASH_FUNCTION = hashFunction;
        this.hashTable = new HashTable<String, ArrayList<FileStat>>(initialSize,loadFactor,collisionHandling);
    }

    public void addWord(String word, FileStat fileStat) throws NullValueException {

        if (fileStat == null) throw new NullValueException("Attempt to add null to Hash Table");
        else {
            int index = getIndex(word);
            int initialIndex = index;
            while (true) {
                index = index % hashTable.getEntrySize();
                if (hashTable.getEntry(index) == null) {
                    HashTablePackage.HashTable.TableEntry<String, ArrayList<FileStat>> newTableEntry = new HashTablePackage.HashTable.TableEntry<String, ArrayList<FileStat>>();
                    newTableEntry.key = word;
                    newTableEntry.value = new ArrayList<FileStat>();
                    newTableEntry.value.add(fileStat);
                    hashTable.setEntry(index, newTableEntry);
                    hashTable.entryCount++;
                    break;
                }
                else {
                    if (hashTable.getEntry(index).key.equals(word)) {
                        HashTablePackage.HashTable.TableEntry<String, ArrayList<FileStat>> tableEntry = hashTable.getEntry(index);
                        tableEntry.value.add(fileStat);
                        break;
                    } else {
                        index = hashTable.handleCollision(index, initialIndex);
                    }
                }
            }
            if ((float) hashTable.entryCount / (float) hashTable.getEntrySize() > hashTable.LOAD_FACTOR())
                resize();
        }
    }

    void resize() {
        HashTablePackage.HashTable.TableEntry[] oldEntryTable = hashTable.getEntryTable();
        hashTable = new HashTable<String, ArrayList<FileStat>>(hashTable.getEntrySize()*2,hashTable.LOAD_FACTOR(),hashTable.COLLISION_HANDLING());

        for (HashTable.TableEntry<String,ArrayList<FileStat>> tableEntry : oldEntryTable)
            if (tableEntry != null) {
                int index = getIndex(tableEntry.key);
                int initialIndex = index;
                while (true) {
                    index = index % hashTable.getEntrySize();
                    if (hashTable.getEntry(index) == null) {
                        hashTable.setEntry(index,tableEntry);
                        break;
                    } else index = hashTable.handleCollision(index, initialIndex);
                }
            }
    }

    public ArrayList<FileStat> getValue(String key) {
        int index = getIndex(key);
        int initialIndex = index;
        while (true) {
            index = index % hashTable.getEntrySize();
            if (hashTable.getEntry(index) == null) {
                return null; //value DNE
            }
            else if (key.equals(hashTable.getEntry(index).key)) {
                HashTablePackage.HashTable.TableEntry<String, ArrayList<FileStat>> tableEntry = hashTable.getEntry(index);
                return tableEntry.value;
            }
            else {
                index = hashTable.handleCollision(index, initialIndex);
            }
        }
    }

    private int getIndex(String key) {
        if (HASH_FUNCTION == HashFunction.SSF) {
            return SSF(key);
        }
        else {
            return PAF(key);
        }
    }
    private int SSF(String input) {
        int sum = 0;
        for (char ch : input.toCharArray()) {
            sum += (int) ch;
            sum = sum % hashTable.getEntrySize();
        }
        return sum;
    }

    private final int MULTIPLIER_CONSTANT = 31;
    public int PAF(String input) {
        int sum = 0;
        int counter = 1;
        for (char ch : input.toCharArray()) {
            sum += ch * Math.pow(MULTIPLIER_CONSTANT,(input.length() - counter));
            counter++;
            sum = sum % hashTable.getEntrySize();
        }
        return sum;
    }

    public int getCollisionCount() {
        return hashTable.getCollisionCount();
    }

    public enum HashFunction {
        SSF,PAF
    }

    public static class FileStat {
        private String fileName;
        private int count;

        public FileStat(String fileName, int count) {
            this.fileName = fileName;
            this.count = count;
        }

        public String getFileName() {
            return fileName;
        }

        public int getCount() {
            return count;
        }
    }

    public void printSearchStats(String[] input) {
        long currentTime = System.nanoTime();
        testFetch(input[0]);
        long elapsedTime = System.nanoTime() - currentTime;
        long timeSum = elapsedTime;

        long shortestTime = elapsedTime;
        long longestTime = elapsedTime;

        for (int i = 1; i < input.length; i++) {
            currentTime = System.nanoTime();
            testFetch(input[i]);
            elapsedTime = System.nanoTime() - currentTime;
            timeSum+=elapsedTime;
            if (elapsedTime > longestTime) {
                longestTime = elapsedTime;
            }
            else if (elapsedTime < shortestTime) {
                shortestTime = elapsedTime;
            }
        }

        System.out.println("Shortest Time: "+shortestTime);
        System.out.println("Longest Time: "+longestTime);
        System.out.println("Average Time: "+(timeSum/input.length));
    }

    private void testFetch(String key) {
        int index = getIndex(key);
        int initialIndex = index;
        while (true) {
            index = index % hashTable.getEntrySize();
            if (hashTable.getEntry(index) == null) {
                break;
            }
            else if (key.equals(hashTable.getEntry(index).key)) {
                break;
            }
            else {
                index = hashTable.handleCollision(index, initialIndex);
            }
        }
    }
}
