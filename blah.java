package mongoDB;

/**
 * Created by talmosko on 3/31/15.
 */

import com.mongodb.BasicDBObject;
import mongoDB.DBObjects.ComposedNoun;
import com.mongodb.util.*;

import utils.FileIterator;
import utils.FileUtils;
import utils.StaticJSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class blah {

    /**
     * Java MongoDB : Convert JSON data to DBObject
     */


    public static void main(String[] args) {

//        MongoClient mongoClient = new MongoClient("hadoopb.ise.bgu.ac.il");
//        DB db = mongoClient.getDB("mydb");
//        DBCollection sentencesCollection = db.getCollection("wang_sentences");
//        //sentencesCollection.createIndex(new BasicDBObject("words", 1).append("pos", 1).append("lemma", 1).append("ner", 1).append("dependencies", 1).append("nouns", 1));
//        BaseStanfordUtils stanfordNLP = new BaseStanfordUtils();
//
//        SentenceDocumentBuilder docb = new SentenceDocumentBuilder();
//        List<CoreMap> coremaps = stanfordNLP.getSentencesAsCoreMapList("Empire State Museum");
//        BasicDBObject doc = docb.createDoc(coremaps.get(0), 3, "rev");
//        System.out.println(doc.get("_id"));
//        System.out.println(sentencesCollection.insert(doc).getUpsertedId());
//        System.out.println(doc.get("_id"));
        Iterator<String> it = null;
        ArrayList<String> jsons = new ArrayList<String>();

        try {
            it = FileUtils.getFileIterator("wang.json");
            while(it.hasNext())
            {
                String str = it.next();
                if(!str.endsWith("}"))
                    str = str.substring(0,str.length()-1);
                if(str.startsWith("["))
                    str = str.substring(1);
                Scanner scan = new Scanner(str);
                scan.useDelimiter(", \"reviewContent\": |, \"date\": ");
                String prologue = scan.next()+ ", \"reviewContent\": ";
                String content = scan.next();
                content = "\"" +content.substring(0, content.length() - 1).replaceAll("\"", "\\\\\"") + "\"";
                String epilogue = ", \"date\": " + scan.next();
                str = prologue + content + epilogue;
                jsons.add(str);
                scan.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            for(String str : jsons)
            {
                BasicDBObject obj = (BasicDBObject) JSON.parse(str);
                System.out.println("after parse");
                System.out.println(obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

