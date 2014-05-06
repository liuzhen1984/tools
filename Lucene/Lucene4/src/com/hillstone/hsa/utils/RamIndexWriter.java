package com.hillstone.hsa.utils;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 13-5-28
 * Time: 下午4:15
 * To change this template use File | Settings | File Templates.
 */
public class RamIndexWriter {
    private IndexWriter ramIndexWriter = null;
    private Directory ramDirectory = new RAMDirectory();

    public RamIndexWriter() throws IOException {
        setRamIndexWriter(this.ramDirectory);
    }
    public void closeRamIndexWriter(){
        try {
            this.ramIndexWriter.close();
            this.ramIndexWriter = null;
            this.ramDirectory.close();
            this.ramDirectory = null;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public IndexWriter getRamIndexWriter() {
        return ramIndexWriter;
    }

    public void setRamIndexWriter(Directory ramDirecotry) throws IOException {
        this.ramIndexWriter = new IndexWriter(ramDirecotry,LuceneUtils.indexWriterConfig);
    }
    public Directory getRamDirectory(){
        return this.ramDirectory;
    }
}
