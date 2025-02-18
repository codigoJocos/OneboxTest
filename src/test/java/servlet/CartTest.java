package servlet;

import model.Cart;
import model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class CartTest {
    private Cart cart;
    private Product product1;
    private Product product2;
    private CartServlet cartServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private ScheduledExecutorService scheduler;

    @BeforeEach
    public void setUp() throws Exception {
        cart = new Cart();
        cartServlet = new CartServlet();
        cartServlet.setCart(cart);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        scheduler = Executors.newScheduledThreadPool(1);	

        doReturn(writer).when(response).getWriter();
        
        //Product1
        product1 = new Product();
        product1.setId(1);
        product1.setName("Laptop");
        product1.setDescription("Gaming Laptop");
        product1.setAmount(1);
        ScheduledFuture<?> countdown1 = scheduler.schedule(() -> {
            System.out.println("Petition with ID: " + product1.getId() + " was deleted for inactivity.");
            cart.getProducts().remove(product1);
        }, 10, TimeUnit.SECONDS);
        product1.setCountDown(countdown1);
        
        //Product2
        product2 = new Product();
        product2.setId(2);
        product2.setName("Mouse");
        product2.setDescription("Wireless Mouse");
        product2.setAmount(2);
        ScheduledFuture<?> countdown2 = scheduler.schedule(() -> {
            System.out.println("Petition with ID: " + product2.getId() + " was deleted for inactivity.");
            cart.getProducts().remove(product2);
        }, 53, TimeUnit.SECONDS);
        product1.setCountDown(countdown2);
        
        cart.addProducts(product1);
        cart.addProducts(product2);
    }

    @Test
    public void testServletAddProduct() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Keyboard");
        when(request.getParameter("description")).thenReturn("Mechanical Keyboard");
        when(request.getParameter("amount")).thenReturn("3");

        cartServlet.doPost(request, response);
        assertTrue(stringWriter.toString().contains("A new product has been created"));
    }

    @Test
    public void testServletListProducts() throws Exception {
        when(request.getParameter("action")).thenReturn("list");
        
        cartServlet.doGet(request, response);
        String output = stringWriter.toString();
        assertTrue(output.contains("Laptop"));
        assertTrue(output.contains("Mouse"));
    }

    @Test
    public void testServletFindProductById() throws Exception {
        when(request.getParameter("action")).thenReturn("find");
        when(request.getParameter("id")).thenReturn("2");
        
        cartServlet.doGet(request, response);
        assertTrue(stringWriter.toString().contains("Wireless Mouse"));
    }

    @Test
    public void testServletRemoveProduct() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        
        cartServlet.doDelete(request, response);
        assertTrue(stringWriter.toString().contains("Petition with ID: 1 was deleted."));
    }
    
    @Test
    public void testProductAutoRemovalAfterCountdown() throws Exception {
        TimeUnit.SECONDS.sleep(11); 
        assertFalse(cart.getProducts().contains(product1));
    }
}
