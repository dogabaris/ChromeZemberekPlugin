import org.antlr.v4.runtime.Token;
import zemberek.tokenizer.ZemberekLexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Master on 21.03.2016.
 */
public class Tokenizator {// isimse al
    ZemberekLexer lexer;
    List<String> ret = new ArrayList<>();

    Tokenizator(String input) {
        this.lexer = new ZemberekLexer();//değiştirilebilir
        System.out.println("Input = " + input);
        Iterator<Token> tokenIterator = lexer.getTokenIterator(input);
        while (tokenIterator.hasNext()) {
            Token token = tokenIterator.next();
            if (token.getType() == 7 || token.getType() == 8) {
                ret.add(token.getText().toLowerCase().replaceAll("'",""));
            }
        }
    }

    List<String> getWords() {
        return this.ret;
    }
}
