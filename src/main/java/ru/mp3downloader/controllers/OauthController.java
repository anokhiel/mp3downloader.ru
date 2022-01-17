package ru.mp3downloader.controllers;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mp3downloader.exception.AuthorizationErrorException;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Optional;

@Slf4j
@Controller
public class OauthController {

    @Value("${yandex.oauth.address}")
    private String url;

    @Value("${yandex.client.id}")
    private String clientId;

    @Value("${yandex.client.secret}")
    private String clientSecret;

    @GetMapping("/oauth")
    public String oauth(@RequestParam Optional<String> code, HttpSession session) {
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            MultivaluedMap formData = new MultivaluedMapImpl();
            formData.add("code", code.orElseThrow(AuthorizationErrorException::new));
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("grant_type", "authorization_code");
            ClientResponse response = webResource
                    .accept("application/json")
                    .type(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .post(ClientResponse.class, formData);
            String str = response.getEntity(String.class);
            JSONObject json = new JSONObject(str);
            session.setAttribute("yandex", json.getString("access_token"));
            log.info("Получен ответ от yandexa "+json.getString("access_token"));
            return "redirect:/";

        } catch (Exception e) {

            e.printStackTrace();
            return "redirect:/";

        }
    }
    @GetMapping("/noyandex")
    public String noYandex(HttpSession session){
        session.setAttribute("yandex", "noauth");
        return "redirect:/";
    }

}
