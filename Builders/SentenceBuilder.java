package mongoDB.Builders;

/**
 * Created by talmosko on 3/30/15.
 */
import com.mongodb.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;


public class SentenceBuilder {

    /**
     * parsing a given sentence, creating a document for the sentence
     * and add it to the database
     * @param parsed - the stanford parser annotations for this sentence
     * @param sentence_order - the place of the sentence at the review
     * @param review_id
     */
    public static BasicDBObject createDoc(CoreMap parsed, int sentence_order, ObjectId review_id)
    {
        //list of all the words in the sentence
        //NOTE: always the first word is "ROOT", the actual
        //sentence words start for index no. 1
        ArrayList<String> words = createWordsArray(parsed);

        //hash for the pos tags
        //key: word, value: pos tag
        HashMap<String,String> posTags = createPosTags(parsed);

        //document for the lemma tags
        //key: word, value: lemma tag
        HashMap<String,String> lemmaTags = createLemmaTags(parsed);

        //document for the ner tags
        //key: word index in the sentence, value: ner tag
        HashMap<Integer,String> nerTags = createNerTags(parsed);


        Tree tree = parsed.get(TreeCoreAnnotations.TreeAnnotation.class);

        //add all the dependencies
        //SemanticGraph tds = parsed.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

        //list of all the dependencies in the sentence
        //ArrayList<BasicDBObject> dependencies = addAllDependencies(tds);

        //list of all the nouns in the sentence (including composed nouns)
        //HashMap<Integer, BasicDBObject> nouns = addNouns(words, posTags, tds);

        /**
        starting create the document
         */
        BasicDBObject document = new BasicDBObject("review_id", review_id);

        //add the sentence text to the document
        document.append("sentence",parsed.get(CoreAnnotations.TextAnnotation.class));

        //add the sentence order in the review
        document.append("sentence_order", sentence_order);

        //add the tree
        document.append("tree", tree.toString());

        //add the words array
        document.append("words", words);

        //add the pos tags
        BasicDBObject posDoc = new BasicDBObject();
        for (String word : posTags.keySet())
        {
            posDoc.append(word, posTags.get(word));
        }
        document.append("pos", posDoc);

        //add the lemma tags
        BasicDBObject lemmaDoc = new BasicDBObject();
        for (String word : lemmaTags.keySet())
        {
            lemmaDoc.append(word, lemmaTags.get(word));
        }
        document.append("lemma", lemmaDoc);

        //add the ner tags
        BasicDBObject nerDoc = new BasicDBObject();
        for (Integer index : nerTags.keySet())
        {
            nerDoc.append(""+ index, nerTags.get(index));
        }
        document.append("ner", nerDoc);

        return document;

    }



    /**
     * add the ner tag for every word in the sentence that has a ner tag
     * @param parsed
     * @return
     */
    private static HashMap<Integer, String> createNerTags(CoreMap parsed) {
        HashMap<Integer,String> nerTags = new HashMap<Integer, String>();
        for (CoreLabel token : parsed.get(CoreAnnotations.TokensAnnotation.class)) {
            Integer index = token.get(CoreAnnotations.IndexAnnotation.class);
            String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            if(!ner.equals("O")) //if there is ner tag for the word
                nerTags.put(index, ner);
        }
        return nerTags;
    }


    /**
     * add the lemma tag for every word in the sentence
     * @param parsed
     * @return
     */
    private static HashMap<String, String> createLemmaTags(CoreMap parsed) {
        HashMap<String, String> lemmaTags = new HashMap<String, String>();
        for (CoreLabel token : parsed.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            lemmaTags.put(word, lemma);
        }
        return lemmaTags;
    }

    /**
     * add all the words in the sentence to the words array
     * @param parsed
     * @return
     */
    private static ArrayList<String> createWordsArray(CoreMap parsed) {
        ArrayList<String> words = new ArrayList<String>();
        words.add("ROOT"); // root is always the first word
        for (CoreLabel token : parsed.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            words.add(word);
        }
        return words;
    }

    /**
     * add the pos tag for every word in the sentence
     * @param parsed
     * @return
     */
    private static HashMap<String, String> createPosTags(CoreMap parsed) {
        HashMap<String, String> posTags = new HashMap<String, String>();
        for (CoreLabel token : parsed.get(CoreAnnotations.TokensAnnotation.class)) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            posTags.put(word, pos);
        }
        return posTags;
    }

}