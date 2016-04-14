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
import net.zemberek.yapi.ek.EkKuralBilgisi;
import net.zemberek.yapi.ek.EkOzelDurumTipi;

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
            Map<String, String> map = JsonUtil.parse(req.body());
            String textBody = map.get("text");
            Tokener tk = new Tokener(textBody);
            List<String> list = tk.getWords();
            int kokiterator = 0;
            int gereksizkokiteratoru=0;

            for(String l: list){
                //Kelime[] cozumler = z.kelimeCozumle(l);
                Kok[] kokler = z.kokBulucu().kokBul(l);
                Kelime[] cozumler  = z.kelimeCozumle(l);

                /*for(Kelime c : cozumler){
                    if ((Arrays.toString(cozumler).contains("ISIM_DONUSUM_LES")
                            || (Arrays.toString(cozumler).contains("ISIM_BULUNMA_LI")) ||
                            (Arrays.toString(cozumler).contains("ISIM_ILGI_CI")) ||
                            (Arrays.toString(cozumler).contains("ISIM_YOKLUK_SIZ")))){
                            cozumler[gereksizkokiteratoru];
                        gereksizkokiteratoru++;
                    }
                }*/

                for(Kok k : kokler){
                    if(k.tip().equals(KelimeTipi.ISIM) ){
                        KokList.add(k);
                        kelimeList.add("");
                        for(Kelime s : cozumler){
                                List<net.zemberek.yapi.ek.Ek> yeni_ekler = s.ekler();
                                if ((s.ekVarmi("ISIM_DONUSUM_LES")
                                        || s.ekVarmi("ISIM_BULUNMA_LI") ||
                                        s.ekVarmi("ISIM_ILGI_CI") ||
                                        s.ekVarmi("ISIM_YOKLUK_SIZ")|| s.ekVarmi("ISIM_YOKLUK_SIZ"))){
                                        yeni_ekler.remove(gereksizkokiteratoru);
                                        gereksizkokiteratoru++;
                                }
                                else
                                {
                                    kelimeList.set(kokiterator,z.kelimeUret(KokList.get(kokiterator),yeni_ekler));

                                    gereksizkokiteratoru=0;
                                }

                        }
                        kokiterator++;
                    }
                }

                kokler = null;
            }

            System.out.println("Kök listesi:");
            System.out.println(KokList);
            System.out.println("Kelime listesi:");
            SiralanmisKelimeler.addAll(FrekansHesapla(kelimeList));
            KokList.clear();

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
