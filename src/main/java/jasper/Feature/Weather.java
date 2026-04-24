package jasper.Feature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static jasper.VariableList.weathercdboolean;

public class Weather {
    static String wclouddesc,wcityname,wcountryname;
    static long whumidity,wcloudcover,wtimezone,wpressure;
    static double wtemp,wwindspeed,wvisibility,wtempfeels;
    static short weatherrrorcode;

    public static void weathercommand(MessageReceivedEvent event, String city){
        if(!weathercdboolean) {
            weathercdboolean = true;
            Timer weathercd = new Timer();
            weathercd.schedule(new TimerTask() {
                @Override
                public void run() {
                    weathercdboolean = false;
                }
            }, 5000); // 5 detik
            weatherdata(city);
            if (weatherrrorcode > 200) {
                EmbedBuilder errorweather = new EmbedBuilder();
                errorweather.setColor(0xab00ff);
                if (weatherrrorcode == 404) {
                    errorweather.setTitle("ERROR! Info Cuaca");
                    errorweather.setDescription("Input kota yang benar!\ncoba input menggunakan bahasa inggris");
                } else {
                    errorweather.setTitle("ERROR! Info Cuaca");
                    errorweather.setDescription("Something went wrong...");
                }
                event.getMessage().replyEmbeds(errorweather.build()).queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
                weatherrrorcode = 0;
            } else {
                weatherembd(event);
            }
        }else{
            EmbedBuilder errorweather = new EmbedBuilder();
            errorweather.setColor(0xab00ff);
            errorweather.setTitle("ERROR! Info Cuaca");
            errorweather.setDescription("Sedang cooldown!");
            event.getMessage().replyEmbeds(errorweather.build()).queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(8,TimeUnit.SECONDS);
        }
    }
    public static void weathercommand(SlashCommandInteractionEvent event, String city){
        if(!weathercdboolean) {
            weathercdboolean = true;
            Timer weathercd = new Timer();
            weathercd.schedule(new TimerTask() {
                @Override
                public void run() {
                    weathercdboolean = false;
                }
            }, 5000); // 5 detik
            weatherdata(city);
            if (weatherrrorcode > 200) {
                EmbedBuilder errorweather = new EmbedBuilder();
                errorweather.setColor(0xab00ff);
                if (weatherrrorcode == 404) {
                    errorweather.setTitle("ERROR! Info Cuaca");
                    errorweather.setDescription("Input kota yang benar!\ncoba input menggunakan bahasa inggris");
                } else {
                    errorweather.setTitle("ERROR! Info Cuaca");
                    errorweather.setDescription("Something went wrong...");
                }
                event.replyEmbeds(errorweather.build()).queue(message -> message.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
                weatherrrorcode = 0;
            } else {
                weatherembd(event);
            }
        }else{
            EmbedBuilder errorweather = new EmbedBuilder();
            errorweather.setColor(0xab00ff);
            errorweather.setTitle("ERROR! Info Cuaca");
            errorweather.setDescription("Sedang cooldown!");
            event.replyEmbeds(errorweather.build()).queue(message -> message.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
        }
    }
    //
    static void weatherdata(String city){
        final String API_KEY = "f0a6f72a69791b3f8ef4a451bc5291e9"; // Ganti dengan API key Anda
        final String URL = "https://api.openweathermap.org/data/2.5/weather?q="+ city + "&appid=" + API_KEY + "&units=metric";
        try {
            // Buka koneksi ke API
            java.net.URL url = URI.create(URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // Periksa status HTTP
            if (conn.getResponseCode() != 200) {
                weatherrrorcode= (short) conn.getResponseCode();
                System.out.println("cd: "+weatherrrorcode);
            }

            // Baca respons dari API
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new InputStreamReader(conn.getInputStream()));

            // COLLECT MAIN DATA
            JSONObject main = (JSONObject) jsonObject.get("main");
            wtemp = ((Number) main.get("temp")).doubleValue(); // Konversi aman ke Double
            whumidity = (long) main.get("humidity");
            wtempfeels = ((Number) main.get("feels_like")).doubleValue(); // Konversi aman ke Double
            wpressure = (long) main.get("pressure");

            // COLLECT WEATHER DATA (FIXED)
            JSONArray weatherArray = (JSONArray) jsonObject.get("weather"); // ✅ Ambil sebagai JSONArray
            JSONObject weather = (JSONObject) weatherArray.getFirst(); // ✅ Ambil elemen pertama
            wclouddesc = (String) weather.get("description");
            // COLLECT CLOUD DATA
            JSONObject clouds = (JSONObject) jsonObject.get("clouds");
            wcloudcover = (long) clouds.get("all");
            // COLLECT WIND DATA
            JSONObject wind = (JSONObject) jsonObject.get("wind");
            wwindspeed = ((Number) wind.get("speed")).doubleValue();
            // COLLECT COUNTRY DATA
            JSONObject sys = (JSONObject) jsonObject.get("sys");
            wcountryname = (String) sys.get("country");
            wcountryname = wcountryname.toLowerCase();

            wvisibility = (long) jsonObject.get("visibility");
            wvisibility/=1000;
            wtimezone = (long) jsonObject.get("timezone");
            wcityname= (String) jsonObject.get("name");
            conn.disconnect();
        } catch (Exception e){e.printStackTrace();}
    }

    static void weatherembd(MessageReceivedEvent event){
        EmbedBuilder weatherembd = new EmbedBuilder();
        weatherembdbuilder(weatherembd);
        event.getMessage().replyEmbeds(weatherembd.build()).queue();
        clearweatherdata();
    }
    static void weatherembd(SlashCommandInteractionEvent event){
        EmbedBuilder weatherembd = new EmbedBuilder();
        weatherembdbuilder(weatherembd);
        event.replyEmbeds(weatherembd.build()).queue();
        clearweatherdata();
    }
    //
    static void weatherembdbuilder(EmbedBuilder weatherembd){
        switch (wclouddesc){
            case "clear sky"-> wclouddesc = "Langit cerah";
            case "few clouds"->wclouddesc="Sedikit awan";
            case "scattered clouds"->wclouddesc="Awan menyebar";
            case "broken clouds"->wclouddesc = "Awan terpecah";
            case "shower rain"->wclouddesc = "Hujan sebentar";
            case "rain"->wclouddesc = "Hujan";
            case "thunderstorm"->wclouddesc = "Badai petir";
            case "thunderstorm with light rain"->wclouddesc = "Badai petir *dengan* hujan ringan";
            case "thunderstorm with rain"->wclouddesc = "Badai petir *dengan* hujan";
            case "thunderstorm with heavy rain"->wclouddesc ="Badai petir *dengan* hujan deras";
            case "light thunderstorm"->wclouddesc="Badai petir ringan";
            case "heavy thunderstorm"->wclouddesc="Badai petir berat";
            case "ragged thunderstorm"->wclouddesc="Badai petir tidak merata";
            case "thunderstorm with light drizzle"->wclouddesc="Badai petir *dengan* gerimis ringan";
            case "thunderstorm with drizzle"->wclouddesc="Badai petir *dengan* gerimis";
            case "thunderstorm with heavy drizzle"->wclouddesc="Badai petir *dengan* gerimis deras";
            case "mist"->wclouddesc = "Berkabut (tipis)";
            case "overcast clouds"->wclouddesc = "Awan mendung";
            case "hurricane"->wclouddesc = "Angin topan";
            case "windy"->wclouddesc = "Berangin";
            case "fog"->wclouddesc = "Kabut";
            case "haze"->wclouddesc = "Kabut asap";
            case "drizzle"->wclouddesc = "Gerimis";
            case "drizzle rain"->wclouddesc = "Hujan gerimis";
            case "light intensity drizzle"->wclouddesc="Gerimis intesitas ringan";
            case "heavy intensity drizzle"->wclouddesc="Gerimis intesitas berat";
            case "light intensity drizzle rain"->wclouddesc="hujan gerimis intesitas ringan";
            case "heavy intensity drizzle rain"->wclouddesc="hujan gerimis intesitas berat";
            case "shower rain and drizzle"->wclouddesc="Hujan ringan & gerimis";
            case "heavy shower rain and drizzle"->wclouddesc="Hujan deras & gerimis";
            case "shower drizzle"->wclouddesc="Gerimis sebentar";
            case "light rain"->wclouddesc="Hujan ringan";
            case "moderate rain"->wclouddesc="Hujan sedang";
            case "heavy intensity rain"->wclouddesc="Hujan intesitas berat";
            case "very heavy rain"->wclouddesc="Hujan sangat deras";
            case "extreme rain"->wclouddesc = "Hujan ekstrim";
            case "freezing rain"->wclouddesc="Hujan beku";
            case "light intensity shower rain"->wclouddesc="Hujan intesitas ringan sebentar";
            case "heavy intensity shower rain"->wclouddesc="Hujan intesitas berat sebentar";
            case "ragged shower rain"->wclouddesc="Hujan sebentar tidak merata";
            case "light snow"->wclouddesc="Salju ringan";
            case "snow"->wclouddesc = "Bersalju";
            case "heavy snow"->wclouddesc="Salju lebat";
            case "sleet"->wclouddesc="Hujan es";
            case "light shower sleet"->wclouddesc="Hujan es ringan";
            case "shower sleet"->wclouddesc="Hujan es sebentar";
            case "light rain and snow"->wclouddesc="Hujan ringan *dan* salju";
            case "rain and snow"->wclouddesc="Hujan *dan* salju";
            case "light shower snow"->wclouddesc="Hujan salju ringan";
            case "shower snow"->wclouddesc="Hujan salju";
            case "heavy shower snow"->wclouddesc="Hujan salju berat";
            case "smoke"->wclouddesc="Berasap";
            case "sand"->wclouddesc="Badai pasir";
            case "squall","squalls"->wclouddesc="Badai";
            case "tornado"->wclouddesc="Tornado";
            default -> wclouddesc="error...";
        }
        switch (wcountryname){
            case "sq" -> wcountryname = "Albanian";
            case "af" -> wcountryname = "Afrika";
            case "ar" -> wcountryname = "Arab";
            case "az" -> wcountryname = "Azerbaijani";
            case "eu" -> wcountryname = "Basque";
            case "be" -> wcountryname = "Belarusia";
            case "bg" -> wcountryname = "Bulgaria";
            case "ca" -> wcountryname = "Catalan";
            case "zh_cn","zh_tw" -> wcountryname = "Cina";
            case "hr" -> wcountryname = "Kroasia";
            case "cz" -> wcountryname = "Ceko";
            case "da" -> wcountryname = "Denmark";
            case "nl" -> wcountryname = "Belanda";
            case "en" -> wcountryname = "Inggris";
            case "fi" -> wcountryname = "Finlandia";
            case "fr","ci" -> wcountryname = "Prancis";
            case "gb" -> wcountryname = "Britania Raya (UK)";
            case "gl" -> wcountryname = "Galicia";
            case "de" -> wcountryname = "Jerman";
            case "el" -> wcountryname = "Yunani";
            case "he" -> wcountryname = "Ibrani";
            case "hi" -> wcountryname = "India";
            case "hu" -> wcountryname = "Hungaria";
            case "is" -> wcountryname = "Islandia";
            case "id" -> wcountryname = "Indonesia";
            case "it" -> wcountryname = "Italia";
            case "ja" -> wcountryname = "Jepang";
            case "kr" -> wcountryname = "Korea Selatan";
            case "kp" -> wcountryname = "Korea Utara";
            case "ku" -> wcountryname = "Kurdi";
            case "la" -> wcountryname = "Latvia";
            case "lr" -> wcountryname = "Liberia";
            case "lt" -> wcountryname = "Lithuania";
            case "mk" -> wcountryname = "Macedonia";
            case "my" -> wcountryname = "Malaysia";
            case "no" -> wcountryname = "Norwegia";
            case "fa" -> wcountryname = "Persia";
            case "pl" -> wcountryname = "Polandia";
            case "pt" -> wcountryname = "Portugis";
            case "pt_br" -> wcountryname = "Português Brasil";
            case "ro" -> wcountryname = "Rumania";
            case "ru" -> wcountryname = "Rusia";
            case "sr" -> wcountryname = "Serbia";
            case "sk" -> wcountryname = "Slovakia";
            case "sl" -> wcountryname = "Slovenia";
            case "sp", "es" -> wcountryname = "Spanyol"; // Menggunakan OR untuk variasi kode
            case "sv", "se" -> wcountryname = "Swedia";
            case "th" -> wcountryname = "Thailand";
            case "tr" -> wcountryname = "Turki";
            case "ua", "uk" -> wcountryname = "Ukrainia";
            case "us" -> wcountryname = "Amerika Serikat (US)";
            case "vi" -> wcountryname = "Vietnam";
            case "za" -> wcountryname = "Afrika Selatan";
            case "zu" -> wcountryname = "Zulu";
        }

        weatherembd.setColor(0xff00b6);
        weatherembd.setTitle("Info Cuaca");
        weatherembd.setDescription("""
                Kota **%s**
                ☁️**Awan**: %s
                  Tertutup awan: %d%%
                  Kecepatan angin: %.2fm/s
                  Jarak pandang: %.2fkm
                🌡**Udara**:
                  Suhu: %.2f℃
                  Terasa: %.2f℃
                  Kelembapan: %d%%
                  Tekanan: %dhPa
                """.formatted(wcityname,wclouddesc,wcloudcover,wwindspeed,wvisibility,wtemp,wtempfeels,whumidity,wpressure));
        weatherembd.setFooter(wcountryname+" UTC - "+wtimezone/3600+"\nby openweathermap");
    }
    static void clearweatherdata(){
        wclouddesc="";wcityname="";wcountryname="";
        whumidity=0;wcloudcover=0;wtimezone=0;wpressure=0;
        wtemp=0;wwindspeed=0;wvisibility=0;wtempfeels=0;
    }
}
