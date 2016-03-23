import java.io.IOException;
import java.util.*;

import static spark.Spark.*;

import com.google.common.collect.Interner;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.KelimeTipi;
import net.zemberek.yapi.Kok;
import zemberek.morphology.apps.TurkishMorphParser;

/**
 * Created by Master on 21.03.2016.
 */
public class Main  {
    public static void main(String[] args) throws IOException {
        port(666);

        Zemberek z = new Zemberek(new TurkiyeTurkcesi());
        List<Kok> KokList = Lists.newArrayList();
        //List<List<String>> KokveFrekans = new ArrayList<List<String>>();
        //Kelime[] cozumler = z.kelimeCozumle("uçak");
        //System.out.println(cozumler[0].kok());
        //System.out.println(kokBulucu.kokBul("gözlükçü"));

        //z.kokBulucu().kokBul();


        post("/MetinAl", (req, res) -> {
            Map<String, String> map = JsonUtil.parse(req.body());
            String textBody = map.get("text");
            TurkishMorphParser parser = TurkishMorphParser.createWithDefaults();
            Tokenizator tk = new Tokenizator(textBody);
            List<String> list = tk.getWords();
            //StemLemma st = new StemLemma(parser);

            for(String l: list){
                //Kelime[] cozumler = z.kelimeCozumle(l);
                Kok[] kokler = z.kokBulucu().kokBul(l);
                for(Kok k : kokler){
                    if(k.tip().equals(KelimeTipi.ISIM)){
                        System.out.println(k.icerik());
                        KokList.add(k);
                    }
                }
            }
            FrekansHesapla(KokList);

            //System.out.println(koklistesi.toString());

            return "Başarılı!";
        });
    }

    public static void FrekansHesapla(List<Kok> kokListe){
        Map<String,Integer> koklervefrekansları = new HashMap<String, Integer>();

        for(Kok i : kokListe){
            if(koklervefrekansları.containsKey(i.icerik())){
                koklervefrekansları.put(i.icerik(),koklervefrekansları.get(i.icerik())+1);
            }else{
                koklervefrekansları.put(i.icerik(),1);
            }
        }
        System.out.println(koklervefrekansları);
        System.out.println(frekansSirala(koklervefrekansları));
    }

    public static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> frekansSirala(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K,V>>() {
                    @Override
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }
}