package mongoDB;

/**
 * Created by talmosko on 3/31/15.
 */

import mongoDB.DBObjects.ComposedNoun;
import org.bson.types.ObjectId;
import com.google.gson.*;
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
        ComposedNoun noun = new ComposedNoun(new ObjectId(),1,1,"dsadsa");
        String json = new Gson().toJson(noun);
        System.out.println(json);
        noun = (ComposedNoun) new Gson().fromJson(json, ComposedNoun.class);

    }
}

