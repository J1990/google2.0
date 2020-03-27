package com.mycompany.app.DocumentLoader.laTimes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LATimesLoader {

	public static List<Document> loadLaTimesDocs(String pathToLADocs) throws IOException {

		List<Document> laDocs = new ArrayList<>();

		File folder = new File(pathToLADocs);
		File[] allFiles = folder.listFiles();

		for (File file : allFiles) {

			org.jsoup.nodes.Document allContent = Jsoup.parse(file, null, "");

			Elements docs = allContent.select("DOC");

			for (Element doc : docs) {
				String docNo, headline, text;
				docNo = (doc.select("DOCNO").text());
				headline = (doc.select("HEADLINE").select("P").text());
				text = (doc.select("TEXT").select("P").text());
				laDocs.add(createDocument(docNo, headline, text));
			}
		}
		return laDocs;
	}

	private static org.apache.lucene.document.Document createDocument(String docNo, String headline, String text) {
		org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
		document.add(new StringField("docno", docNo, Field.Store.YES));
		document.add(new TextField("headline", headline, Field.Store.YES));
		document.add(new TextField("text", text, Field.Store.YES));
		return document;
	}
}
