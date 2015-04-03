package mongoDB.Builders;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import mongoDB.DBObjects.Dependency;
import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * Created by talmosko on 4/3/15.
 */
public class DependencyBuilder {

    /**
     * create for all dependency tag a object, and add him to the list
     * @param sentenceID - the "_id" field of the related sentence document
     * @param tds
     * @return
     */
    public static ArrayList<Dependency> addAllDependencies(ObjectId sentenceID, SemanticGraph tds) {
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        for(SemanticGraphEdge td: tds.getEdgeSet())
        {
            String reln = td.getRelation().getShortName();
            String govString = td.getGovernor().word();
            int govIndex = td.getGovernor().index();
            String depString= td.getDependent().word();
            int depIndex = td.getDependent().index();
            dependencies.add(new Dependency(sentenceID, govString, govIndex, depString, depIndex, reln));
        }
        return dependencies;
    }
}
