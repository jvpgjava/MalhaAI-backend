package br.edu.malhaia.adapters.out.websearch;

import br.edu.malhaia.application.port.out.WebSearchPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Busca DuckDuckGo HTML filtrada por allowlist de domínios técnicos.
 * Falhas de rede → lista vazia (orientação segue com RAG local).
 */
@Component
public class AllowlistWebSearchAdapter implements WebSearchPort {

	private static final Logger log = LoggerFactory.getLogger(AllowlistWebSearchAdapter.class);

	private static final Set<String> ALLOWLIST = Set.of(
			"stackoverflow.com",
			"developer.mozilla.org",
			"docs.oracle.com",
			"learn.microsoft.com",
			"cloud.google.com",
			"roadmap.sh",
			"freecodecamp.org",
			"en.wikipedia.org",
			"pt.wikipedia.org",
			"github.com",
			"dev.to"
	);

	private static final Pattern HREF = Pattern.compile("uddg=([^&\"]+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern TITLE = Pattern.compile(
			"class=\"result__a\"[^>]*>(.*?)</a>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern SNIPPET = Pattern.compile(
			"class=\"result__snippet\"[^>]*>(.*?)</", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private final HttpClient http = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(6))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();

	@Override
	public List<ResultadoBusca> buscar(String consulta, int limite) {
		if (consulta == null || consulta.isBlank() || limite <= 0) {
			return List.of();
		}
		try {
			String sites = String.join(" OR ", ALLOWLIST.stream().map(d -> "site:" + d).toList());
			String q = consulta.trim() + " (" + sites + ")";
			String url = "https://html.duckduckgo.com/html/?q="
					+ URLEncoder.encode(q, StandardCharsets.UTF_8);

			HttpRequest request = HttpRequest.newBuilder(URI.create(url))
					.timeout(Duration.ofSeconds(10))
					.header("User-Agent", "MalhaIA/1.0 (curricular-assistant; educational)")
					.GET()
					.build();

			HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 400) {
				log.warn("Busca web status {}", response.statusCode());
				return List.of();
			}
			return parse(response.body(), limite);
		} catch (Exception e) {
			log.warn("Busca web indisponível: {}", e.getMessage());
			return List.of();
		}
	}

	private List<ResultadoBusca> parse(String html, int limite) {
		List<String> urls = new ArrayList<>();
		Matcher hrefs = HREF.matcher(html);
		while (hrefs.find()) {
			String decoded = decode(hrefs.group(1));
			if (decoded != null && isAllowlisted(decoded)) {
				urls.add(decoded);
			}
		}
		List<String> titulos = new ArrayList<>();
		Matcher titles = TITLE.matcher(html);
		while (titles.find()) {
			titulos.add(strip(titles.group(1)));
		}
		List<String> trechos = new ArrayList<>();
		Matcher snippets = SNIPPET.matcher(html);
		while (snippets.find()) {
			trechos.add(strip(snippets.group(1)));
		}

		List<ResultadoBusca> out = new ArrayList<>();
		Set<String> vistos = new LinkedHashSet<>();
		int n = Math.min(limite, urls.size());
		for (int i = 0; i < n; i++) {
			String u = urls.get(i);
			if (!vistos.add(u)) {
				continue;
			}
			String titulo = i < titulos.size() && !titulos.get(i).isBlank() ? titulos.get(i) : u;
			String trecho = i < trechos.size() ? trechos.get(i) : titulo;
			if (trecho.isBlank()) {
				continue;
			}
			out.add(new ResultadoBusca(titulo, u, truncar(trecho, 600)));
		}
		return out;
	}

	private static boolean isAllowlisted(String url) {
		try {
			String host = URI.create(url).getHost();
			if (host == null) {
				return false;
			}
			String h = host.toLowerCase(Locale.ROOT);
			if (h.startsWith("www.")) {
				h = h.substring(4);
			}
			for (String allowed : ALLOWLIST) {
				if (h.equals(allowed) || h.endsWith("." + allowed)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private static String decode(String raw) {
		try {
			return java.net.URLDecoder.decode(raw, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return null;
		}
	}

	private static String strip(String raw) {
		return raw.replaceAll("(?s)<[^>]*>", " ")
				.replace("&amp;", "&").replace("&quot;", "\"").replace("&#x27;", "'")
				.replaceAll("\\s+", " ").trim();
	}

	private static String truncar(String s, int max) {
		return s == null ? "" : (s.length() <= max ? s : s.substring(0, max) + "…");
	}
}
