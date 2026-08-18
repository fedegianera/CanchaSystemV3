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
            helper.setSubject("[Cancha System] Verificá tu correo electrónico");

            String verificationUrl = "http://localhost:4200/verificar/" + verificationToken;
            String body = """
                    <h1 style="margin-bottom:0.5rem;font-size:28px;line-height:32px;font-weight:700">
                      Verificá tu correo
                    </h1>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0">
                      ¡Hola, %1$s!
                    </p>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0"><span>
                      Por favor, hacé click en el botón de abajo para verificar su dirección de correo en CanchaSystem.</span>
                    </p>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0"><span>
                      Si el botón no funciona, copiá el link abajo del botón y pegalo en tu buscador.</span>
                    </p>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0">
                      Si vos no te registraste en CanchaSystem, podés ignorar este mensaje.
                    </p>
                    <p style="font-size:1rem;line-height:24px;margin:16px 0">
                      Este email expira en 2 horas.
                    </p>
                    <a style="line-height:100%%;text-decoration:none;display:inline-block;max-width:100%%;color:rgb(255,255,255);border-radius:12px;background-color:rgb(95,125,55);padding: 0.75rem 1rem;font-size:14px;font-weight:700" href="%2$s" target="_blank">
                      <span></span>
                      <span style="max-width:100%%;display:inline-block;line-height:120%%">
                        Verificar correo electrónico
                      </span>
                      <span></span>
                    </a>
                    <a style="color:#067df7;text-decoration:none" href="%2$s" target="_blank">
                      <p style="color:rgb(119,119,119);margin: 0.5rem 0 16px;font-size:13px;line-height:16px;font-weight:700">
                        %2$s
                      </p>
                    </a>
                """
                    .formatted(
                            client.getName(),
                            verificationUrl
                    );

            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @Async
    public void sendPasswordResetMail(String toEmail, String name, String code) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setTo(toEmail);
            helper.setSubject("[Cancha System] Código para restablecer tu contraseña");

            String body = """
                <h1 style="margin-bottom:0.5rem;font-size:28px;line-height:32px;font-weight:700">
                  Restablecer contraseña
                </h1>
                <p style="font-size:1rem;line-height:24px;margin:16px 0">
                  ¡Hola, %1$s!
                </p>
                <p style="font-size:1rem;line-height:24px;margin:16px 0">
                  Recibimos una solicitud para restablecer tu contraseña. Usá el siguiente código para continuar:
                </p>
                <div style="text-align:center;margin:24px 0">
                  <span style="display:inline-block;background-color:rgb(95,125,55);color:white;font-size:32px;font-weight:700;letter-spacing:8px;padding:1rem 1.5rem;border-radius:12px">
                    %2$s
                  </span>
                </div>
                <p style="font-size:1rem;line-height:24px;margin:16px 0">
                  Este código expira en 15 minutos.
                </p>
                <p style="font-size:1rem;line-height:24px;margin:16px 0">
                  Si vos no solicitaste este cambio, podés ignorar este mensaje.
                </p>
              """
                    .formatted(name, code);

            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}