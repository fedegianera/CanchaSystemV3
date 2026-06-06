package com.example.CanchaSystem.service;

import com.example.CanchaSystem.model.Client;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendMail(Client client, UUID verificationToken) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setTo(client.getMail());
            helper.setSubject("[Cancha System] Verifique su correo electrónico");

            String verificationUrl = "http://localhost:4200/verificar/" + verificationToken;
            String body = """
                    <h1 style="margin-bottom:0.5rem;font-size:28px;line-height:32px;font-weight:700">
                      Verifique su correo
                    </h1>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0">
                      ¡Hola, %1$s!
                    </p>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0"><span>
                      Por favor, haga click en el botón de abajo para verificar su dirección de correo (%2$s).</span>
                    </p>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0"><span>
                      Si el botón no funciona, copie el link debajo del botón y péguelo en su buscador.</span>
                    </p>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0">
                      Este email expira en 2 horas.
                    </p>
                    <a style="line-height:100%%;text-decoration:none;display:inline-block;max-width:100%%;color:rgb(255,255,255);border-radius:12px;background-color:rgb(95,125,55);padding: 0.75rem 1rem;font-size:14px;font-weight:700" href="%3$s" target="_blank">
                      <span></span>
                      <span style="max-width:100%%;display:inline-block;line-height:120%%">
                        Verificar correo electrónico
                      </span>
                      <span></span>
                    </a>
                    <a style="color:#067df7;text-decoration:none" href="%3$s" target="_blank">
                      <p style="color:rgb(119,119,119);margin: 0.5rem 0 16px;font-size:13px;line-height:16px;font-weight:700">
                        %3$s
                      </p>
                    </a>
                """
                    .formatted(
                            client.getName(),
                            client.getMail(),
                            verificationUrl
                    );

            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}