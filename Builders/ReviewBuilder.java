package mongoDB.Builders;

import com.mongodb.BasicDBObject;

import java.util.ArrayList;

/**
 * Created by talmosko on 4/3/15.
 */
public class ReviewBuilder {

    /**
     * creates a document for review
     * @param source
     * @param dataset
     * @param info - list of the details about the review
     * @return
     */
    public static BasicDBObject createDoc(String source, String dataset, ArrayList<String> info) {
        //TODO: hash func
        BasicDBObject doc = new BasicDBObject();
        doc.append("source", source);
        doc.append("dataset", dataset);
        doc.append("userName", info.get(0));
        doc.append("city", info.get(1));
        doc.append("generalRating", info.get(2));
        doc.append("reviewId", info.get(3));
        doc.append("hotelName", info.get(4));
        doc.append("specialRatings", info.get(5));
        doc.append("userLocation", info.get(6));
        doc.append("state", info.get(7));
        doc.append("reviewContent", info.get(8).replaceAll("\"", "\\\\\""));
        doc.append("date", info.get(9));
        doc.append("reviewTitle", info.get(10));
        return doc;
    }
}
