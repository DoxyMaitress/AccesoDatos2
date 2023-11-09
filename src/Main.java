
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try {
            // Permitir al usuario elegir entre MySQL y PostgreSQL
            Scanner scanner = new Scanner(System.in);
            System.out.print("Elija la base de datos (mysql/postgresql): ");
            String dbType = scanner.nextLine().toLowerCase();

            String dbUrl, dbUser, dbPassword;

            if (dbType.equals("mysql")) {
                dbUrl = "jdbc:mysql://localhost:3306/perro2";
                dbUser = "root";
                dbPassword = "password";
            } else if (dbType.equals("postgresql")) {
                dbUrl = "jdbc:postgresql://localhost:5432/perro2";
                dbUser = "postgres";
                dbPassword = "password";
            } else {
                System.out.println("Tipo de base de datos no válido. Saliendo.");
                return;
            }

            BaseDeDatos conexionDB = new BaseDeDatos(dbUrl, dbUser, dbPassword);
            Connection connection = conexionDB.getConnection();
            while (true) {
                mostrarMenu();
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                switch (opcion) {
                    case 1:
                        agregarPerro(connection);
                        break;
                    case 2:
                        modificarPerro(connection);
                        break;
                    case 3:
                        eliminarPerro(connection);
                        break;
                    case 4:
                        mostrarTodosLosPerros(connection);
                        break;
                    case 5:
                        busquedaDePerros(connection);
                        break;
                    case 6:
                        busquedaConFiltrado(connection);
                        break;
                    case 7:
                        recuperarUltimoElementoBorrado(connection);
                        break;
                    case 8:
                        eliminarTodosLosDatos(connection);
                        break;
                    case 9:
                        System.out.println("¡Hasta luego!");
                        System.exit(0);
                        break; // Agrega un break para salir del switch
                    default:
                        System.out.println("Opción no válida. Por favor, elija una opción del menú.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("Menú de Opciones:");
        System.out.println("1. Agregar Perro");
        System.out.println("2. Modificar Perro");
        System.out.println("3. Eliminar Perro");
        System.out.println("4. Mostrar Todos los Perros");
        System.out.println("5. Búsqueda de Perros por Nombre");
        System.out.println("6. Búsqueda con Filtrado (Raza y Edad)");
        System.out.println("7. Recuperar Último Perro Eliminado");
        System.out.println("8. Eliminar Todos los Datos del Programa");
        System.out.println("9. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void agregarPerro(Connection connection) {
        try {
            // Implementa la lógica para agregar un perro a la base de datos
            Scanner scanner = new Scanner(System.in);
            System.out.print("Nombre del perro: ");
            String nombre = scanner.nextLine();
            System.out.print("Raza: ");
            String raza = scanner.nextLine();
            System.out.print("Edad: ");
            int edad = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea
            System.out.print("Color: ");
            String color = scanner.nextLine();
            System.out.print("Dueño: ");
            String dueño = scanner.nextLine();
            System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
            String fechaNacimiento = scanner.nextLine();

            String sql = "INSERT INTO perros (nombre, raza, edad, color, dueño, fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nombre);
            statement.setString(2, raza);
            statement.setInt(3, edad);
            statement.setString(4, color);
            statement.setString(5, dueño);
            statement.setString(6, fechaNacimiento);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Perro agregado correctamente.");
            } else {
                System.out.println("No se pudo agregar el perro.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void modificarPerro(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese el ID del perro a modificar: ");
            int perroId = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            // Verificar si el perro está marcado como eliminado
            String checkSql = "SELECT * FROM perros WHERE id = ? AND eliminado = TRUE";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, perroId);
            ResultSet checkResult = checkStatement.executeQuery();

            if (checkResult.next()) {
                System.out.println("No se puede modificar un perro marcado como eliminado.");
            } else {
                // Si el perro no está eliminado, proceder con la modificación
                System.out.print("Nuevo nombre del perro: ");
                String nombre = scanner.nextLine();
                System.out.print("Nueva raza: ");
                String raza = scanner.nextLine();
                System.out.print("Nueva edad: ");
                int edad = scanner.nextInt();
                scanner.nextLine();  // Consumir el salto de línea
                System.out.print("Nuevo color: ");
                String color = scanner.nextLine();
                System.out.print("Nuevo dueño: ");
                String dueño = scanner.nextLine();
                System.out.print("Nueva fecha de nacimiento (YYYY-MM-DD): ");
                String fechaNacimiento = scanner.nextLine();

                String sql = "UPDATE perros SET nombre=?, raza=?, edad=?, color=?, dueño=?, fecha_nacimiento=? WHERE id=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, nombre);
                statement.setString(2, raza);
                statement.setInt(3, edad);
                statement.setString(4, color);
                statement.setString(5, dueño);
                statement.setString(6, fechaNacimiento);
                statement.setInt(7, perroId);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Perro modificado correctamente.");
                } else {
                    System.out.println("No se pudo modificar el perro. Asegúrate de que el ID sea válido.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void eliminarPerro(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese el ID del perro a eliminar: ");
            int perroId = scanner.nextInt();

            // Verificar si el perro ya está eliminado
            String checkSql = "SELECT * FROM perros WHERE id = ? AND eliminado = TRUE";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, perroId);
            ResultSet checkResult = checkStatement.executeQuery();

            if (checkResult.next()) {
                System.out.println("El perro ya ha sido eliminado anteriormente.");
            } else {
                // Si el perro no está eliminado, marcarlo como eliminado
                String sql = "UPDATE perros SET eliminado = TRUE WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, perroId);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Perro marcado como eliminado correctamente.");
                } else {
                    System.out.println("No se pudo marcar el perro como eliminado. Asegúrate de que el ID sea válido.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static void mostrarTodosLosPerros(Connection connection) {
        try {
            String sql = "SELECT * FROM perros WHERE eliminado = FALSE"; // Agregar una condición para filtrar los perros no eliminados
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String raza = resultSet.getString("raza");
                int edad = resultSet.getInt("edad");
                String color = resultSet.getString("color");
                String dueño = resultSet.getString("dueño");
                String fechaNacimiento = resultSet.getString("fecha_nacimiento");

                System.out.println("ID: " + id);
                System.out.println("Nombre: " + nombre);
                System.out.println("Raza: " + raza);
                System.out.println("Edad: " + edad);
                System.out.println("Color: " + color);
                System.out.println("Dueño: " + dueño);
                System.out.println("Fecha de Nacimiento: " + fechaNacimiento);
                System.out.println("----------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void busquedaDePerros(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese el nombre del perro a buscar: ");
            String nombre = scanner.nextLine();

            String sql = "SELECT * FROM perros WHERE nombre LIKE ? AND eliminado = FALSE"; // Agregar condición para verificar si no está eliminado
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + nombre + "%");

            ResultSet resultSet = statement.executeQuery();

            boolean encontrado = false; // Variable para rastrear si se encontró al menos un perro

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String perroNombre = resultSet.getString("nombre");
                String raza = resultSet.getString("raza");
                int edad = resultSet.getInt("edad");
                String color = resultSet.getString("color");
                String dueño = resultSet.getString("dueño");
                String fechaNacimiento = resultSet.getString("fecha_nacimiento");

                System.out.println("ID: " + id);
                System.out.println("Nombre: " + perroNombre);
                System.out.println("Raza: " + raza);
                System.out.println("Edad: " + edad);
                System.out.println("Color: " + color);
                System.out.println("Dueño: " + dueño);
                System.out.println("Fecha de Nacimiento: " + fechaNacimiento);
                System.out.println("----------------------");

                encontrado = true; // Marcar como encontrado si al menos un perro coincide con la búsqueda
            }

            if (!encontrado) {
                System.out.println("No se ha encontrado ningún perro con ese nombre o el perro está marcado como eliminado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void busquedaConFiltrado(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese la raza para filtrar (deje en blanco para omitir): ");
            String raza = scanner.nextLine();
            System.out.print("Ingrese la edad para filtrar (deje en blanco para omitir): ");
            String edadInput = scanner.nextLine();

            String sql = "SELECT * FROM perros WHERE eliminado = FALSE";

            if (!raza.isEmpty()) {
                sql += " AND raza LIKE ?";
            }

            if (!edadInput.isEmpty()) {
                sql += " AND edad = ?";
            }

            PreparedStatement statement = connection.prepareStatement(sql);

            int parameterIndex = 1; // Índice del parámetro en la consulta SQL

            if (!raza.isEmpty()) {
                statement.setString(parameterIndex, "%" + raza + "%");
                parameterIndex++;
            }

            if (!edadInput.isEmpty()) {
                statement.setInt(parameterIndex, Integer.parseInt(edadInput));
            }

            ResultSet resultSet = statement.executeQuery();

            boolean encontrado = false; // Variable para rastrear si se encontró al menos un perro

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String perroRaza = resultSet.getString("raza");
                int edad = resultSet.getInt("edad");
                String color = resultSet.getString("color");
                String dueño = resultSet.getString("dueño");
                String fechaNacimiento = resultSet.getString("fecha_nacimiento");

                System.out.println("ID: " + id);
                System.out.println("Nombre: " + nombre);
                System.out.println("Raza: " + perroRaza);
                System.out.println("Edad: " + edad);
                System.out.println("Color: " + color);
                System.out.println("Dueño: " + dueño);
                System.out.println("Fecha de Nacimiento: " + fechaNacimiento);
                System.out.println("----------------------");

                encontrado = true; // Marcar como encontrado si al menos un perro coincide con la búsqueda
            }

            if (!encontrado) {
                System.out.println("No se ha encontrado ningún perro que coincida con los filtros o el perro está marcado como eliminado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void recuperarUltimoElementoBorrado(Connection connection) {
        try {
            String sql = "SELECT * FROM perros WHERE eliminado = TRUE ORDER BY id DESC LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String raza = resultSet.getString("raza");
                int edad = resultSet.getInt("edad");
                String color = resultSet.getString("color");
                String dueño = resultSet.getString("dueño");
                String fechaNacimiento = resultSet.getString("fecha_nacimiento");

                System.out.println("Último perro eliminado:");
                System.out.println("ID: " + id);
                System.out.println("Nombre: " + nombre);
                System.out.println("Raza: " + raza);
                System.out.println("Edad: " + edad);
                System.out.println("Color: " + color);
                System.out.println("Dueño: " + dueño);
                System.out.println("Fecha de Nacimiento: " + fechaNacimiento);

                // Ahora, actualizamos el registro marcando eliminado como 0
                String updateSql = "UPDATE perros SET eliminado = FALSE WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setInt(1, id);
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("El perro ha sido recuperado y marcado como no eliminado.");
                } else {
                    System.out.println("No se pudo marcar el perro como no eliminado. Asegúrate de que el ID sea válido.");
                }
            } else {
                System.out.println("No se encontraron registros eliminados.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void eliminarTodosLosDatos(Connection connection) {
        try {
            System.out.print("¿Está seguro de que desea eliminar todos los datos del programa? (S/N): ");
            Scanner scanner = new Scanner(System.in);
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("s")) {
                String sql = "DELETE FROM perros";
                PreparedStatement statement = connection.prepareStatement(sql);
                int rowsAffected = statement.executeUpdate();
                System.out.println(rowsAffected + " registros eliminados.");
            } else {
                System.out.println("Eliminación cancelada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
