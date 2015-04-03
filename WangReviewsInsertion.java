package mongoDB;

import com.mongodb.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import mongoDB.DBObjects.ComposedNoun;
import mongoDB.DBObjects.Dependency;
import mongoDB.Builders.ComposedNounBuilder;
import mongoDB.Builders.DependencyBuilder;
import mongoDB.Builders.ReviewBuilder;
import mongoDB.Builders.SentenceBuilder;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import stanfordUtils.BaseStanfordUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by talmosko on 4/1/15.
 */
public class WangReviewsInsertion {
    public static BaseStanfordUtils stanfordUtils ;
    public static MongoClient mongoClient;
    public static DB db;
    public static DBCollection reviewsCollection;
    public static DBCollection sentencesCollection;
    public static DBCollection dependenciesCollection;
    public static DBCollection nounsCollection;


    public static void main (String[] args) throws IOException {
        mongoClient = new MongoClient();
        db = mongoClient.getDB(Consts.DBName);
        reviewsCollection = db.getCollection(Consts.reviewsCollectionName);
        sentencesCollection = db.getCollection(Consts.sentencesCollectionName);
        dependenciesCollection = db.getCollection(Consts.dependenciesCollectionName);
        nounsCollection = db.getCollection(Consts.nounsCollectionName);
        stanfordUtils = new BaseStanfordUtils();
        parseWangJSON();

   }

    /**
     * get all the reviews from the wang json,
     * creates a document from them, and add them to the DB
     */
    private static void parseWangJSON() {
        Scanner scan = null;
        try {
            scan = new Scanner(new FileReader("wang.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scan.useDelimiter("\\[\\{\"userName\"\\: |\\{\"userName\"\\: |, \"city\": |, \"generalRating\": |, \"reviewId\": |, \"hotelName\": |, \"specialRatings\": |, \"userLocation\": |, \"state\": |, \"reviewContent\": |, \"date\": |, \"reviewTitle\": |\"\\}\\,|\"\\}\\]");
        int i = 0;
        ArrayList<String> info = new ArrayList<String>();
        while(scan.hasNext()) {
            String next = scan.next();
            System.out.println(next);
            if(!next.equals("\n") && !next.equals(""))
            {
               if (i != 10)
               {// when i = 10 this is the last data field for this review
                   //some fixes for " inside the data
                   System.out.println(i);
                   String toAdd =next.substring(1, next.length() - 1);
                   info.add(toAdd);
                   System.out.println(info);
               }
                else
                {// i = 10 -> this is the last data field for this review
                    System.out.println(i);
                    info.add(next.substring(1));
                    System.out.println(info);
                    //finished to get all data for this review, creates a document
                    BasicDBObject doc = ReviewBuilder.createDoc(Consts.tripAdvisorSource, Consts.wangDataset, info);

                    //TODO: hash func
                    //checking if this review is in the db
                    BasicDBObject query = new BasicDBObject("reviewContent", doc.get("reviewContent"));
                    DBCursor cursor = reviewsCollection.find(query);
                    if(cursor.hasNext())
                    {//the review is found in the DB
                        //TODO: log
                    }
                    else
                    {// the review is not in the DB
                        reviewsCollection.insert(doc);
                        System.out.println(doc.toString());
                        parseReview(doc);
                    }
                    cursor.close();
                    i = -1; //next review
                }
                System.out.println(i);
                i++;
            }
        }
    }

    /**
     * parsing a given review using Stanford Parser,
     * and for every sentence creates a document
     * add the sentence, nouns, dependencies to the DB.
     * @param reviewDoc
     */
    public static void parseReview(BasicDBObject reviewDoc)
    {
        ObjectId reviewId = (ObjectId) reviewDoc.get("_id");
        String content = (String) reviewDoc.get("reviewContent");
        List<CoreMap> coremaps = stanfordUtils.getSentencesAsCoreMapList(content);
        int i=0;
        for(CoreMap sentence : coremaps)
        {
            //get the document for the sentence
            BasicDBObject sentenceDoc = SentenceBuilder.createDoc(sentence, i, reviewId);
//            try {
//                FileWriter fw = new FileWriter("docs/reviews/"+reviewId+i+".json");
//                fw.write(document.toString());
//                fw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            sentencesCollection.insert(sentenceDoc);
            System.out.println(sentenceDoc.toString());
            ObjectId sentenceID = (ObjectId) sentenceDoc.get("_id");

            //this for dependencies and nouns
            SemanticGraph tds = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

            //creates dependencies
            ArrayList<Dependency> dependencies = DependencyBuilder.addAllDependencies(sentenceID, tds);
            for(Dependency d : dependencies)
            {//add all dependencies to the DB
                insertDependency(d);
            }

            //create nouns
             /*
                NOTE: composedNoun has actually 2 types:
                1. one word noun, that has no dependency to other noun in the sentence, startIndex = endIndex
                2. actually composed (=more than one word) noun, endIndex > startIndex
             */
            HashMap<Integer,ComposedNoun> nouns = ComposedNounBuilder.addNouns(sentenceDoc, tds);
            for (ComposedNoun n : nouns.values())
            {//add all nound to the db
                insertComposedNoun(n);
            }
            i++;
        }
    }

    /**
     * insert given composedNoun to the DB
     * @param noun
     */
    public static void insertComposedNoun(ComposedNoun noun)
    {
        Morphia morphia = new Morphia();
        morphia.map(ComposedNoun.class);
        morphia.createDatastore(mongoClient, Consts.DBName).save(noun);
        System.out.println(noun.toJSON());

    }

    public static void insertDependency (Dependency de)
    {
        Morphia morphia = new Morphia();
        morphia.map(Dependency.class);
        morphia.createDatastore(mongoClient, Consts.DBName).save(de);
        System.out.println(de.toJSON());

    }
}
