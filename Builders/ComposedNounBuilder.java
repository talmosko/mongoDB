package mongoDB.Builders;

import com.mongodb.BasicDBObject;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import mongoDB.DBObjects.ComposedNoun;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by talmosko on 4/3/15.
 */
public class ComposedNounBuilder {
    /**
     * find all composed nouns, or one-word nouns, in the sentence
     * @param sentenceDoc - the document of the related sentence
     * @param tds
     * @return hashMap of all nouns founded. key: startIndex, value: ComposedNoun
     */
    public static HashMap<Integer,ComposedNoun> addNouns(BasicDBObject sentenceDoc, SemanticGraph tds) {
        HashMap<Integer,ComposedNoun> nouns = new HashMap<Integer, ComposedNoun>();
        //ObjectId sentenceID, ArrayList<String> words, HashMap<String, String> posTags
        ObjectId sentenceID = (ObjectId) sentenceDoc.get("_id");
        ArrayList<String> words = (ArrayList<String>) sentenceDoc.get("words");
        BasicDBObject posDoc = (BasicDBObject) sentenceDoc.get("pos");
        for(String word : words)
        {
            String posTag = (String) posDoc.get(word);
            if(posTag!=null && posTag.startsWith("NN"))
            {
                int start_index = words.indexOf(word);
                int end_index = start_index;
                String noun = "";
                ArrayList<String> relatedJJs = checkForRelatedJJs(word, posDoc, tds);
                for(SemanticGraphEdge td: tds.getEdgeSet())
                {
                    if(td.getRelation().getShortName().equals("nn") && td.getGovernor().word().equals(word))
                    {//if this word is a part of a composed noun
                        if(start_index == end_index) {
                            //start index equals end index only if its one-word noun.
                            //well, by the dependency we go that this word is a part of composed noun
                            start_index = td.getDependent().index();
                        }
                        //add this noun part to create complete composed noun
                        noun = noun + td.getDependent().word() + " ";

                        //checks for related JJs for this part
                        relatedJJs.addAll(checkForRelatedJJs(td.getDependent().word(), posDoc, tds));

                        if(nouns.containsKey(td.getDependent().index()))
                        {//if this part of a composed noun is in the hashmap- we need to delete him because
                         //we save only composed nouns (or one-word nouns)
                            nouns.remove(td.getDependent().index());
                        }
                    }
                }
                noun = noun + word;
                ComposedNoun composedNoun = new ComposedNoun(sentenceID, start_index, end_index, noun, relatedJJs);
                nouns.put(start_index, composedNoun);
            }
        }
        return nouns;
    }

    /**
     * checks whether there is adjectives that have a dependency with a given noun
     * NOTE: every one-word noun or a part of composed noun is checked by that function
     * @param noun
     * @param posDoc
     * @param tds
     * @return
     */
    private static ArrayList<String> checkForRelatedJJs(String noun, BasicDBObject posDoc, SemanticGraph tds)
    {
        ArrayList<String> relatedJJs = new ArrayList<String>();
        for(SemanticGraphEdge td: tds.getEdgeSet())
        {
            if(td.getGovernor().word().equals(noun))
            {
                String dep = td.getDependent().word();
                String depPOS = (String) posDoc.get(dep);
                if(depPOS.startsWith("JJ"))
                {//if the dependent is adjective, we found related adjective
                    relatedJJs.add(dep);
                }
            }
            else if(td.getDependent().word().equals(noun))
            {
                String gov = td.getDependent().word();
                String govPOS = (String) posDoc.get(gov);
                if(govPOS.startsWith("JJ"))
                {//if the governor is adjective, we found related adjective
                    relatedJJs.add(gov);
                }
            }
        }
        return relatedJJs;

    }

}
