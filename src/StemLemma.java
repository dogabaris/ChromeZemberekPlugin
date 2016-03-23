import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.parser.MorphParse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master on 21.03.2016.
 */
public class StemLemma {//kök hesap her kelime için dönüyor

    TurkishMorphParser parser;

    public StemLemma(TurkishMorphParser parser) {
        this.parser = parser;
    }

    public List<String> parse(String word) {
        List<String> ret = new ArrayList<>();
        List<MorphParse> parses = parser.parse(word);

        List<String> tmp = new ArrayList<>();
        for (MorphParse parse : parses) {
            tmp = parse.getStems();
        //    ret = ListUtil.union(tmp, ret);
        }
        return ret;
    }
}
