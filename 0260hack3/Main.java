import java.util.*;

class AuctionItem {
    private String itemName;
    private double startingPrice;
    private double highestBid;
    private double reservePrice;
    private User highestBidder;
    private Date endTime;
    private double minBidIncrement;

    public AuctionItem(String itemName, double startingPrice, double reservePrice, Date endTime, double minBidIncrement) {
        this.itemName = itemName;
        this.startingPrice = startingPrice;
        this.reservePrice = reservePrice;
        this.highestBid = startingPrice;
        this.endTime = endTime;
        this.minBidIncrement = minBidIncrement;
    }

    public String getItemName() {
        return itemName;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public Date getEndTime() {
        return endTime;
    }

    public boolean isAuctionOpen() {
        return new Date().before(endTime);
    }

    public void placeBid(User user, double bidAmount) {
        if (!isAuctionOpen()) {
            System.out.println("Auction for " + itemName + " is closed.");
            return;
        }
        if (bidAmount >= highestBid + minBidIncrement) {
            highestBid = bidAmount;
            highestBidder = user;
            user.addBid(this, bidAmount);
            System.out.println(user.getUsername() + " placed a bid of $" + bidAmount + " on " + itemName);
        } else {
            System.out.println("Bid amount must be at least $" + (highestBid + minBidIncrement) + ". Please place a higher bid.");
        }
    }

    public boolean isReserveMet() {
        return highestBid >= reservePrice;
    }
}

class User {
    private String username;
    private String password;
    private Map<AuctionItem, Double> bids;
    private List<AuctionItem> watchlist;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.bids = new HashMap<>();
        this.watchlist = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void addBid(AuctionItem item, double amount) {
        bids.put(item, amount);
    }

    public void viewBids() {
        System.out.println("Bids placed by " + username + ":");
        if (bids.isEmpty()) {
            System.out.println("No bids placed.");
        } else {
            bids.forEach((item, amount) -> {
                System.out.println("Item: " + item.getItemName() + " - Bid: $" + amount);
            });
        }
    }

    public void addToWatchlist(AuctionItem item) {
        if (!watchlist.contains(item)) {
            watchlist.add(item);
            System.out.println(item.getItemName() + " has been added to your watchlist.");
        } else {
            System.out.println(item.getItemName() + " is already in your watchlist.");
        }
    }

    public void viewWatchlist() {
        System.out.println("Watchlist for " + username + ":");
        if (watchlist.isEmpty()) {
            System.out.println("No items in the watchlist.");
        } else {
            for (AuctionItem item : watchlist) {
                System.out.println("Item: " + item.getItemName() + " - Highest Bid: $" + item.getHighestBid() + 
                                   " - Auction ends at: " + item.getEndTime());
            }
        }
    }
}

class AuctionSystem {
    private ArrayList<AuctionItem> items;
    private ArrayList<User> users;
    private ArrayList<AuctionItem> pastAuctions;

    public AuctionSystem() {
        items = new ArrayList<>();
        users = new ArrayList<>();
        pastAuctions = new ArrayList<>();
    }

    public ArrayList<AuctionItem> getItems() {
        return items;
    }

    public void registerUser(String username, String password) {
        users.add(new User(username, password));
        System.out.println("User " + username + " registered successfully.");
    }

