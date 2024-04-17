import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public class Product{
        private String productId;
        private String productName;
        private int inventoryCount;
        private int tempInventoryCount;

        public Product(String productId, String productName, int inventoryCount){
            this.productId = productId;
            this.productName = productName;
            this.inventoryCount = inventoryCount;
            this.tempInventoryCount = inventoryCount;
        }

        public String getProductId(){
            return productId;
        }
        public String getProductName(){
            return productName;
        }
        public int getInventoryCount(){
            return inventoryCount;
        }
        public void setInventoryCount(int inventoryCount){
            this.inventoryCount = inventoryCount;
        }

        public int getTempInventoryCount(){
            return tempInventoryCount;
        }

        public void setTempInventoryCount(int n){
            this.tempInventoryCount = n;
        }
    }

    public class Order{
        private List<String> productIds;
        private List<Integer> quantityOrdered;
        private String orderId;

        public Order(List<String> productIds, List<Integer> quantityOrdered, String orderId){
            this.productIds = productIds;
            this.quantityOrdered = quantityOrdered;
            this.orderId = orderId;
        }

        public List<String> getProductIdList(){
            return productIds;
        }

        public List<Integer> getQuantityOrderedList(){
            return quantityOrdered;
        }
        public String getOrderId(){
            return orderId;
        }
    }

    public class ProductRepository{
        Map<String, Product> productMap;
        public ProductRepository(){
            this.productMap = new HashMap<>();
        }

        public Map<String, Product> getProductMap(){
            return productMap;
        }

        public void addProduct(Product product){
            productMap.put(product.getProductId(), product);
        }

        public void reduceInventoryCount(Product product, int reduceBy){
            int currentCount = product.getInventoryCount();
            currentCount = currentCount - reduceBy;
            product.setInventoryCount(currentCount);
            productMap.replace(product.getProductId(), product);
        }

        public void addInventoryCount(Product product){
            int currentCount = product.getInventoryCount();
            currentCount++;
            product.setInventoryCount(currentCount);
            productMap.replace(product.getProductId(), product);
        }

        public Product getProductById(String productId){
            return productMap.get(productId);
        }

        public void replaceProduct(Product product){
            productMap.replace(product.getProductId(), product);
        }
    }

    public class OrderRepository{
        Map<String, Order> orderMap;

        public OrderRepository(){
            this.orderMap = new HashMap<>();
        }

        public Map<String, Order> getOrderMap(){
            return orderMap;
        }

        public void addNewOrder(Order order){
            orderMap.put(order.getOrderId(), order);
        }

        public Order getOrderById(String orderId){
            return orderMap.get(orderId);
        }

    }

    public class ECommerceService{
        OrderRepository orderRepository;
        ProductRepository productRepository;

        public ECommerceService(OrderRepository orderRepository, ProductRepository productRepository){
            this.orderRepository = orderRepository;
            this.productRepository = productRepository;
        }


        public void createProduct(String productId, String productName, int inventoryCount){
            Product product = new Product(productId, productName, inventoryCount);
            productRepository.addProduct(product);
            System.out.println("Product added : " + productId + " " + productName + " " + inventoryCount);
        }

        public int getInventory(String productId){
            Product product = productRepository.getProductById(productId);
            return product.getTempInventoryCount();
        }

        public void createOrder(List<String> productIds, List<Integer> quantityOrdered, String orderId){
            Order order = new Order(productIds, quantityOrdered, orderId);
            orderRepository.addNewOrder(order);
            //reduce inventory count
            int size = productIds.size();
            List<Integer> currentInventoryCount = new ArrayList<>(size);
            List<Integer> updatedInventoryCount = new ArrayList<>(size);
            for(int i = 0; i<size; i++){
                String productId = productIds.get(i);
                Product product = productRepository.getProductById(productId);
                currentInventoryCount.add(product.getInventoryCount());
                if(currentInventoryCount.get(i)<quantityOrdered.get(i)){
                    System.out.println("We dont have the required quantity in our stock");
                    updatedInventoryCount.add(product.getInventoryCount());
                    continue;
                }
                updatedInventoryCount.add(product.getInventoryCount()-quantityOrdered.get(i));
                product.setTempInventoryCount(updatedInventoryCount.get(i));
                productRepository.replaceProduct(product);
                System.out.println("Product Name : " + product.getProductName() + " and quantity left in stock is " + product.getTempInventoryCount());
            }
            System.out.println("New order will be created once payment is completed with orderId " + orderId);

        }

        public void confirmOrder(String orderId){
            Order order = orderRepository.getOrderById(orderId);
            List<String> productIds = order.getProductIdList();
            List<Integer> quantityOrdered = order.getQuantityOrderedList();
            for(int i = 0; i<productIds.size(); i++){
                Product product = productRepository.getProductById(productIds.get(i));
                productRepository.reduceInventoryCount(product, quantityOrdered.get(i));
                System.out.println("Product Name : " + product.getProductName() + " and quantity left in stock is " + product.getInventoryCount());
            }
            System.out.println("Order is confirmed");
        }
    }


    public void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        OrderRepository orderRepository = new OrderRepository();

        ECommerceService eCommerceService = new ECommerceService(orderRepository, productRepository);

        eCommerceService.createProduct("1", "A1", 2);
        eCommerceService.createProduct("2", "B2", 5);
        eCommerceService.createProduct("3", "C3", 4);

        System.out.println("Inventory : " + eCommerceService.getInventory("1"));

        List<String> productIds = new ArrayList<>();
        productIds.add("2");
        productIds.add("3");

        List<Integer> quantityOrdered = new ArrayList<>();
        quantityOrdered.add(4);
        quantityOrdered.add(4);

        eCommerceService.createOrder(productIds, quantityOrdered, "1");
        eCommerceService.confirmOrder("1");

        List<String> productIds2 = new ArrayList<>();
        productIds2.add("3");
        productIds2.add("2");

        List<Integer> qe = new ArrayList<>();
        qe.add(2);
        qe.add(1);

        eCommerceService.createOrder(productIds2, qe, "2");


    }
}