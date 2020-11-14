import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;


public class Index {
	private FSDirectory directory = null;
	private IndexWriter writer = null;
	public Index(String path) {
		try {
			directory = FSDirectory.open(Paths.get(path));
			Analyzer analyzer = new SimpleAnalyzer();
			IndexWriterConfig cf = new IndexWriterConfig(analyzer);
			cf.setOpenMode(OpenMode.CREATE);
			writer = new IndexWriter(directory, cf);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void indexFile(File f) {
		try {
			Document d = new Document();
			BufferedReader r = new BufferedReader(
					new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String content = "";
			String s;
			while ((s = r.readLine()) != null) {
				content = content + s + " ";
			}
			r.close();
			
			TextField noidung = new TextField("noidung", content, Field.Store.NO);
			StringField ten = new StringField("tenfile", f.getName(),Field.Store.YES);
			d.add(noidung);
			d.add(ten);
			writer.addDocument(d);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void index(String fold) {
		try {
			File fld = new File(fold);
			if (!fld.isDirectory())
				return;
			for(File f : fld.listFiles())
				if (f.isFile())
					indexFile(f);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String arg[]) {
		Index obj = new Index("index");
		obj.index("data");
	}
}
