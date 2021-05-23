package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.Compounder;
import com.gitlab.sszuev.flashcards.TextToSpeechService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * To work with external Voice RSS API.
 * Created by @ssz on 22.05.2021.
 *
 * @see <a href='http://voicerss.org/api/'>Voice RSS</a>
 */
@Service
public class VoiceRSSClientTTSImpl implements TextToSpeechService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VoiceRSSClientTTSImpl.class);

    private static final List<Map.Entry<String, String>> LANGUAGES = List.of(
            Map.entry("ar-eg", "Arabic (Egypt)")
            , Map.entry("ar-sa", "Arabic (Saudi Arabia)")
            , Map.entry("bg-bg", "Bulgarian")
            , Map.entry("ca-es", "Catalan")
            , Map.entry("zh-cn", "Chinese (China)")
            , Map.entry("zh-hk", "Chinese (Hong Kong)")
            , Map.entry("zh-tw", "Chinese (Taiwan)")
            , Map.entry("hr-hr", "Croatian")
            , Map.entry("cs-cz", "Czech")
            , Map.entry("da-dk", "Danish")
            , Map.entry("nl-be", "Dutch (Belgium)")
            , Map.entry("nl-nl", "Dutch (Netherlands)")
            , Map.entry("en-au", "English (Australia)")
            , Map.entry("en-ca", "English (Canada)")
            , Map.entry("en-gb", "English (Great Britain)")
            , Map.entry("en-in", "English (India)")
            , Map.entry("en-ie", "English (Ireland)")
            , Map.entry("en-us", "English (United States)")
            , Map.entry("fi-fi", "Finnish")
            , Map.entry("fr-ca", "French (Canada)")
            , Map.entry("fr-fr", "French (France)")
            , Map.entry("fr-ch", "French (Switzerland)")
            , Map.entry("de-at", "German (Austria)")
            , Map.entry("de-de", "German (Germany)")
            , Map.entry("de-ch", "German (Switzerland)")
            , Map.entry("el-gr", "Greek")
            , Map.entry("he-il", "Hebrew")
            , Map.entry("hi-in", "Hindi")
            , Map.entry("hu-hu", "Hungarian")
            , Map.entry("id-id", "Indonesian")
            , Map.entry("it-it", "Italian")
            , Map.entry("ja-jp", "Japanese")
            , Map.entry("ko-kr", "Korean")
            , Map.entry("ms-my", "Malay")
            , Map.entry("nb-no", "Norwegian")
            , Map.entry("pl-pl", "Polish")
            , Map.entry("pt-br", "Portuguese (Brazil)")
            , Map.entry("pt-pt", "Portuguese (Portugal)")
            , Map.entry("ro-ro", "Romanian")
            , Map.entry("ru-ru", "Russian")
            , Map.entry("sk-sk", "Slovak")
            , Map.entry("sl-si", "Slovenian")
            , Map.entry("es-mx", "Spanish (Mexico)")
            , Map.entry("es-es", "Spanish (Spain)")
            , Map.entry("sv-se", "Swedish")
            , Map.entry("ta-in", "Tamil")
            , Map.entry("th-th", "Thai")
            , Map.entry("tr-tr", "Turkish")
            , Map.entry("vi-vn", "Vietnamese"));

    private final RestTemplate restTemplate;
    private final Compounder compounder;

    private final String api;
    private final String format;
    private final String key;

    public VoiceRSSClientTTSImpl(RestTemplate template,
                                 Compounder compounder,
                                 @Value("${app.speaker.voicerss.api:api.voicerss.org}") String url,
                                 @Value("${app.speaker.voicerss.format:16khz_16bit_stereo}") String format,
                                 @Value("${app.speaker.voicerss.key}") String key) {
        this.compounder = Objects.requireNonNull(compounder);
        this.api = Objects.requireNonNull(url);
        this.key = Objects.requireNonNull(key);
        this.format = Objects.requireNonNull(format);
        this.restTemplate = Objects.requireNonNull(template);
    }

    @Override
    public String getResourceID(String text, String language, String... options) {
        String lang = LANGUAGES.stream()
                .map(Map.Entry::getKey)
                .filter(x -> x.startsWith(language.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported language " + language));
        return compounder.create(lang, text);
    }

    @Override
    public Resource getResource(String id) {
        String lang = compounder.getFirst(id);
        String text = compounder.getRest(id);

        @SuppressWarnings("HttpUrlsUsage")
        String uri = UriComponentsBuilder.fromHttpUrl("http://" + api)
                .queryParam("key", key)
                .queryParam("f", format)
                .queryParam("hl", lang)
                .queryParam("src", text)
                .toUriString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("URL:::<{}>", uri);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, new MediaType("audio", "wav").toString());

        HttpEntity<byte[]> entity = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);

        byte[] res = entity.getBody();
        if (res == null) {
            throw new RuntimeException("No content found by URI " + uri);
        }
        return new ByteArrayResource(res);
    }
}
