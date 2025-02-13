package com.nextroom.nextRoomServer.util;

import com.nextroom.nextRoomServer.util.inapp.AndroidPurchaseUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
public class AndroidPurchaseUtilsTest {

    @Autowired
    private AndroidPurchaseUtils androidPurchaseUtils;

    @Test
    public void verifyPurchase() {
        String purchaseToken = "";

        try {
            androidPurchaseUtils.verifyPurchase(purchaseToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void verifyNotification() {
        String purchaseToken = "";

        try {
            androidPurchaseUtils.verifyNotification(purchaseToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
