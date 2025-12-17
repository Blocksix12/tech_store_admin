package com.teamforone.tech_store.service.admin;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.SmsSubmissionResponseMessage;
import com.vonage.client.sms.messages.TextMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SmsService {
    @Value("${vonage.api_key}")
    private String apiKey;

    @Value("${vonage.api_secret}")
    private String apiSecret;

    @Value("${vonage.from_name}")
    private String fromName;

    private VonageClient vonageClient;

    @PostConstruct
    public void init() {
        vonageClient = VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();
    }

    public void sendSms(String to, String message) {
        // Chuyển số sang E.164 (VN)
        String formatted = formatPhone(to);

        TextMessage textMessage = new TextMessage(fromName, formatted, message);
        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(textMessage);

        for (SmsSubmissionResponseMessage r : response.getMessages()) {
            if (r.getStatus() == MessageStatus.OK) {
                System.out.println("✅ SMS đã gửi tới " + formatted);
            } else {
                System.err.println("❌ Lỗi gửi SMS: " + r.getErrorText());
            }
        }
    }

    private String formatPhone(String phone) {
        phone = phone.trim();
        if (phone.startsWith("0")) {
            return "+84" + phone.substring(1);
        } else if (!phone.startsWith("+")) {
            return "+84" + phone;
        }
        return phone;
    }
}
