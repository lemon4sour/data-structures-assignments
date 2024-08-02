package HashTablePackage;

import java.util.ArrayList;

public interface HashTableInterface<K,V> {
    void addWord(String word, WordSearchHashTable.FileStat fileStat) throws NullValueException;
    ArrayList<WordSearchHashTable.FileStat> getValue(String key);
}
