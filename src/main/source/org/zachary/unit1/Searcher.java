package org.zachary.unit1;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import java.io.IOException;

/**
 * Created by ZacharyChang.
 */
public class Searcher {
    public static void search(String indexDir, String q) throws IOException, ParseException {
        //Open index
        Directory dir = FSDirectory.open(new File(indexDir));
        IndexSearcher iSearcher = new IndexSearcher(dir);
        //Parse query
        QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
        Query query = parser.parse(q);
        long start = System.currentTimeMillis();
        TopDocs docs = iSearcher.search(query, 10);    //Search index
        long end = System.currentTimeMillis();
        //Write search stats
        System.out.println("Found " + docs.totalHits + " document(s) (in " + (end - start) + " milliseconds) that matched query '" + q + "':");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = iSearcher.doc(scoreDoc.doc);   //Retrieve matching document
            System.out.println(doc.get("fullpath"));    //Display filename
        }
        iSearcher.close();  //Close IndexSearcher
    }

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java " + Searcher.class.getName() + " <index dir> <data dir>");
        }
        String indexDir = args[0];  //Parse provided index directory
        String q = args[1];   //Parse provided query string

        search(indexDir, q);
    }
}
