package org.zachary.unit1;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by ZacharyChang.
 */
public class Indexer {
    private IndexWriter writer;

    private static class TextFilesFilter implements FileFilter {
        //Index .txt files only, using FileFilter
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".txt");
        }
    }

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));
        //Create Lucene IndexWriter
        writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public int index(String dataDir, FileFilter filter) throws Exception {
        File[] files = new File(dataDir).listFiles();
        assert files != null;
        for (File f : files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }
        return writer.numDocs();    //Return number of documents indexed
    }

    private void indexFile(File f) throws Exception {
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }

    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new Field("contents", new FileReader(f)));  //Index file content
        doc.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED)); //Index file name
        doc.add(new Field("fullpath", f.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));    //Index file full path
        return doc; //Add document to Lucene index
    }

    public void close() throws IOException {
        writer.close(); //Close IndexWriter
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java " + Indexer.class.getName() + " <index dir> <data dir>");
        }
        String indexDir = args[0];  //Create index in this directory
        String dataDir = args[1];   //Index *.txt files from this directory

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();

        System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
    }
}
