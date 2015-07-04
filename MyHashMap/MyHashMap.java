package week1;

import java.util.*;

/**
 * Created by Джек on 02.07.2015.
 */
public class MyHashMap<K,V> implements Map<K,V> {

    public static int DEFAULT_TABLE_SIZE = 16;
    private int size = 0;
    private Bucket<K,V>[] table;
    private double loadFactor = 0.75;

    public MyHashMap() {
        table = new Bucket[DEFAULT_TABLE_SIZE];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {

        int position = findPosition(key);

        if(table[position] == null){
            return false;
        } else {
            Bucket<K,V> iter = findInBucket(key, position);
            return iter != null;
        }

    }

    @Override
    public boolean containsValue(Object value) {

        Iterator<Bucket<K,V>> iter = new BucketIterator();

        while (iter.hasNext()) {
            if (iter.next().getValue().equals(value)) {
                return true;
            }
        }

        return false; // iterator
    }

    @Override
    public V get(Object key) {

        int position = findPosition(key);

        if(table[position] == null){
            return null;
        } else {
            Bucket<K,V> iter = findInBucket(key, position);
            return iter != null ? iter.value : null;
        }
    }

    @Override
    public V put(K key, V value) {

        int hash = Math.abs(key.hashCode());

        int position = hash % table.length;

        if(table[position] == null){
            table[position] = new Bucket<K, V>(key,value);
        } else {

            Bucket iter = table[position];

            // find last node in bucket

            while(iter.next != null){
                iter = iter.next;
            }

            iter.next = new Bucket<K,V>(key,value);


        }

        size++;
        System.out.println("Length - " + table.length);
       // reSize();

        return null;
    }
    private void reSize() {
        if ((1.0 *size) / DEFAULT_TABLE_SIZE > loadFactor) {
            Bucket<K,V>[] resizeTable = new Bucket [table.length * 2];
            BucketIterator iter = new BucketIterator();
            while (iter.hasNext()) {
                int hash = Math.abs(iter.next().getKey().hashCode());

                int position = hash % resizeTable.length;
                if(resizeTable[position] == null){
                    resizeTable[position] = new Bucket<K, V>(iter.next().getKey(),iter.next().getValue());
                } else {

                    Bucket iterator = resizeTable[position];

                    // find last node in bucket
                   while(iterator.next != null) {
                      iterator = iterator.next;
                   }
                    iterator.next = new Bucket<K, V>(iter.next().getKey(), iter.next().getValue());
                }

            }
            table = resizeTable;

        }
    }

    @Override
    public V remove(Object key) {

        int hash = Math.abs(key.hashCode());

        int position = hash % table.length;

        if (table[position] != null) {
            Bucket<K, V> iter = table[position];
            if (iter.key.equals(key)) {
                V value = iter.value;
                if (iter.next == null) {
                    table[position] = null;
                } else {
                    table[position] = iter.next;
                }
                size--;
                return value;

            } else {
                // check nodes in bucket
                while (iter.next != null){
                    iter = iter.next;
                    if (iter.key.equals(key)) {
                        V value = iter.value;
                        size--;
                        return value;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++){
            table[i] = null;
        }
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<K>();
        Iterator <Bucket<K,V>> iter = new BucketIterator();

        while (iter.hasNext()) {
            set.add(iter.next().getKey());
        }
        return set;
    }

    @Override

    public Collection<V> values() {
        Collection<V> val = new ArrayList<V>();
        BucketIterator iter = new BucketIterator();

        while (iter.hasNext()) {
            val.add(iter.next().getValue());
        }
        return val;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    private class BucketIterator implements Iterator<Bucket<K,V>> {

        Bucket<K,V> current;
        int index = 0;

        public BucketIterator() {


            findTableIndex ();

        }

        private void findTableIndex () {
            while (index < table.length && table[index] == null) {
                index++;
            }
            current = index == table.length ? null : table[index];
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Bucket<K,V> next() {
            Bucket <K,V> iterator = current;
            if (iterator.next != null) {
                current = iterator.next;
            } else {
                index++;
                findTableIndex ();
            }
            return iterator;
        }
    }

    private Bucket<K,V> findInBucket(Object key, int position){
        Bucket<K,V> iter = table[position];

        // find last node in bucket
        do {
            if (iter.key.equals(key)) {
                return iter;
            }

            iter = iter.next;
        }
        while (iter.next != null);

        return null;
    }

    private int findPosition(Object key){
        int hash = Math.abs(key.hashCode());
        return hash % table.length;
    }

    private static class Bucket<KB,VB> {
        KB key;
        VB value;
        Bucket<KB,VB> next;

        public Bucket(KB key, VB value, Bucket next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Bucket(KB key, VB value) {
            this.key = key;
            this.value = value;
        }

        public KB getKey() {
            return key;
        }

        public VB getValue() {
            return value;
        }

        public Bucket<KB, VB> getNext() {
            return next;
        }

        public void setKey(KB key) {
            this.key = key;
        }

        public void setValue(VB value) {
            this.value = value;
        }

        public void setNext(Bucket<KB, VB> next) {
            this.next = next;
        }
    }
}
