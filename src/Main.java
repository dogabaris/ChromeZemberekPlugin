import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static spark.Spark.*;

import com.google.common.collect.Lists;
import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.KelimeTipi;
import net.zemberek.yapi.Kok;
import zemberek.morphology.apps.TurkishMorphParser;

/**
 * Created by Master on 21.03.2016.
 */
public class Main  {


    public static void main(String[] args) throws IOException, SQLException ,ClassNotFoundException {
        port(666);

        Zemberek z = new Zemberek(new TurkiyeTurkcesi());
        List<Kok> KokList = Lists.newArrayList();

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

    public static java.sql.Connection baglantiyiSagla() throws Exception {

        Class.forName("com.mysql.jdbc.Driver");
        java.sql.Connection baglanti=null;
        baglanti=DriverManager.getConnection("jdbc:mysql://localhost/googleplugin?useUnicode=true&characterEncoding=utf-8","root","");
        return baglanti;

    }

    public static void FrekansHesapla(List<Kok> kokListe) throws Exception {
        Map<String,Integer> koklervefrekansları = new HashMap<String, Integer>();
        List<Map.Entry<String,Integer>> siralanmiskoklervefrekansları;

        for(Kok i : kokListe){
            if(koklervefrekansları.containsKey(i.icerik())){
                koklervefrekansları.put(i.icerik(),koklervefrekansları.get(i.icerik())+1);
            }else{
                koklervefrekansları.put(i.icerik(),1);
            }
        }
        System.out.println(koklervefrekansları);
        System.out.println(frekansSirala(koklervefrekansları));
        siralanmiskoklervefrekansları = frekansSirala(koklervefrekansları);

        java.sql.Connection baglanti=null;
        baglanti = baglantiyiSagla();

        for(Map.Entry<String, Integer> satir : siralanmiskoklervefrekansları){
            //INSERT INTO table (id, name, age) VALUES(1, "A", 19) ON DUPLICATE KEY UPDATE name="A", age=19
            String query = "insert into frekanslar(kelime,frekans) values('"+ satir.getKey() +"','" + satir.getValue() + "') ON DUPLICATE KEY UPDATE frekans=(frekans+" + satir.getValue() + ")";
            java.sql.Statement stm = baglanti.createStatement();
            int executeUpdate = stm.executeUpdate(query);
        }

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
