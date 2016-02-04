package org.zachary.test;

import org.junit.Test;
import org.zachary.learn.HelloLucene;

/**
 * Created by ZacharyChang.
 */
public class TestLucene {
    @Test
    public void testLucene() {
        HelloLucene.indexer();
    }

    @Test
    public void testSearcher() {
        HelloLucene.searcher();
    }
}
