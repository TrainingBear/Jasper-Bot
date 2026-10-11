package jasper.feature;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jasper.Main;
import jasper.Util.JsonConfig;
import jasper.featureData.ColorUtil;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

public final class Weather implements FeatureInterface {
	private static final Properties WEATHER_PROPERTIES = new Properties();
	private static final byte[] HTML_TEMPLATE;
	private static final String DB_URL = "jdbc:sqlite:cities.db";
	static {
		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement()) {
			stmt.execute("""
					    CREATE TABLE IF NOT EXISTS weather_City_Coor (
					        city TEXT PRIMARY KEY,
					        country TEXT NOT NULL,
					        latitude REAL NOT NULL,
					        longitude REAL NOT NULL
					    )
					""");
		} catch (Exception e) {
			e.printStackTrace();
		}
		final ClassLoader classLoader = Weather.class.getClassLoader();
		try (InputStream weatherStream = classLoader.getResourceAsStream("weather_id.properties");
				InputStream htmlStream = classLoader.getResourceAsStream("weather_template.html")) {
			WEATHER_PROPERTIES.load(weatherStream);
			HTML_TEMPLATE = htmlStream.readAllBytes();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Weather() {
	}

	@Override
	public CommandInfoContainer commandInsert() {
		// OptionData options = new OptionData(OptionType.STRING,
		// "city", "Masukkan nama kota atau koordinat (latitude, longitude)", true);
		// final List<String> prefixAliases = List.of("weather", "cuaca", "we", "c");
		// Help.inputFeature(new FeatureContainer(
		// prefixAliases,
		// "🔢 **!math** _atau_ **=**",
		// "Kondisi cuaca",
		// List.of("""
		// *Coming Soon*
		// """)));
		return new FeatureInterface.CommandInfoContainer("cuaca", "Coming soon feature, mager jir");
		// FeatureInterface.CommandInfoContainer(
		// "cuaca", "Kondisi cuaca saat ini", prefixAliases,
		// options);
	}

	@Override
	public void handleSlashCommand(SlashCommandInteractionEvent event) {
		final OptionMapping inputArg;
		if ((inputArg = event.getOption("city")) == null)
			return;
		event.replyEmbeds(ColorUtil.WAITING.getEmbedMessage("Weather...")
				.setDescription("Mohon tunggu...").build())
				.queue(message -> {
					final MessageEmbed msge = requestByCity(inputArg.getAsString().toLowerCase());
					message.editOriginalEmbeds(msge).queue();
					if (msge.getColorRaw() == ColorUtil.ERROR.getColor())
						message.deleteOriginal().queueAfter(Main.ERROR_DELETE_TIME, TimeUnit.SECONDS);
				});
	}

	@Override
	public void handleMessageCommand(MessageReceivedEvent event, String[] args) {
		final Message userMessage = event.getMessage();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(
					new ByteArrayInputStream(HTML_TEMPLATE));

			Java2DRenderer renderer = new Java2DRenderer(
					document,
					480,
					120);
			ImageIO.write(renderer.getImage(), "PNG", output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		userMessage.replyFiles(FileUpload.fromData(output.toByteArray(), "test.png")).queue();

		// if (args.length <= 0) {
		// userMessage.replyEmbeds(ColorUtil.ERROR.getEmbedMessage("**ERROR!** Weather")
		// .setDescription("Masukkan nama kota!")
		// .build());
		// return;
		// }

		// userMessage.replyEmbeds(ColorUtil.WAITING.getEmbedMessage("Weather...")
		// .setDescription("Mohon tunggu...").build())
		// .queue(message -> {
		// MessageEmbed msge;
		// String city = String.join(" ", args).toLowerCase();
		// if (args.length == 2)
		// try {
		// msge = requestByCoordinates(new double[] {
		// Double.parseDouble(args[0]),
		// Double.parseDouble(args[1])
		// });
		// } catch (NumberFormatException e) {
		// msge = requestByCity(city);
		// }
		// else
		// msge = requestByCity(city);

		// if (msge.getColorRaw() == ColorUtil.NORMAL.getColor())
		// message.editMessageEmbeds(msge).queue();
		// else {
		// message.delete().queueAfter(Main.ERROR_DELETE_TIME, TimeUnit.SECONDS);
		// try {
		// userMessage.delete().queueAfter(Main.ERROR_DELETE_TIME, TimeUnit.SECONDS);
		// } catch (Exception ignored) {
		// }
		// }
		// });
	}

	private static MessageEmbed requestByCity(@NotNull String cityName) {
		if (!cityName.matches("[a-zA-Z ]+"))
			return ColorUtil.ERROR.getEmbedMessage("**ERROR!** Weather")
					.setDescription("Nama kota hanya boleh huruf alfabet dan spasi!").build();
		final long startTimer = System.currentTimeMillis();

		ObjectNode wi;
		/** country name, latitude, longitude */
		Triple<String, Double, Double> city = getCityInfos(cityName.toLowerCase());
		try {
			if (city == null) {
				Main.sendLog("City: " + cityName + " missing, outsourcing...");
				ObjectNode oN = httpsHandle(
						"https://geocoding-api.open-meteo.com/v1/search?name="
								+ URLEncoder.encode(cityName, StandardCharsets.UTF_8) + "&count=1");
				if (!oN.has("results"))
					throw new RuntimeException("Kota " + cityName + " tidak ditemukan!");
				oN = (ObjectNode) oN.get("results").get(0);

				final String realCityName;
				try (Connection conn = DriverManager.getConnection(DB_URL);
						PreparedStatement ps = conn.prepareStatement(
								"INSERT OR IGNORE INTO weather_City_Coor(city, country, latitude, longitude) VALUES(?,?,?,?)")) {

					ps.setString(1, (realCityName = Normalizer.normalize(oN.get("name").asText(),
							Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase()));// āàč -> aac

					final String country = oN.get("country").asText();
					final double latitude = oN.get("latitude").asDouble(),
							longitude = oN.get("longitude").asDouble();

					ps.setString(2, country);
					ps.setDouble(3, latitude);
					ps.setDouble(4, longitude);
					ps.executeUpdate();
					city = new Triple<>(country, latitude, longitude);
				}
				final String capilatizedRealCity = Character.toUpperCase(realCityName.charAt(0))
						+ realCityName.substring(1);
				cityName = !cityName.equalsIgnoreCase(realCityName) ? "\\*" + capilatizedRealCity : capilatizedRealCity;
			} else
				cityName = Character.toUpperCase(cityName.charAt(0)) + cityName.substring(1);

			final String urlString = setupOpenMeteoLink(city.getSecond(), city.getThird());

			wi = httpsHandle(urlString);
		} catch (RuntimeException e) {
			return ColorUtil.ERROR.getEmbedMessage("**ERROR!** Weather")
					.setDescription(e.getMessage())
					.build();
		} catch (Exception e) {
			return ColorUtil.ERROR.getEmbedMessage("**ERROR!** Weather")
					.setDescription("Ada masalah pada https, " + e.getMessage())
					.build();
		}
		final long elapsed = System.currentTimeMillis() - startTimer;
		return getDescWeatherInfos(wi, cityName, elapsed)
				.setFooter(
						WEATHER_PROPERTIES.getProperty(
								"country." + city.getFirst().replace(' ', '_'), city.getFirst())
								+ ' ' + wi.get("timezone_abbreviation").asText() + "\nby Open-Meteo")
				.build();
	}

	private static MessageEmbed requestByCoordinates(@NotNull double... locations) {
		final String urlString = setupOpenMeteoLink(locations);
		ObjectNode wi;
		final long startTimer = System.currentTimeMillis();

		try {
			wi = httpsHandle(urlString);
		} catch (RuntimeException e) {
			return ColorUtil.ERROR.getEmbedMessage("**ERROR!** Weather")
					.setDescription(e.getMessage())
					.build();
		} catch (Exception e) {
			return ColorUtil.ERROR.getEmbedMessage("**ERROR!** Weather")
					.setDescription("Ada masalah pada https, " + e.getMessage())
					.build();
		}
		final long elapsed = System.currentTimeMillis() - startTimer;
		return getDescWeatherInfos(wi, null, elapsed)
				.setFooter(cutTimezoneString(wi.get("timezone").asText()) + ' '
						+ wi.get("timezone_abbreviation").asText() + "\nby Open-Meteo")
				.build();
	}

	private static EmbedBuilder getDescWeatherInfos(@NotNull ObjectNode wi, @Nullable String cityName,
			final long elapsed) {
		final ObjectNode curr = (ObjectNode) wi.get("current"),
				units = (ObjectNode) wi.get("current_units");
		return ColorUtil.NORMAL.getEmbedMessage("Weather Info — " + elapsed + "ms")
				.setDescription(new StringBuilder()
						.append(cityName != null ? "Kota: **" + cityName + "**\n" : "")
						.append("Lat: " + wi.get("latitude") + ", Lon: " + wi.get("longitude"))
						.append("\n☁️**Awan**: " + WEATHER_PROPERTIES.getProperty("weather."
								+ curr.get("weather_code"), "..."))
						.append("\nTertutup awan: " + curr.get("cloud_cover")
								+ units.get("cloud_cover").asText())
						.append("\nKecepatan angin: "
								+ Main.subDigit(curr.get("wind_speed_10m").asDouble() * 5.0 / 18.0,
										2, false)
								+ "m/s")
						.append("\nJarak pandang: " + (curr.get("visibility").asInt() / 1_000) + "km")
						.append("\n**🌡Udara:**")
						.append("\nSuhu: "
								+ Main.subDigit(curr.get("temperature_2m").asDouble(), 2, false)
								+ units.get("temperature_2m").asText())
						.append("\nTerasa: " + Main.subDigit(curr.get("apparent_temperature").asDouble(),
								2, false) + units.get("apparent_temperature").asText())
						.append("\nKelembapan: " + curr.get("relative_humidity_2m")
								+ units.get("relative_humidity_2m").asText())
						.toString());
	}

	/**
	 * 
	 * @param cityName
	 * @return {@link Triple}<{@link String country}, {@link Double latitude},
	 *         {@link Double longitude}>
	 */
	private static Triple<String, Double, Double> getCityInfos(final String cityName) {
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement ps = conn.prepareStatement(
						"SELECT country, latitude, longitude FROM weather_City_Coor WHERE city = ?")) {
			ps.setString(1, cityName);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next()
						? new Triple<>(
								rs.getString("country"),
								rs.getDouble("latitude"),
								rs.getDouble("longitude"))
						: null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	private static ObjectNode httpsHandle(final String url) throws Exception {
		HttpsURLConnection conn = null;
		try {
			conn = (HttpsURLConnection) URI.create(url).toURL().openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			final int responseCode;
			if ((responseCode = conn.getResponseCode()) != HttpsURLConnection.HTTP_OK)
				throw new RuntimeException("Ada masalah pada API: " + responseCode);

			StringBuilder response = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null)
					response.append(line);
			}
			return JsonConfig.parseFromString(response.toString());
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}

	private static String setupOpenMeteoLink(double... locs) {
		if (locs.length != 2)
			throw new IllegalArgumentException("Locs argument not valid: " + locs.length + " argument(s)");
		return "https://api.open-meteo.com/v1/forecast?latitude="
				+ locs[0] + "&longitude=" + locs[1]
				+ "&current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,visibility,cloud_cover,weather_code&timezone=auto";
	}

	private static String cutTimezoneString(@NotNull String loc) {
		int i = 0;
		while (i < loc.length())
			if (loc.charAt(i++) == '/')
				break;
		return loc.substring(i).replace("_", " ");
	}
}