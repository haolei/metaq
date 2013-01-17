package com.taobao.metamorphosis.client.extension.storage;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.taobao.common.store.journal.IndexMap;
import com.taobao.common.store.journal.JournalStore;
import com.taobao.common.store.journal.OpItem;
import com.taobao.common.store.util.BytesKey;


/**
 * ��֤�Ƚ��ȳ�˳���JournalStore
 * 
 * @author �޻�
 * @since 2011-8-12 ����10:30:56
 */

public class MessageStore extends JournalStore {
    public MessageStore(String path, String name) throws IOException {
        super(path, name, new MessageIndexMap(), false, false, false);
    }

    static class MessageIndexMap implements IndexMap {
        private final Lock lock = new ReentrantLock();
        private final Map<BytesKey, OpItem> map;


        MessageIndexMap() {
            this.map = new LinkedHashMap<BytesKey, OpItem>();
        }


        @Override
        public void put(BytesKey key, OpItem opItem) {
            this.lock.lock();
            try {
                this.map.put(key, opItem);
            }
            finally {
                this.lock.unlock();
            }
        }


        @Override
        public void remove(BytesKey key) {
            this.lock.lock();
            try {
                this.map.remove(key);
            }
            finally {
                this.lock.unlock();
            }

        }


        @Override
        public OpItem get(BytesKey key) {
            this.lock.lock();
            try {
                return this.map.get(key);
            }
            finally {
                this.lock.unlock();
            }
        }


        @Override
        public int size() {
            this.lock.lock();
            try {
                return this.map.size();
            }
            finally {
                this.lock.unlock();
            }
        }


        @Override
        public boolean containsKey(BytesKey key) {
            this.lock.lock();
            try {
                return this.map.containsKey(key);
            }
            finally {
                this.lock.unlock();
            }
        }


        @Override
        public Iterator<BytesKey> keyIterator() {
            this.lock.lock();
            try {
                return new MessageIndexMapItreator(new LinkedHashSet<BytesKey>(this.map.keySet()).iterator());
            }
            finally {
                this.lock.unlock();
            }
        }


        @Override
        public void putAll(Map<BytesKey, OpItem> map) {
            this.lock.lock();
            try {
                this.map.putAll(map);
            }
            finally {
                this.lock.unlock();
            }
        }


        @Override
        public void close() throws IOException {
            this.lock.lock();
            try {
                this.map.clear();
            }
            finally {
                this.lock.unlock();
            }
        }

        class MessageIndexMapItreator implements Iterator<BytesKey> {
            private final Iterator<BytesKey> mapIterator;
            private BytesKey currentKey;


            MessageIndexMapItreator(Iterator<BytesKey> mapIterator) {
                this.mapIterator = mapIterator;
            }


            @Override
            public boolean hasNext() {
                MessageIndexMap.this.lock.lock();
                try {
                    if (this.mapIterator.hasNext()) {
                        return true;
                    }
                    return false;
                }
                finally {
                    MessageIndexMap.this.lock.unlock();
                }
            }


            @Override
            public BytesKey next() {
                MessageIndexMap.this.lock.lock();
                try {
                    BytesKey result = null;
                    result = this.mapIterator.next();
                    this.currentKey = result;
                    return result;
                }
                finally {
                    MessageIndexMap.this.lock.unlock();
                }
            }


            @Override
            public void remove() {
                MessageIndexMap.this.lock.lock();
                try {
                    if (this.currentKey == null) {
                        throw new IllegalStateException("The next method is not been called");
                    }
                    MessageIndexMap.this.remove(this.currentKey);
                }
                finally {
                    MessageIndexMap.this.lock.unlock();
                }

            }

        }
    }
}