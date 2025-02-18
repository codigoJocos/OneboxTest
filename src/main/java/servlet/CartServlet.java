package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Cart;
import model.Product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.*;


@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);	
    private Cart cart = new Cart();


    public void init() throws ServletException {
        System.out.println("Servlet inicializado!");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        if ("list".equals(action)) {
            listOfProducts(out);
            
        } else if ("find".equals(action)) {
            findProductById(request, out);
            
        } else {
            out.println("Invalid action");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        if ("add".equals(action)) {
            addProduct(request, out);
        } else {
            out.println("Invalid action");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        removeProductById(request, out);
    }

    private void listOfProducts(PrintWriter out) {
        if (cart.getProducts().isEmpty()){
            out.println("There are no products");
        }else {
            for(Product product : cart.getProducts()) {
                out.println(product.toString());
                deleteCountDown(cart, product);
            }
        }
    }


    private void findProductById(HttpServletRequest request, PrintWriter out) {
    	try {
    		int id = Integer.parseInt(request.getParameter("id"));
    		
        	if (cart.getProducts().isEmpty()) {
                out.println("There are no products");
                
            }else {
            	boolean productFind = false;
            	
                for(Product product : cart.getProducts()) {
                    if(product.getId() == id) {
                        out.println(product.toString());
                        deleteCountDown(cart, product);
                        productFind = true;
                        break;
                    }
                }
                
                if (!productFind) {
                    out.println("There is no product with this ID.");
                }
                
            }
    	}catch(NumberFormatException excepcion){
    		out.println("Error id was not and Integer");

    	}
    }

    private void addProduct(HttpServletRequest request, PrintWriter out) {
        try {
        	 Product product = new Product();
             

             if (cart.getProducts().isEmpty()) {
                 product.setId(1);
             }else {
                 product.setId(cart.getProducts().get(cart.getProducts().size()-1).getId() + 1);
             }
             product.setName(request.getParameter("name"));
             product.setDescription(request.getParameter("description"));
             int amount = Integer.parseInt(request.getParameter("amount"));
             
             product.setAmount(amount);

             cart.addProducts(product);
             deleteCountDown(cart, product);
             
             out.println("A new product has been created");
             out.println(product.toString());
             
        }catch(NumberFormatException excepcion){
    		out.println("Error id was not and Integer");
        }
        
    }

    private void removeProductById(HttpServletRequest request, PrintWriter out) {
    	if (request.getParameter("id")==null) {
    		out.println("null");
    	}
        if (cart.getProducts().isEmpty()){
            out.println("There are no products");

        }else{
        	try {
                int idToRemove = Integer.parseInt(request.getParameter("id"));
              //Finds the product of the id and removes it
                boolean removed = false;
                for (Product product : cart.getProducts()) {
                    if(product.getId()==idToRemove) {
                        cart.getProducts().remove(product);
                        product.getCountDown().cancel(false);
                        removed = true;
                        break;
                    }
                }

                if (removed) {
                    out.println("Petition with ID: " + idToRemove + " was deleted.");
                } else {
                    out.println("There is no product with this ID.");
                }
        	}catch(NumberFormatException excepcion) {
        		out.println("Error id was not and Integer");
        		
        	}
            
        }
    }

    //Any petition will be deleted in 10 minutes
    private void deleteCountDown(Cart products, Product product) {
        // Stop count down
        if (product.getCountDown() != null) {
            product.getCountDown().cancel(false); // Cancela la tarea programada
        }

        // Start count down 10 MINUTES
        ScheduledFuture<?> tarea = scheduler.schedule(() -> {
            System.out.println("Petition with ID: " + product.getId() + " was deleted for inactivity.");
            products.getProducts().remove(product);
        }, 10, TimeUnit.MINUTES);

        product.setCountDown(tarea);
    }
    
    protected void setCart(Cart cart) {
    	this.cart = cart;
    }
}