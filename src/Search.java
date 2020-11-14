import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


public class Search {
	private FSDirectory directory = null;
	private IndexReader reader = null;
	private IndexSearcher searcher = null;
	
	public Search(String fold) {
		try {
			directory = FSDirectory.open(Paths.get(fold));
			reader = DirectoryReader.open(directory);
			searcher = new IndexSearcher(reader);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> search(String exp) {
		ArrayList<String> ret = new ArrayList<String>();
		
		SimpleAnalyzer analyzer = new SimpleAnalyzer();
		QueryParser parser = new QueryParser("noidung", analyzer);
		try {
			Query query = parser.parse(exp);
			TopDocs docs = searcher.search(query, 18);
			ScoreDoc score[] = docs.scoreDocs;
			for (int i = 0; i < score.length; i++) {
				Document d = reader.document(score[i].doc);
				System.out.println(score[i]);
				ret.add(d.get("tenfile"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static void main(String arg[]) {
		Search obj = new Search("index");
		ArrayList<String> result = obj.search("núi Thiên Ấn");
		
		for (String s : result) {
			System.out.println(s);
		}
	}
}
