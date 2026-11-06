package com.eklavya.spotfit.view.owner;


// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.control.Label;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.VBox;

// public class Sidebar {

//     public static VBox build(String activeKey) {
//         VBox root = new VBox();
//         root.setPrefWidth(250);
//         root.setMinWidth(250);
//         root.setStyle("-fx-background-color:" + OwnerUi.SURFACE + "; -fx-border-color: transparent "
//                 + OwnerUi.CARD_BORDER + " transparent transparent; -fx-border-width:0 1 0 0;");

//         HBox brand = new HBox(8);
//         brand.setPadding(new Insets(24, 20, 20, 20));
//         brand.setAlignment(Pos.CENTER_LEFT);
//         Label logo = new Label("SpotFit Owner");
//         logo.setStyle("-fx-font-size:20px; -fx-font-weight:800; -fx-text-fill:" + OwnerUi.PRIMARY_CONTAINER + ";");
//         brand.getChildren().add(logo);

//         VBox nav = new VBox(4);
//         nav.setPadding(new Insets(4, 12, 12, 12));

//         String[][] items = {
//                 {"Dashboard", "dashboard"},
//                 {"My Spaces", "myspaces"},
//                 {"Bookings", "bookings"},
//                 {"Complaints", "complaints"},
//                 {"Customers", "crm"},
//                 {"Analytics", "analytics"},
//                 {"Revenue & Payouts", "revenue"},
//                 {"Staff / Managers", "staff"},
//                 {"Reviews", "reviews"},
//                 {"Promotions", "promotions"},
//                 {"Notifications", "notifications"},
//                 {"My Profile", "profile"},
//                 {"Settings", "settings"},
//                 {"Help Center", "help"}
//         };

//         for (String[] item : items) {
//             nav.getChildren().add(navItem(item[0], item[1], item[1].equals(activeKey)));
//         }

//         VBox spacer = new VBox();
//         VBox.setVgrow(spacer, Priority.ALWAYS);

//         Label logout = new Label("  Log Out");
//         logout.setMaxWidth(Double.MAX_VALUE);
//         logout.setPrefHeight(44);
//         logout.setStyle("-fx-font-size:14px; -fx-font-weight:700; -fx-text-fill:" + OwnerUi.ERROR
//                 + "; -fx-padding:0 16; -fx-cursor:hand;");
//         logout.setOnMouseClicked(e -> OwnerUi.show(new SignInPage().getScene()));

//         VBox bottom = new VBox(logout);
//         bottom.setPadding(new Insets(12));

//         root.getChildren().addAll(brand, nav, spacer, bottom);
//         return root;
//     }

//     private static Label navItem(String text, String key, boolean active) {
//         Label l = new Label("  " + text);
//         l.setMaxWidth(Double.MAX_VALUE);
//         l.setPrefHeight(42);
//         String base = "-fx-font-size:13.5px; -fx-font-weight:700; -fx-background-radius:12; -fx-padding:0 14; -fx-cursor:hand;";
//         if (active) {
//             l.setStyle(base + " -fx-background-color:" + OwnerUi.PRIMARY_LIGHT + "; -fx-text-fill:" + OwnerUi.PRIMARY_CONTAINER + ";");
//         } else {
//             l.setStyle(base + " -fx-text-fill:" + OwnerUi.TEXT_MUTED + ";");
//         }
//         l.setOnMouseClicked(e -> OwnerUi.go(key));
//         return l;
//     }
// }

