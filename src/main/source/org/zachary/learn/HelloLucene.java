package org.zachary.learn;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by ZacharyChang.
 */
public class HelloLucene {
    public static void indexer() {
        // create IndexWriter
        IndexWriter writer = null;
        try {
            // create Directory
            Directory directory = FSDirectory.open(new File("C:\\Dev\\lucene_test\\hellolucene"));
            // create IndexWriterConfig and IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
            writer = new IndexWriter(directory, config);
            // create Document
            Document document;
            // add Field into document
            File path = new File("C:\\Dev\\lucene_test\\data");
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    document = new Document();
                    document.add(new Field("content", new FileReader(file)));
                    document.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                    document.add(new Field("path", file.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                    writer.addDocument(document);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void searcher() {
        try {
            // 创建Directory
            Directory directory = FSDirectory.open(new File("C:\\Dev\\lucene_test\\hellolucene"));
            // 创建IndexReader
            IndexReader reader = IndexReader.open(directory);
            // 根据IndexReader创建IndexSearcher
            IndexSearcher searcher = new IndexSearcher(reader);
            // 创建搜索的QueryParser，确定搜索域
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            // 创建Query，搜索指定域中包含某字段
            Query query = parser.parse("apache");
            // 使用IndexSearcher搜索并返回TopDocs
            TopDocs topDocs = searcher.search(query, 20);
            // 根据返回TopDocs获取ScoreDoc对象数组
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 根据IndexSearcher和ScoreDoc对象获取Document对象
                Document document = searcher.doc(scoreDoc.doc);
                // 输出值
                System.out.println(document.get("path") + "\\" + document.get("filename"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
