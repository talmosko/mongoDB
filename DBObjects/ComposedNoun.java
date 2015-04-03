package mongoDB.DBObjects;

import com.google.gson.Gson;
import mongoDB.Consts;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;

/**
 * Created by talmosko on 4/3/15.
 */
@Entity (Consts.nounsCollectionName)
public class ComposedNoun {
    /*
    NOTE: composedNoun has actually 2 types:
    1. one word noun, that has no dependency to other noun in the sentence, startIndex = endIndex
    2. actually composed (=more than one word) noun, endIndex > startIndex
     */
    @Id
    private ObjectId id;

    //the ID of the document of the related sentence
    private ObjectId sentenceID;

    //the index of the first noun in the composed noun
    private int startIndex;

    //the index of the last noun in the composed noun
    private int endIndex;

    //the related noun himself
    private String word;

    //a list of the adjectives that have a dependency with this noun
    public ArrayList<String> relatedAdjectives;

    public ComposedNoun (ObjectId sentenceID, int startIndex, int endIndex,String word, ArrayList<String> relatedAdjectives)
    {
        this.sentenceID = sentenceID;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.word = word;
        this.relatedAdjectives = relatedAdjectives;
    }

    public ComposedNoun (ObjectId sentenceID, int startIndex, int endIndex, String word)
    {
        this.sentenceID = sentenceID;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.word = word;
        this.relatedAdjectives = new ArrayList<String>();
    }


    public ObjectId getId() {
        return id;
    }

    public ObjectId getSentenceID() {
        return sentenceID;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getWord() {
        return word;
    }

    public String toJSON()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
