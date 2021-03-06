package com.mycompany.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.mycompany.app.Constants.Constants;
import com.mycompany.app.DocumentLoader.fbis.FBISLoader;
import com.mycompany.app.DocumentLoader.fr94.FR94Loader;
import com.mycompany.app.DocumentLoader.ft.FTLoader;
import com.mycompany.app.DocumentLoader.laTimes.LATimesLoader;
import com.mycompany.app.Index.Indexer;
import com.mycompany.app.factory.AnalyzerType;

import com.mycompany.app.factory.SimilarityType;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;

public class IndexDocuments {

    private static ArrayList<Document> docs;
    private static Indexer indexer;

    public static void main(String[] args) throws IOException {

        boolean validInput = false;
        AnalyzerType analyzerType = null;
        boolean validSimilarity = false;
        SimilarityType similarityType = null;
        boolean forceUpdate = false;
        boolean indexExists = false;

        String indexPath = Constants.INDEXED_DOCS_FILE_PATH;

        while (!validInput && !validSimilarity) {
            // clear the screen
            System.out.print("\033[H\033[2J");
            System.out.flush();

            System.out.println("Please choose an Analyser by selecting a number");
            System.out.println("1. Simple");
            System.out.println("2. English");
            System.out.println("3. Custom");
            System.out.println("4. Standard");

            String input = System.console().readLine();

            switch (input) {
                case "1":
                    analyzerType = AnalyzerType.Simple;
                    validInput = true;
                    break;
                case "2":
                    analyzerType = AnalyzerType.English;
                    validInput = true;
                    break;
                case "3":
                    analyzerType = AnalyzerType.Custom;
                    validInput = true;
                    break;
                case "4":
                    analyzerType = AnalyzerType.Standard;
                    validInput = true;
                    break;
                default:
                    analyzerType = AnalyzerType.Custom;
                    validInput = true;
                    break;
            }

            System.out.println("Please choose a Similarity by selecting a number");
            System.out.println("1. BM25Similarity");
            System.out.println("2. BooleanSimilarity");
            System.out.println("3. MultiSimilarity");
            System.out.println("4. LMDirichletSimilarity");

            input = System.console().readLine();

            switch (input) {
                case "1":
                    similarityType = SimilarityType.BM25;
                    validSimilarity = true;
                    break;
                case "2":
                    similarityType = SimilarityType.Boolean;
                    validSimilarity = true;
                    break;
                case "3":
                    similarityType = SimilarityType.Multi;
                    validSimilarity = true;
                    break;
                case "4":
                    similarityType = SimilarityType.LMDirichlet;
                    validSimilarity = true;
                    break;
                default:
                    similarityType = SimilarityType.BM25;
                    validSimilarity = true;
                    break;
            }
            indexPath += (analyzerType + "-" + similarityType);

            System.out.println("Force update index? (y/n)");
            input = System.console().readLine();
            forceUpdate = input.equals("y") || input.equals("Y");

            indexExists = new File(indexPath).exists();
        }

        if (!indexExists || forceUpdate) {
            FileUtils.deleteDirectory(new File(indexPath));
            loadAndIndexDocs(analyzerType, similarityType, indexPath);
        } else {
            System.out.println("Index already exists. Force update index!");
        }
    }

    private static void loadAndIndexDocs(AnalyzerType analyzerType, SimilarityType similarityType, String indexPath) {
        try {
            FBISLoader fbisLoader = new FBISLoader();
            docs = fbisLoader.loadFBISDocs(Constants.DATASET_FILE_PATH + "fbis");
            indexer = new Indexer(indexPath);
            indexer.IndexDocs(docs, analyzerType, similarityType);
        } catch (IOException ex) {
            System.out.println("ERROR: IOException occurred while loading FBIS documents");
        }

        try {
            FR94Loader fr94Loader = new FR94Loader();
            docs = fr94Loader.loadFR94Docs(Constants.DATASET_FILE_PATH + "fr94");
            indexer = new Indexer(indexPath);
            indexer.IndexDocs(docs, analyzerType, similarityType);
        } catch (IOException ex) {
            System.out.println("ERROR: IOException occurred while loading FR94 documents");
        }

        try {
            FTLoader ftLoader = new FTLoader();
            docs = ftLoader.loadFTDocs(Constants.DATASET_FILE_PATH + "ft");
            indexer = new Indexer(indexPath);
            indexer.IndexDocs(docs, analyzerType, similarityType);
        } catch (IOException ex) {
            System.out.println("ERROR: IOException occurred while loading FT documents");
        }

        try {
            LATimesLoader laTimesLoader = new LATimesLoader();
            docs = laTimesLoader.loadLaTimesDocs(Constants.DATASET_FILE_PATH + "latimes");
            indexer = new Indexer(indexPath);
            indexer.IndexDocs(docs, analyzerType, similarityType);
        } catch (IOException ex) {
            System.out.println("ERROR: IOException occurred while loading LATimes documents");
        }
    }
}