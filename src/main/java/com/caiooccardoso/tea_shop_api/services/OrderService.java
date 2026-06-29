package com.caiooccardoso.tea_shop_api.services;

import com.caiooccardoso.tea_shop_api.dto.CreateOrderDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderItemDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderAddressResponseDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderCustomerResponseDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderDetailResponseDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderItemResponseDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderSummaryResponseDTO;
import com.caiooccardoso.tea_shop_api.exceptions.InsufficientStockException;
import com.caiooccardoso.tea_shop_api.exceptions.OrderNotFoundException;
import com.caiooccardoso.tea_shop_api.exceptions.ProductNotFoundException;
import com.caiooccardoso.tea_shop_api.exceptions.UserNotFoundException;
import com.caiooccardoso.tea_shop_api.models.Address;
import com.caiooccardoso.tea_shop_api.models.Order;
import com.caiooccardoso.tea_shop_api.models.OrderItem;
import com.caiooccardoso.tea_shop_api.models.Product;
import com.caiooccardoso.tea_shop_api.models.User;
import com.caiooccardoso.tea_shop_api.repositories.OrderRepository;
import com.caiooccardoso.tea_shop_api.repositories.ProductRepository;
import com.caiooccardoso.tea_shop_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderDetailResponseDTO createOrder(CreateOrderDTO createOrderDTO) {
        User customer = userRepository.findById(createOrderDTO.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO item : createOrderDTO.getItems()) {
            Product product = productRepository.getProductById(item.getProductId());
            if (product == null) {
                throw new ProductNotFoundException("Produto não encontrado");
            }

            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Estoque insuficiente para o produto: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItems.add(orderItem);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setAddress(createOrderDTO.getAddress());
        order.setItems(orderItems);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        return toOrderDetailResponse(savedOrder);
    }

    public OrderDetailResponseDTO getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado"));
        return toOrderDetailResponse(order);
    }

    public List<OrderSummaryResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toOrderSummaryResponse)
                .toList();
    }

    private OrderSummaryResponseDTO toOrderSummaryResponse(Order order) {
        OrderSummaryResponseDTO responseDTO = new OrderSummaryResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setCreatedAt(order.getCreatedAt());
        responseDTO.setItemCount(order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
        responseDTO.setTotal(order.getTotal());
        return responseDTO;
    }

    private OrderDetailResponseDTO toOrderDetailResponse(Order order) {
        OrderDetailResponseDTO responseDTO = new OrderDetailResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setCreatedAt(order.getCreatedAt());
        responseDTO.setCustomer(toCustomerResponse(order.getCustomer()));
        responseDTO.setShippingAddress(toAddressResponse(order.getAddress()));

        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .toList();

        responseDTO.setItems(items);
        responseDTO.setItemCount(items.stream().mapToInt(OrderItemResponseDTO::getQuantity).sum());
        responseDTO.setSubtotal(order.getTotal());
        responseDTO.setShippingCost(BigDecimal.ZERO);
        responseDTO.setDiscount(BigDecimal.ZERO);
        responseDTO.setTotal(order.getTotal());
        return responseDTO;
    }

    private OrderCustomerResponseDTO toCustomerResponse(User user) {
        OrderCustomerResponseDTO customerResponseDTO = new OrderCustomerResponseDTO();
        customerResponseDTO.setId(user.getId());
        customerResponseDTO.setFirstName(user.getFirstName());
        customerResponseDTO.setLastName(user.getLastName());
        customerResponseDTO.setEmail(user.getMail());
        return customerResponseDTO;
    }

    private OrderAddressResponseDTO toAddressResponse(Address address) {
        OrderAddressResponseDTO addressResponseDTO = new OrderAddressResponseDTO();
        addressResponseDTO.setNameOfRecipient(address.getNameOfRecipient());
        addressResponseDTO.setStreet(address.getStreet());
        addressResponseDTO.setNumber(address.getNumber());
        addressResponseDTO.setUnitOrApt(address.getUnitOrApt());
        addressResponseDTO.setCity(address.getCity());
        addressResponseDTO.setState(address.getState());
        addressResponseDTO.setPostalCode(address.getPostalCode());
        addressResponseDTO.setCountry(address.getCountry());
        return addressResponseDTO;
    }

    private OrderItemResponseDTO toOrderItemResponse(OrderItem item) {
        Product product = item.getProduct();

        OrderItemResponseDTO itemResponseDTO = new OrderItemResponseDTO();
        itemResponseDTO.setId(item.getId());
        itemResponseDTO.setProductId(product.getId());
        itemResponseDTO.setProductName(product.getName());
        itemResponseDTO.setImageUrl(Optional.of(product.getImagesURLs())
                .filter(images -> !images.isEmpty())
                .map(images -> images.get(0))
                .orElse(null));
        itemResponseDTO.setUnitPrice(product.getPrice());
        itemResponseDTO.setQuantity(item.getQuantity());
        itemResponseDTO.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return itemResponseDTO;
    }
}