    public User loginUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                System.out.println("Login successful.");
                return user;
            }
        }
        System.out.println("Invalid credentials.");
        return null;
    }

    public void addItem(String itemName, double startingPrice, double reservePrice, int auctionDurationMinutes, double minBidIncrement) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, auctionDurationMinutes);
        Date endTime = calendar.getTime();
        AuctionItem newItem = new AuctionItem(itemName, startingPrice, reservePrice, endTime, minBidIncrement);
        items.add(newItem);
        System.out.println("Item " + itemName + " added to the auction with a starting price of $" + startingPrice);
    }

    public void showItems() {
        if (items.isEmpty()) {
            System.out.println("No items available for auction.");
        } else {
            System.out.println("Items available for auction:");
            for (int i = 0; i < items.size(); i++) {
                AuctionItem item = items.get(i);
                System.out.println((i + 1) + ". " + item.getItemName() + " - Highest Bid: $" + item.getHighestBid() + 
                                   " - Auction ends at: " + item.getEndTime());
            }
        }
    }

    public void placeBid(int itemIndex, User user, double bidAmount) {
        if (itemIndex >= 0 && itemIndex < items.size()) {
            AuctionItem item = items.get(itemIndex);
            item.placeBid(user, bidAmount);
        } else {
            System.out.println("Invalid item selected.");
        }
    }

    public void declareWinners() {
        if (items.isEmpty()) {
            System.out.println("No items to declare winners for.");
        } else {
            System.out.println("Auction results:");
            for (AuctionItem item : items) {
                User highestBidder = item.getHighestBidder();
                if (highestBidder != null && item.isReserveMet()) {
                    System.out.println("Item: " + item.getItemName() + " won by " + highestBidder.getUsername() +
                            " with a bid of $" + item.getHighestBid());
                    pastAuctions.add(item);
                } else if (highestBidder == null) {
                    System.out.println("Item: " + item.getItemName() + " received no bids.");
                } else {
                    System.out.println("Item: " + item.getItemName() + " did not meet the reserve price.");
                }
            }
            items.removeAll(pastAuctions);
        }
    }

    public void viewAuctionHistory() {
        if (pastAuctions.isEmpty()) {
            System.out.println("No past auctions to display.");
        } else {
            System.out.println("Past auction results:");
            for (AuctionItem item : pastAuctions) {
                System.out.println("Item: " + item.getItemName() + " - Sold for: $" + item.getHighestBid() + 
                                   " to " + item.getHighestBidder().getUsername());
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuctionSystem auctionSystem = new AuctionSystem();
        User loggedInUser = null;

        System.out.println("Welcome to the Enhanced Online Auction System!");

        while (true) {
            if (loggedInUser == null) {
                System.out.println("\n1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        auctionSystem.registerUser(username, password);
                        break;
                    case 2:
                        System.out.print("Enter username: ");
                        username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        password = scanner.nextLine();
                        loggedInUser = auctionSystem.loginUser(username, password);
                        break;
                    case 3:
                        System.out.println("Exiting the system. Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } else {
                System.out.println("\n1. Add item to auction");
                System.out.println("2. View auction items");
                System.out.println("3. Place a bid");
                System.out.println("4. View my bids");
                System.out.println("5. Add item to watchlist");
                System.out.println("6. View my watchlist");
                System.out.println("7. Declare winners");
                System.out.println("8. View auction history");
                System.out.println("9. Logout");

                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                switch (choice) {
                    case 1:
                        System.out.print("Enter item name: ");
                        String itemName = scanner.nextLine();
                        System.out.print("Enter starting price: ");
                        double startingPrice = scanner.nextDouble();
                        System.out.print("Enter reserve price: ");
                        double reservePrice = scanner.nextDouble();
                        System.out.print("Enter auction duration in minutes: ");
                        int auctionDurationMinutes = scanner.nextInt();
                        System.out.print("Enter minimum bid increment: ");
                        double minBidIncrement = scanner.nextDouble();
                        scanner.nextLine();  // Consume newline
                        auctionSystem.addItem(itemName, startingPrice, reservePrice, auctionDurationMinutes, minBidIncrement);
                        break;
                    case 2:
                        auctionSystem.showItems();
                        break;
                    case 3:
                        auctionSystem.showItems();
                        System.out.print("Select item index to bid on: ");
                        int itemIndex = scanner.nextInt() - 1;
                        System.out.print("Enter your bid amount: ");
                        double bidAmount = scanner.nextDouble();
                        auctionSystem.placeBid(itemIndex, loggedInUser, bidAmount);
                        break;
                    case 4:
                        loggedInUser.viewBids();
                        break;
                    case 5:
                        auctionSystem.showItems();
                        System.out.print("Select item index to add to watchlist: ");
                        int watchItemIndex = scanner.nextInt() - 1;
                        if (watchItemIndex >= 0 && watchItemIndex < auctionSystem.getItems().size()) {
                            loggedInUser.addToWatchlist(auctionSystem.getItems().get(watchItemIndex));
                        } else {
                            System.out.println("Invalid item selected.");
                        }
                        break;
                    case 6:
                        loggedInUser.viewWatchlist();
                        break;
                    case 7:
                        auctionSystem.declareWinners();
                        break;
                    case 8:
                        auctionSystem.viewAuctionHistory();
                        break;
                    case 9:
                        loggedInUser = null;
                        System.out.println("Logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }
}



