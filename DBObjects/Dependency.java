package mongoDB.DBObjects;

/**
 * Created by talmosko on 4/2/15.
 */

import com.google.gson.Gson;
import mongoDB.Consts;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
@Entity (Consts.dependenciesCollectionName)
public class Dependency {
    @Id
    private ObjectId id;

    //the ID of the document of the related sentence
    private ObjectId sentenceID;

    //governor word
    private String gov;

    //governor word index in the sentence
    private int govIndex;

    //dependent word
    private String dep;

    //dependent word index in the sentence
    private int depIndex;

    //dependency relation tag
    private String reln;

    public Dependency(ObjectId sentenceID, String gov, int govIndex, String dep, int depIndex, String reln)
    {
        this.sentenceID = sentenceID;
        this.gov = gov;
        this.govIndex = govIndex;
        this.dep = dep;
        this.depIndex = depIndex;
        this.reln = reln;
    }


    public String getGov() {
        return gov;
    }

    public int getGovIndex() {
        return govIndex;
    }

    public String getDep() {
        return dep;
    }

    public int getDepIndex() {
        return depIndex;
    }

    public String getReln() {
        return reln;
    }

    public ObjectId getSentenceID() {
        return sentenceID;
    }

    public ObjectId getId() {
        return id;
    }

    public String toJSON()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
