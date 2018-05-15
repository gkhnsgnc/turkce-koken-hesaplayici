package atolye15Kamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TurkceKokenHesaplayici {
    private final String[] kalinSesliler = {"a", "ı", "o", "u"};
    private final String[] inceSesliler = {"e", "i", "ö", "ü"};
    private final String[] duzSesliler = {"a", "e", "ı", "i"};
    private final String[] yuvarlakSesliler = {"o", "ö", "u", "ü"};
    private final String[] genisSesliler = {"a", "e", "o", "ö"};
    private final String[] darSesliler = {"ı", "i", "u", "ü"};
    private final String[] yabanci = {"x", "w", "j"};
    private final Locale lokal = new Locale("tr","TR");
    
    public void hesapla(String girdi) {
        if(girdi.contains(" ")) {
            String kelimeler[] = girdi.split(" ");
            for(String kelime: kelimeler) {
                int yuzde = kokenHesapla(kelime.toLowerCase(lokal));
                System.out.println("Girilen \"" + kelime + "\" kelimesi %" + yuzde + " ihtimalle Türkçe kökenli. (" + hecelerineAyrilmis(kelime) + ")");
            }
        } else {
            int yuzde = kokenHesapla(girdi.toLowerCase(lokal));
            System.out.println("Girilen \"" + girdi + "\" kelimesi %" + yuzde + " ihtimalle Türkçe kökenli. (" + hecelerineAyrilmis(girdi) + ")");
        }
    }
    
    private int kokenHesapla(String girdi) {
        int yuzde = 0;
        
        if(girdi.isEmpty() ||
           girdi.length() == 1 ||
           bastanVeyaSondanUcUnsuzVarMi(girdi) ||
           hepsiAyniMi(girdi) ||
           bcdgIleBitiyorMu(girdi) ||
           stringKarakterIceriyorMu(girdi, yabanci)) {
            return 0;
        }
        
        if(kucukUnlu(girdi)) yuzde += 30;
        if(buyukUnlu(girdi)) yuzde += 30;
        if(!ikiSesliYanYanaMi(girdi)) yuzde += 10;
        if(!bastanCiftUnsuzVarMi(girdi)) yuzde += 10;
        if(turkceKarakterVarMi(girdi)) yuzde += 20;
        
        return yuzde;
    }
    
    public boolean buyukUnlu(String kelime) {
        boolean inceIceriyor = false;
        for(String inceSesli: inceSesliler) {
            if(kelime.contains(inceSesli)) {
                inceIceriyor = true;
                break;
            }
        }
        
        boolean kalinIceriyor = false;
        for(String kalinSesli: kalinSesliler) {
            if(kelime.contains(kalinSesli)) {
                kalinIceriyor = true;
                break;
            }
        }
        
        if(inceIceriyor && kalinIceriyor) {
            return false;
        }
        
        return true;
    }
    
    public boolean kucukUnlu(String kelime) {
        ArrayList<Integer> sesliIndisler = sesliIndisler(kelime);
        
        if(sesliIndisler.isEmpty()) return false;
        if(sesliIndisler.size() == 1) return true;
        
        String[] yuvarlakGenisSesliler = diziKesisim(yuvarlakSesliler, genisSesliler);
        String[] duzDarSesliler = diziKesisim(duzSesliler, darSesliler);
        
        int ilkIndis = sesliIndisler.get(0);
        int ikinciIndis = sesliIndisler.get(1);
        int sayac = 1;
        
        while(true) {
            String ilkKarakter = kelime.charAt(ilkIndis) + "";
            String ikinciKarakter = kelime.charAt(ikinciIndis) + "";
            if(stringKarakterIceriyorMu(ilkKarakter, duzSesliler)) {
                // düzden sonra düz gelmeli
                if(!stringKarakterIceriyorMu(ikinciKarakter, duzSesliler)) {
                    return false;
                }
            } else {
                // yuvarlaktan sonra yuvarlak/dar veya düz/geniş gelmeli
                if(stringKarakterIceriyorMu(ikinciKarakter, yuvarlakGenisSesliler) ||
                   stringKarakterIceriyorMu(ikinciKarakter, duzDarSesliler)) {
                    return false;
                }
            }
            
            sayac++;
            
            if(sayac >= sesliIndisler.size()) break;
            
            ilkIndis = ikinciIndis;
            ikinciIndis = sesliIndisler.get(sayac);
        }
        
        return true;
    }
    
    private boolean stringKarakterIceriyorMu(String str, String[] karakterler) {
        return Arrays.stream(karakterler).parallel().anyMatch(str::contains);
    }
    
    private String ilkSesli(String str) {
        for(char karakter: str.toCharArray()) {
            if(sesliMi(karakter)) {
                return karakter + "";
            }
        }
        
        return null;
    }
    
    private boolean sesliMi(char c) {
        if(c == 'a' ||
           c == 'e' ||
           c == 'ı' ||
           c == 'i' ||
           c == 'o' ||
           c == 'ö' ||
           c == 'u' ||
           c == 'ü') {
            return true; 
        }
        
        return false;
    }
    
    private String[] diziKesisim(String[] dizi1, String[] dizi2) {
        Set<String> s1 = new HashSet<>(Arrays.asList(dizi1));
        Set<String> s2 = new HashSet<>(Arrays.asList(dizi2));
        s1.retainAll(s2);
        
        return s1.toArray(new String[s1.size()]);
    }
    
    private String karakteriCikart(String kelime, int karakterIndex) {
        StringBuilder sb = new StringBuilder(kelime).deleteCharAt(karakterIndex);
        return sb.toString();
    }
    
    private ArrayList<Integer> sesliIndisler(String kelime) {
        ArrayList<Integer> indisler = new ArrayList<>();
        char[] kelimeArray = kelime.toCharArray();
        for(int i = 0; i < kelimeArray.length; i++) {
            char karakter = kelimeArray[i];
            if(sesliMi(karakter)) {
                indisler.add(i);
            }
        }
        
        return indisler;
    }
    
    public boolean ikiSesliYanYanaMi(String kelime) {
        ArrayList<Integer> sesliIndisler = sesliIndisler(kelime);
        
        if(sesliIndisler.isEmpty() || sesliIndisler.size() == 1) return false;
        
        int ilkIndis = sesliIndisler.get(0);
        int ikinciIndis = sesliIndisler.get(1);
        int sayac = 1;
        
        while(true) {
            if(ikinciIndis == ilkIndis + 1) return true;
            
            sayac++;
            
            if(sayac >= sesliIndisler.size()) break;
            
            ilkIndis = ikinciIndis;
            ikinciIndis = sesliIndisler.get(sayac);
        }
        
        return false;
    }
    
    public boolean bcdgIleBitiyorMu(String kelime) {
        if(kelime.endsWith("b") ||
           kelime.endsWith("c") ||
           kelime.endsWith("d") ||
           kelime.endsWith("g")) {
            return true;
        }
        
        return false;
    }
    
    public boolean turkceKarakterVarMi(String kelime) {
        if(kelime.contains("ğ")) {
            return true;
        }
        
        return false;
    }
    
    public boolean hepsiAyniMi(String kelime) {
        char[] harfler = kelime.toCharArray();
        boolean bayrak = true;
        int ilk = harfler[0];
        for(int i = 1; i < harfler.length && bayrak; i++)
        {
          if (harfler[i] != ilk) bayrak = false;
        }
        
        return bayrak;
    }
    
    public String[] hecelerineAyir(String girdi) {
        String ikiliKelime = ikiliSistemeCevir(girdi);
        
        ikiliKelime = ikiliKelime.replaceAll("101", "1-01");
        ikiliKelime = ikiliKelime.replaceAll("101", "1-01");
        ikiliKelime = ikiliKelime.replaceAll("1001", "10-01");
        ikiliKelime = ikiliKelime.replaceAll("1001", "10-01");
        ikiliKelime = ikiliKelime.replaceAll("10001", "100-01");
        ikiliKelime = ikiliKelime.replaceAll("10001", "100-01");
        
        String heceli = "";
        
        int sayac = 0;
        for(char bit: ikiliKelime.toCharArray()) {
            if(bit == '-') {
                heceli += "-";
            } else {
                heceli += girdi.charAt(sayac);
                sayac++;
            }
        }
        
        return heceli.split("-");
    }
    
    public String hecelerineAyrilmis(String girdi) {
        String ikiliKelime = ikiliSistemeCevir(girdi);
        
        ikiliKelime = ikiliKelime.replaceAll("101", "1-01");
        ikiliKelime = ikiliKelime.replaceAll("101", "1-01");
        ikiliKelime = ikiliKelime.replaceAll("1001", "10-01");
        ikiliKelime = ikiliKelime.replaceAll("1001", "10-01");
        ikiliKelime = ikiliKelime.replaceAll("10001", "100-01");
        ikiliKelime = ikiliKelime.replaceAll("10001", "100-01");
        
        String heceli = "";
        
        int sayac = 0;
        for(char bit: ikiliKelime.toCharArray()) {
            if(bit == '-') {
                heceli += "-";
            } else {
                heceli += girdi.charAt(sayac);
                sayac++;
            }
        }
        
        return heceli;
    }
    
    public String ikiliSistemeCevir(String kelime) {
        String sonuc = "";
        for(char harf: kelime.toCharArray()) {
            if(sesliMi(harf)) {
                sonuc += "1";
            } else {
                sonuc += "0";
            }
        }
        
        return sonuc;
    }
    
    public boolean bastanCiftUnsuzVarMi(String kelime) {
        if(kelime.length() < 2) return false;
        String ilkIki = kelime.substring(0, 2);
        boolean hepsiSessiz = true;
        for(char harf: ilkIki.toCharArray()) {
            if(sesliMi(harf)) {
                hepsiSessiz = false;
                break;
            }
        }
        
        return hepsiSessiz;
    }
    
    public boolean bastanVeyaSondanUcUnsuzVarMi(String kelime) {
        if(kelime.length() < 3) return false;
        String ilkUc = kelime.substring(0, 3);
        boolean ilk3hepsiSessiz = true;
        for(char harf: ilkUc.toCharArray()) {
            if(sesliMi(harf)) {
                ilk3hepsiSessiz = false;
                break;
            }
        }
        
        if(ilk3hepsiSessiz) return true;
        
        if(kelime.length() > 3) {
            String sonUc = kelime.substring(kelime.length() - 3);
            boolean son3hepsiSessiz = true;
            for(char harf: sonUc.toCharArray()) {
                if(sesliMi(harf)) {
                    son3hepsiSessiz = false;
                    break;
                }
            }

            if(son3hepsiSessiz) return true;

            return false;
        }
        
        return false;
    }
}
