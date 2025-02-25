package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
    private static final String db = "jdbc:mysql://localhost:3306/product-manager";
    private static final String user = "root";
    private static final String pass = "";

    static Scanner sc = new Scanner(System.in);

    public static void crearProducto(Connection conn) throws SQLException {
        System.out.print("Introduce la referencia: ");
        String referencia = sc.nextLine();

        System.out.print("Introduce el nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Introduce el precio: ");
        double precio = sc.nextDouble();

        System.out.print("Introduce la categoría: ");
        int categoria = sc.nextInt();
        sc.nextLine();

        PreparedStatement st = conn.prepareStatement("insert into product(reference, name, price, category) values (?, ?, ?, ?)");

        st.setString(1, referencia);
        st.setString(2, nombre);
        st.setDouble(3, precio);
        st.setInt(4, categoria);

        st.executeUpdate();
    }

    public static void listarProductos(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select * from product");

        System.out.printf("%5s %5s  %-40s  %8s\n", "ID", "REF", "NOMBRE", "PRECIO");
        while(rs.next()) {
            System.out.printf("%5d %5s  %-40s  %8.2f\n", 
                              rs.getInt("id"), 
                              rs.getString("reference"),
                              rs.getString("name"),
                              rs.getDouble("price"));
        }
    }

    public static void actualizarProducto(Connection conn) throws SQLException {
        listarProductos(conn);

        System.out.print("Introduce el identificador del producto que quieres actualizar: ");
        int id = sc.nextInt();
        sc.nextLine();

        PreparedStatement st = conn.prepareStatement("select * from product where id = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        rs.next();

        System.out.print("Introduce la nueva referencia (pulsa intro para no cambiar): ");
        String referencia = sc.nextLine().trim();
        referencia = referencia.isEmpty() ? rs.getString("reference") : referencia;

        System.out.print("Introduce el nuevo nombre (pulsa intro para no cambiar): ");
        String nombre = sc.nextLine().trim();
        nombre = nombre.isEmpty() ? rs.getString("name") : nombre;

        System.out.print("Introduce el nuevo precio (pon 0 para no cambiar): ");
        Double precio = sc.nextDouble();
        precio = precio == 0 ? rs.getDouble("price") : precio;

        System.out.print("Introduce la nueva categoría (pon 0 para no cambiar): ");
        int categoria = sc.nextInt();
        sc.nextLine();
        categoria = categoria == 0 ? rs.getInt("category") : categoria;

        st = conn.prepareStatement("update product set reference = ?, name = ?, price = ?, category = ? where id = ?");

        st.setString(1, referencia);
        st.setString(2, nombre);
        st.setDouble(3, precio);
        st.setInt(4, categoria);
        st.setInt(5, id);

        st.executeUpdate();
    }

    public static void borrarProducto(Connection conn) throws SQLException {
        listarProductos(conn);

        System.out.print("Introduce el identificador del producto a borrar: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        PreparedStatement st = conn.prepareStatement("delete from product where id = ?");
        st.setInt(1, id);
        if (st.executeUpdate() > 0) {
            System.out.println("Producto borrado con éxito");
        }
        else {
            System.err.println("El producto no existe");
        }
    }

    public static void main(String[] args) {
        String opcion = "";

        do {
            System.out.println();
            System.out.println("CRUD BÁSICO PRODUCT MANAGER");
            System.out.println("c: Crear Producto");
            System.out.println("r: Listar Productos");
            System.out.println("u: Actualizar Producto");
            System.out.println("d: Borrar Producto");
            System.out.println("e: Salir");
            System.out.print("Introduzca opción: ");
    
            opcion = sc.nextLine();
    
            try (Connection conn = DriverManager.getConnection(db, user, pass)) {
                switch (opcion.toLowerCase()) {
                    case "c":
                        crearProducto(conn);
                        break;
                    case "r":
                        listarProductos(conn);
                        break;
                    case "u":
                        actualizarProducto(conn);
                        break;
                    case "d":
                        borrarProducto(conn);
                        break;
                    case "e":
                        break;
                    default:
                        System.err.println("Opción no válida");
                }
            }
            catch (SQLException e) {
                System.err.println(e.getMessage());
            }

        } while(!opcion.toLowerCase().equals("e"));
    }
}
