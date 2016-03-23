import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.apps.TurkishSentenceParser;
import zemberek.morphology.parser.MorphParse;
import zemberek.morphology.parser.SentenceMorphParse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Master on 21.03.2016.
 */
public class Parser {

    TurkishSentenceParser sentenceParser;
    TurkishMorphParser morphParser;
    Z3MarkovModelDisambiguator disambiguator;
    List StopWords = Arrays.asList(
            " ",
            ",",
            "#",
            "...",
            ";"
    );
    public Parser() throws IOException {
        this.morphParser= TurkishMorphParser.createWithDefaults();
        this.disambiguator = new Z3MarkovModelDisambiguator();

        this.sentenceParser = new TurkishSentenceParser(
                morphParser,
                disambiguator
        );
    }

    void parseAndDisambiguate(String sentence) {
        System.out.println("Sentence  = " + sentence);
        SentenceMorphParse sentenceParse = sentenceParser.parse(sentence);


        System.out.println("Before disambiguation.");
        writeParseResult(sentenceParse);

        System.out.println("\nAfter disambiguation.");
        sentenceParser.disambiguate(sentenceParse);

        writeParseResult(sentenceParse);

    }

    private void writeParseResult(SentenceMorphParse sentenceParse) {
        for (SentenceMorphParse.Entry entry : sentenceParse) {
            System.out.println("Word = " + entry.input);
            for (MorphParse parse : entry.parses) {
                System.out.println(parse.formatLong());
            }
        }
    }

    public List<String> getParsed(String sentence) throws IOException {

        List<MorphParse> res = sentenceParser.bestParse(sentence);
        List<String> ret = new ArrayList<>();
        for(MorphParse w : res){
            String word = w.root.trim();
            if(!StopWords.contains(word) || !word.matches("\\p{Punct}")){
                ret.add(word);
            }
        }
        return ret;

    }
}
