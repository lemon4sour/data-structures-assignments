package Search;

import HashTablePackage.HashTable;
import HashTablePackage.NullValueException;
import HashTablePackage.WordSearchHashTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static WordSearchHashTable wordTable;

    public static void main(String[] args) throws NullValueException, InvalidSearchException, FileNotFoundException {
        wordTable = new WordSearchHashTable(2477, 0.8F, HashTablePackage.WordSearchHashTable.HashFunction.PAF, HashTable.CollisionHandling.DoubleHash);
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 100; i++) { //iterate through 100 files
            String fileName;
            if (i < 10)
                fileName = "sport/00"+i+".txt";
            else if (i < 100)
                fileName = "sport/0"+i+".txt";
            else
                fileName = "sport/"+i+".txt";

            System.out.print(fileName);

            TextFileData textFileData = new TextFileData(fileName,FileReader.takeData(fileName));

            for (WordInfo wordInfo : textFileData.getDataOfWords())
                wordTable.addWord(wordInfo.word, new WordSearchHashTable.FileStat(fileName, wordInfo.count));

            System.out.println(" Done.");

        }

        System.out.println("Indexing took "+(System.currentTimeMillis()-startTime)+" ms");

        Scanner scanner = new Scanner(new File("search.txt"));
        String[] searchList = new String[1000];
        for (int i = 0; i < 1000; i++) {
            searchList[i] = scanner.next();
        }
        wordTable.printSearchStats(searchList);

        System.out.println("CC: "+ wordTable.getCollisionCount());

        String search = "indoor world car";
        System.out.println("Searching for: "+search);

        String relevantFile = Search(search);
        System.out.println("Most Relevant File: "+relevantFile);

    }

    public static String Search(String searchInput) throws InvalidSearchException {
        String[] input = searchInput.split(" ");
        if (input.length == 3) {

            ArrayList<Result> totalResults = new ArrayList();
            int keywordIndex = 0;
            for (String keyword : input) {
                ArrayList<WordSearchHashTable.FileStat> wordSearchResult = wordTable.getValue(keyword);
                if (wordSearchResult == null)
                    System.out.println("No file with the word '"+keyword+"'.");
                else {
                    for (WordSearchHashTable.FileStat fileStat : wordSearchResult) {
                        boolean resultExists = false;
                        for (Result result : totalResults) {
                            if (result.fileName.equals(fileStat.getFileName())){
                                result.count[keywordIndex] = fileStat.getCount();
                                resultExists = true;
                            }
                        }
                        if (!resultExists) {
                            Result newResult = new Result();
                            newResult.fileName = fileStat.getFileName();
                            newResult.count[keywordIndex] = fileStat.getCount();
                            totalResults.add(newResult);
                        }
                    }
                }
                keywordIndex++;
            }

            int highestSum = 0; //for relevancy comparison
            for (Result result : totalResults) {
                int sum = 0;
                for (int i : result.count) sum+=i;
                if (sum > highestSum) highestSum = sum;
            }

            //relevancy comparison
            double[] scores = new double[totalResults.size()];
            int i = 0;
            for (Result result : totalResults) {
                double sum = (result.count[0]+result.count[1]+result.count[2]);
                double mean = sum/3;
                double deviationSum = Math.abs(result.count[0]-mean)+Math.abs(result.count[1]-mean)+Math.abs(result.count[2]-mean);
                scores[i] = Math.pow((highestSum - sum),2) + Math.pow(deviationSum,2);
                i++;
            }
            int lowestScoreIndex = 0; //return lowest score
            double lowestScore = scores[0];
            for (int j = 1; j < totalResults.size(); j++) {
                if (scores[j] < lowestScore) {
                    lowestScore = scores[j];
                    lowestScoreIndex = j;
                }
            }

            Result result = totalResults.get(lowestScoreIndex);

            return result.fileName;
        }
        else {
            throw new InvalidSearchException("Search cannot have less or more than 3 keywords");
        }
    }
}
