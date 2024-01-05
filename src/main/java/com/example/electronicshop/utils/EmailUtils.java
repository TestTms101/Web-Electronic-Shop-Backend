package com.example.electronicshop.utils;

import com.example.electronicshop.models.enity.Order;
import com.example.electronicshop.service.MailService;
import com.example.electronicshop.service.MailType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Service
@Getter
@Setter
public class EmailUtils implements Runnable{
    private MailService mailService;
    private Order order;

    @Override
    @SneakyThrows
    public void run() {
        Map<String, Object> model = new HashMap<>();
        Locale locale = new Locale("vn", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String paid = "Chưa thanh toán";

        model.put("orderId", order.getId());
        model.put("total", currencyFormatter.format(order.getTotalPrice().add(new BigDecimal(order.getDelivery().getDeliveryInfo().get("fee").toString()))));
        model.put("paymentType", order.getPaymentDetail().getPaymentType());
        if ((boolean) order.getPaymentDetail().getPaymentInfo().get("isPaid"))
            paid = "Đã thanh toán";
        model.put("isPaid", paid);
        model.put("name", order.getDelivery().getShipName());
        model.put("presenttime", LocalDate.now());
        model.put("phone", order.getDelivery().getShipPhone());
        model.put("address", order.getDelivery().getShipAddress()+ ", "+
                order.getDelivery().getShipWard()+ ", "+
                order.getDelivery().getShipDistrict()+ ", "+
                order.getDelivery().getShipProvince());
        model.put("subtotal",order.getTotalPrice());
        model.put("ship",new BigDecimal(order.getDelivery().getDeliveryInfo().get("fee").toString()));
        Map<String, String> items = new HashMap<>();
        order.getItems().forEach(item -> items.put(String.format("<img src=\"%s\" alt=\"\" width=\"42\" height=\"42\" style=\"vertical-align:middle\"> <span>%s</span> <br/> <span>[SL: %s]</span>", item.getItem().getImages().get(0).getUrl(),item.getItem().getName(), item.getQuantity()), currencyFormatter.format(item.getSubPrice())));
        model.put("orders", items);
        mailService.sendEmail(order.getUser().getEmail(), model, MailType.ORDER);
    }
}
