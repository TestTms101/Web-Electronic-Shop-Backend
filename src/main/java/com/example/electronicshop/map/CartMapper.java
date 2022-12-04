package com.example.electronicshop.map;

import com.example.electronicshop.communication.response.CartItemRes;
import com.example.electronicshop.communication.response.CartRes;
import com.example.electronicshop.models.enity.Order;
import com.example.electronicshop.models.enity.OrderItem;
import com.example.electronicshop.models.product.ProductOption;
import com.example.electronicshop.repository.ProductOptionRepository;
import com.example.electronicshop.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartMapper {
    private static ProductOptionRepository productOptionRepository;
    private static ProductRepository productRepository;
    public CartRes toCartRes(Order order) {
        CartRes res = new CartRes(order.getId(), order.getTotalProduct(), order.getTotalPrice(), order.getState());
        res.setItems(order.getItems().stream().map(CartMapper::toCartItemRes).collect(Collectors.toList()));
        return res;
    }

    public static CartItemRes toCartItemRes(OrderItem orderItem) {
//        Optional<ProductOption> stockOption = productOptionRepository.findByProductIdAndAndValue(new ObjectId(orderItem.getItem().getId()), orderItem.getValue());
//        Optional<Product> product = productRepository.findById(orderItem.getItem().getId());
//        if(product.get().getOptions().isEmpty()){
//            return new CartItemRes(orderItem.getId(), orderItem.getItem().getId(),
//                    orderItem.getItem().getName(), orderItem.getItem().getSale(), orderItem.getQuantity(),
//                    orderItem.getItem().getImages(), orderItem.getItem().getPrice(), orderItem.getValue(),
//                    product.get().getQuantity(), orderItem.getSubPrice());
//        }else return new CartItemRes(orderItem.getId(), orderItem.getItem().getId(),
//                orderItem.getItem().getName(), orderItem.getItem().getSale(), orderItem.getQuantity(),
//                orderItem.getItem().getImages(), orderItem.getItem().getPrice(), orderItem.getValue(),
//                1, orderItem.getSubPrice());
        if(orderItem.getItem().getOptions().size()==0){
            return new CartItemRes(orderItem.getId(), orderItem.getItem().getId(),
                    orderItem.getItem().getName(), orderItem.getItem().getSale(), orderItem.getQuantity(),
                    orderItem.getItem().getImages(), orderItem.getItem().getPrice(), orderItem.getValue(),
                    orderItem.getItem().getQuantity(), orderItem.getSubPrice());
        }else {
            Optional<ProductOption> stockOption = productOptionRepository.findByProductAndValue(new ObjectId(orderItem.getItem().getId()), orderItem.getValue());
            return new CartItemRes(orderItem.getId(), orderItem.getItem().getId(),
                    orderItem.getItem().getName(), orderItem.getItem().getSale(), orderItem.getQuantity(),
                    orderItem.getItem().getImages(), orderItem.getItem().getPrice(), orderItem.getValue(),
                    stockOption.get().getStock(), orderItem.getSubPrice());
        }

    }
//    public static CartItemRes toCartItemResOption(OrderItem orderItem) {
//        Optional<ProductOption> stockOption = productOptionRepository.findByProductIdAndAndValue(new ObjectId(orderItem.getItem().getId()), orderItem.getValue());
//        return new CartItemRes(orderItem.getId(), orderItem.getItem().getId(),
//                orderItem.getItem().getName(), orderItem.getItem().getSale(), orderItem.getQuantity(),
//                orderItem.getItem().getImages(), orderItem.getItem().getPrice(), orderItem.getValue(),
//                stockOption.get().getStock(), orderItem.getSubPrice());
//    }
}
