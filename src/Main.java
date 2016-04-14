import java.io.IOException;
import java.sql.*;
import java.util.*;

import static spark.Spark.*;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.Kelime;
import net.zemberek.yapi.KelimeTipi;
import net.zemberek.yapi.Kok;
import net.zemberek.yapi.ek.Ek;

/**
 * Created by Master on 21.03.2016.
 */
public class Main  {
    public static void main(String[] args) throws IOException, SQLException ,ClassNotFoundException {
        port(666);

        List<Map.Entry<String, Integer>> SiralanmisKelimeler = Lists.newArrayList();
        Zemberek z = new Zemberek(new TurkiyeTurkcesi());


        post("/MetinAl", (req, res) -> {
            List<Kok> KokList = Lists.newArrayList();
            List<String> kelimeList = Lists.newArrayList();
            List<Kok> objectList = Lists.newArrayList();
            Map<String, String> map = JsonUtil.parse(req.body());
            String textBody = map.get("text");
            Tokenizator tk = new Tokenizator(textBody);
            List<String> list = tk.getWords();
            int kokiterator = 0;

            for(String l: list){
                //Kelime[] cozumler = z.kelimeCozumle(l);
                Kok[] kokler = z.kokBulucu().kokBul(l);
                Kelime[] cozumler  = z.kelimeCozumle(l);
                //System.out.println("*** " + Arrays.toString(cozumler));

                for(Kok k : kokler){
                    if(k.tip().equals(KelimeTipi.ISIM)){
                        //System.out.println(k.icerik());
                        KokList.add(k);
                        //kelimeList.add(k.icerik());
                        kelimeList.add("");
                        for(Kelime s : cozumler){
                                kelimeList.set(kokiterator,z.kelimeUret(KokList.get(kokiterator),s.ekler()));
                                //Kok cevrim = (Kok) kelimeList.get(kokiterator);

                        }
                        kokiterator++;
                    }
                }

                kokler = null;
            }
            System.out.println(kelimeList);
            //KokList.add(0,(Kok) kelimeList.get(0));
            //FrekansHesapla(KokList);
            //kelimeList.addAll(KokList)
            //KokList.add(0, kelimeList.get(0));

            SiralanmisKelimeler.addAll(FrekansHesapla(kelimeList));
            KokList.clear();
            //System.out.println(koklistesi.toString());

            return "Başarılı!";
        });

        get("/listeYenile",(request, response) -> {
            ArrayList<ArrayList<String>> yuksekFrekanslilar = Lists.newArrayList();
            Gson gson = new Gson();

            for(int i=0;i<5;i++){
                try{
                    yuksekFrekanslilar.add(i, new ArrayList<String>());
                    yuksekFrekanslilar.get(i).add(0,"");
                    yuksekFrekanslilar.get(i).add(1,"");
                }catch (IndexOutOfBoundsException e){

                }

            }

            for(int i=0;i<5;i++){
                try {
                    yuksekFrekanslilar.get(i).set(0, SiralanmisKelimeler.get(i).getKey());
                    yuksekFrekanslilar.get(i).set(1, String.valueOf(SiralanmisKelimeler.get(i).getValue()));
                }
                catch(IndexOutOfBoundsException e){

                }
            }

            String post = gson.toJson(yuksekFrekanslilar);
            yuksekFrekanslilar.clear();
            SiralanmisKelimeler.clear();

            return post;
        });
        get("/veritabanilisteYenile",(request, response) -> {
            ArrayList<ArrayList<String>> yuksekFrekanslilar = Lists.newArrayList();
            java.sql.Connection baglanti = baglantiyiSagla();
            java.sql.Statement stm;
            Gson gson = new Gson();
            int iterator=0;

            String query = "SELECT * FROM frekanslar ORDER BY frekans DESC LIMIT 5";
            stm = baglanti.createStatement();
            ResultSet frekans = stm.executeQuery(query);

            while (frekans.next()) {
                String word = frekans.getString("kelime");
                int freq = frekans.getInt("frekans");

                yuksekFrekanslilar.add(iterator, new ArrayList<String>());
                yuksekFrekanslilar.get(iterator).add(0, word);
                yuksekFrekanslilar.get(iterator).add(1, String.valueOf(freq));
                iterator++;
            }

            String post = gson.toJson(yuksekFrekanslilar);
            yuksekFrekanslilar.clear();
            SiralanmisKelimeler.clear();

            return post;
        });

    }

    public static java.sql.Connection baglantiyiSagla() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        java.sql.Connection baglanti=null;
        baglanti=DriverManager.getConnection("jdbc:mysql://localhost/googleplugin?useUnicode=true&characterEncoding=utf-8","root","");
        //baglanti.setAutoCommit(false);
        return baglanti;

    }

    public static List<Map.Entry<String, Integer>> FrekansHesapla(List<String> kokListe) throws Exception {
        Map<String,Integer> koklervefrekansları = new HashMap<String, Integer>();
        List<Map.Entry<String,Integer>> siralanmiskoklervefrekansları;

        for(String i : kokListe){
            if(koklervefrekansları.containsKey(i)){
                koklervefrekansları.put(i,koklervefrekansları.get(i)+1);
            }else{
                koklervefrekansları.put(i,1);
            }
        }
        System.out.println(koklervefrekansları);
        System.out.println(frekansSirala(koklervefrekansları));
        siralanmiskoklervefrekansları = frekansSirala(koklervefrekansları);

        java.sql.Connection baglanti=null;
        baglanti = baglantiyiSagla();
        java.sql.Statement stm;

        stm = baglanti.createStatement();
        for(Map.Entry<String, Integer> satir : siralanmiskoklervefrekansları){
            //INSERT INTO table (id, name, age) VALUES(1, "A", 19) ON DUPLICATE KEY UPDATE name="A", age=19
            String query = "insert into frekanslar(kelime,frekans) values('"+ satir.getKey() +"','" + satir.getValue() + "') ON DUPLICATE KEY UPDATE frekans=(frekans+" + satir.getValue() + ")";

            int executeUpdate = stm.executeUpdate(query);
        }
        stm.close();
        baglanti.close();
        return siralanmiskoklervefrekansları;
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
