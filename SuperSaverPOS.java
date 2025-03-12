import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



// these use for sending email revenue report
import javax.mail.*;                
import javax.mail.internet.*;


class Item {
    String code, name, sizeOrWeight, manufacturer, expiry;
    double price;
    int discount;

    public Item(String code, String name, double price, String sizeOrWeight, String manufacturer, String expiry, int discount) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.sizeOrWeight = sizeOrWeight;
        this.manufacturer = manufacturer;
        this.expiry = expiry;
        this.discount = discount;
    }
}

class Bill {
    String cashier, customer;
    List<String> items = new ArrayList<>();
    double totalCost = 0, totalDiscount = 0;
    LocalDateTime dateTime;

    public Bill(String cashier, String customer) {
        this.cashier = cashier;
        this.customer = customer;
        this.dateTime = LocalDateTime.now();
    }

    public void addItem(Item item, int quantity) {
        double itemTotal = item.price * quantity * (1 - item.discount / 100.0);
        totalCost += itemTotal;
        totalDiscount += item.price * quantity * (item.discount / 100.0);
        items.add(item.name + " (" + item.sizeOrWeight + "), " + quantity + ", " + item.discount + "%, $" + itemTotal);
    }

    public void saveAsTextFile(String filename) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true)); // 'true' to append
        writer.write("Super-Saving Supermarket\nBill Receipt\n");
        writer.write("Cashier: " + cashier + "\nCustomer: " + customer + "\n");
        writer.write("Date: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n\n");
        writer.write("Item (Size/Weight), Quantity, Discount, Final Price\n");
        for (String item : items) {
            writer.write(item + "\n");
        }
        writer.write("\nTotal Discount: $" + totalDiscount + "\nTotal Cost: $" + totalCost);
        writer.write("\n-----------------------------------------------------\n");  // Add a separator for clarity
        writer.close();
    }
}

class POS {
    static Map<String, Item> itemDatabase = new HashMap<>();
    static String revenueFile = "revenue.txt";  // File to store revenue data

    public static void loadItemsFromCSV(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine(); // Skip header
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            Item item = new Item(
                    data[0],
                    data[1],
                    Double.parseDouble(data[2]),
                    data[3],
                    data[6],
                    data[5],
                    Integer.parseInt(data[7])
            );
            itemDatabase.put(data[0], item);
        }
        br.close();
    }

    public static void savePendingBill(Bill bill, String filename) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(bill.cashier + "," + bill.customer + "\n");
        for (String item : bill.items) {
            writer.write(item + "\n");
        }
        writer.write("TotalDiscount," + bill.totalDiscount + "\nTotalCost," + bill.totalCost + "\n");
        writer.close();
    }

    public static Bill loadPendingBill(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String[] header = br.readLine().split(",");
        Bill bill = new Bill(header[0], header[1]);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("TotalDiscount")) {
                bill.totalDiscount = Double.parseDouble(line.split(",")[1]);
            } else if (line.startsWith("TotalCost")) {
                bill.totalCost = Double.parseDouble(line.split(",")[1]);
            } else {
                bill.items.add(line);
            }
        }
        br.close();
        return bill;
    }

    // New method to save revenue data
    public static void saveRevenue(Bill bill) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(revenueFile, true));
        writer.write(bill.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " | Total Cost: $" + bill.totalCost + "\n");
        writer.close();
    }

    // New method to generate revenue report between two dates
    public static void generateRevenueReport(String fromDate, String toDate) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(revenueFile));
        String line;
        double totalRevenue = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");  // Correct format with time

        LocalDateTime start = LocalDateTime.parse(fromDate + " 00:00", formatter); // Assume "00:00" time for start date
        LocalDateTime end = LocalDateTime.parse(toDate + " 23:59", formatter);   // Assume "23:59" time for end date

        BufferedWriter reportWriter = new BufferedWriter(new FileWriter("revenue_report_summary.txt", false)); // New summary txt file
        reportWriter.write("--- Revenue Report (" + fromDate + " to " + toDate + ") ---\n");

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(" \\| "); // Split by the delimiter used in saveRevenue
            if (data.length < 2) continue;

            String dateStr = data[0];
            double revenue = Double.parseDouble(data[1].split(": ")[1].trim().replace("$", ""));

            LocalDateTime date = LocalDateTime.parse(dateStr, formatter); // Parse using correct format with time
            if (!date.isBefore(start) && !date.isAfter(end)) {
                totalRevenue += revenue;
                reportWriter.write("Date: " + dateStr + " | Revenue: $" + revenue + "\n");
            }
        }
        reportWriter.write("\nTotal Revenue: $" + totalRevenue + "\n");
        reportWriter.close();
        reader.close();
    }

    public static void sendEmailWithReport(String reportFilePath) throws Exception {
        final String username = "sangeethk.23@cse.mrt.ac.lk"; // replace with your email
        final String password = "glse mciy sfce lqul"; // App password (not your email password)
    
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    
        Session session = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
    
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO,
              
                InternetAddress.parse("salesteam@supersaving.lk"));
        message.setSubject("Super-Saving Revenue Report");
    
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Please find the attached revenue report.");
    
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(new File(reportFilePath));
    
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);
    
        message.setContent(multipart);
        Transport.send(message);
    
        System.out.println("Report sent via email.");
    }
}


public class SuperSaverPOS {
    public static void main(String[] args) {
        try {
            POS.loadItemsFromCSV("super_saving_items.csv");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nWelcome to SuperSaverPOS!");
                System.out.println("1. Start a New Bill");
                System.out.println("2. Load a Pending Bill");
                System.out.println("3. Generate Revenue Report");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                Bill bill = null;

                if (choice.equals("1")) {
                    // Start a new bill
                    System.out.print("Enter cashier name: ");
                    String cashier = scanner.nextLine();
                    System.out.print("Enter customer name (or leave blank): ");
                    String customer = scanner.nextLine();
                    bill = new Bill(cashier, customer);
                } else if (choice.equals("2")) {
                    // Load a pending bill
                    try {
                        System.out.println("Loading pending bill...");
                        bill = POS.loadPendingBill("pending_bill.csv");
                        System.out.println("Pending bill loaded:");
                        System.out.println("Cashier: " + bill.cashier);
                        System.out.println("Customer: " + bill.customer);
                        System.out.println("Date: " + bill.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                        System.out.println("Existing Items:");
                        for (String item : bill.items) {
                            System.out.println(item);
                        }
                        System.out.println("\nTotal Discount: $" + bill.totalDiscount);
                        System.out.println("Total Cost: $" + bill.totalCost);
                    } catch (Exception e) {
                        System.out.println("No pending bill found or error loading the pending bill.");
                        continue;
                    }
                } else if (choice.equals("3")) {
                    // Generate Revenue Report
                    System.out.print("Enter start date (yyyy-MM-dd): ");
                    String fromDate = scanner.nextLine();
                    System.out.print("Enter end date (yyyy-MM-dd): ");
                    String toDate = scanner.nextLine();
                    try {
                        POS.generateRevenueReport(fromDate, toDate);
                        System.out.println("Revenue report generated successfully!");
                        POS.sendEmailWithReport("revenue_report_summary.txt");
                    } catch (Exception e) {
                        System.out.println("Error generating the revenue report.");
                        e.printStackTrace();
                    }
                    continue;
                } else if (choice.equals("4")) {
                    // Exit the program
                    System.out.println("Exiting SuperSaverPOS. Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice! Please select a valid option.");
                    continue;
                }

                // Process bill items (this will happen after loading or creating a bill)
                while (true) {
                    System.out.print("Enter item code (or 'done' to finish, 'save' to pause): ");
                    String code = scanner.nextLine();  // this should consume the newline character

                    if (code.equals("done")) break;
                    if (code.equals("save")) {
                        POS.savePendingBill(bill, "pending_bill.csv");
                        System.out.println("Bill saved as pending.");
                        break;
                    }

                    if (!POS.itemDatabase.containsKey(code)) {
                        System.out.println("Item not found!");
                        continue;
                    }

                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();  // Consume the leftover newline character after nextInt()
                    bill.addItem(POS.itemDatabase.get(code), quantity);
                }

                // Save the bill when done and save revenue
                bill.saveAsTextFile("bill.txt");
                POS.saveRevenue(bill); // Save revenue
                System.out.println("Bill saved as bill.txt");
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
