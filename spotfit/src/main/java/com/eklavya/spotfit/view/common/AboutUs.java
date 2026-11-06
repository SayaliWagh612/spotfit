// package com.eklavya.spotfit.view.common;

// import javafx.animation.ScaleTransition;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.geometry.Rectangle2D;
// import javafx.scene.Cursor;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;
// import javafx.scene.text.Text;
// import javafx.scene.text.TextFlow;
// import javafx.stage.Screen;
// import javafx.util.Duration;

// public class AboutUs {

//     // =========================================================
//     // COLORS
//     // =========================================================

//     private static final String BG_PAGE = "#FFF8F2";
//     private static final String PRIMARY = "#D66A00";
//     private static final String PRIMARY_DARK = "#A94D00";
//     private static final String ACCENT = "#FF9F43";

//     private static final String TEXT_DARK = "#21160F";
//     private static final String TEXT_MUTED = "#75675E";

//     private static final String CARD_BG = "#FFFFFF";
//     private static final String SOFT_BG = "#FFF1E5";
//     private static final String BORDER = "#EAD8C8";

//     private static final String FONT_NAME = "Segoe UI";

//     // =========================================================
//     // IMAGES
//     // =========================================================

//     private static final String SHASHI_IMAGE =
//             "/images/About/Shashi.png";

//     private static final String SAYALI_IMAGE =
//             "/images/About/Sayali.png";

//     private static final String AARYA_IMAGE =
//             "/images/About/Aarya.png";

//     private static final String SAKSHI_IMAGE =
//             "/images/About/Sakshi.png";

//     private static final String PRATHVITA_IMAGE =
//             "/images/About/Prathvita.png";

//     private static final String VISHWAJIT_IMAGE =
//             "/images/vishwajit_virkar.jpg";

//     private static final String SACHIN_IMAGE =
//             "/images/About/Sachine.png";

//     private static final String PRAMOD_IMAGE =
//             "/images/About/Pramode.png";

//     private static final String AKSHAY_IMAGE =
//             "/images/akshay_sir.jpg";

//     private static final String SHIV_IMAGE =
//             "/images/About/Shiv.png";

//     private static final String SUBODH_IMAGE =
//             "/images/About/Subod.png";

//     // =========================================================
//     // SCENE
//     // =========================================================

//     private Scene aboutUsScene;

//     // =========================================================
//     // GET ABOUT US SCENE
//     // =========================================================

//     public Scene getAboutUsScene(
//             Runnable onBack
//     ) {

//         /*
//          * IMPORTANT:
//          *
//          * We receive the back callback from WelcomePage.
//          *
//          * When user clicks Back:
//          *
//          * AboutUs
//          *      ↓
//          * onBack.run()
//          *      ↓
//          * WelcomePage
//          */

//         BorderPane root =
//                 new BorderPane();

//         root.setStyle(
//                 "-fx-background-color: "
//                         + BG_PAGE
//                         + ";"
//         );

//         // =====================================================
//         // TOP BAR
//         // =====================================================

//         root.setTop(
//                 createTopBar(onBack)
//         );

//         // =====================================================
//         // CONTENT
//         // =====================================================

//         VBox content =
//                 new VBox(28);

//         content.setAlignment(
//                 Pos.TOP_CENTER
//         );

//         content.setPadding(
//                 new Insets(
//                         30,
//                         60,
//                         50,
//                         60
//                 )
//         );

//         // =====================================================
//         // PAGE TITLE
//         // =====================================================

//         content.getChildren().add(
//                 createPageTitle()
//         );

//         // =====================================================
//         // SHASHI SIR
//         // =====================================================

//         content.getChildren().add(
//                 createShashiSection()
//         );

//         // =====================================================
//         // PROJECT
//         // =====================================================

//         content.getChildren().add(
//                 createProjectSection()
//         );

//         // =====================================================
//         // TEAM
//         // =====================================================

//         content.getChildren().add(
//                 createTeamSection()
//         );

//         // =====================================================
//         // INSTRUCTORS
//         // =====================================================

//         content.getChildren().add(
//                 createImagePeopleSection(
//                         "Thanks to Our Instructors",
//                         new String[]{
//                                 "Sachine Patil",
//                                 "Pramod Bansode",
//                                 "Akshay Jagtap"
//                         },
//                         new String[]{
//                                 SACHIN_IMAGE,
//                                 PRAMOD_IMAGE,
//                                 AKSHAY_IMAGE
//                         }
//                 )
//         );

//         // =====================================================
//         // SUPER MENTORS
//         // =====================================================

//         content.getChildren().add(
//                 createImagePeopleSection(
//                         "Thanks to Our Super Mentors",
//                         new String[]{
//                                 "Shivkumar Tengse",
//                                 "Subodh Yelgandharwar"
//                         },
//                         new String[]{
//                                 SHIV_IMAGE,
//                                 SUBODH_IMAGE
//                         }
//                 )
//         );

//         // =====================================================
//         // MENTORS
//         // =====================================================

//         content.getChildren().add(
//                 createMentorTeamLeadSection()
//         );

//         // =====================================================
//         // FOOTER
//         // =====================================================

//         content.getChildren().add(
//                 createFooter()
//         );

//         // =====================================================
//         // SCROLL
//         // =====================================================

//         ScrollPane scrollPane =
//                 new ScrollPane(content);

//         scrollPane.setFitToWidth(
//                 true
//         );

//         scrollPane.setHbarPolicy(
//                 ScrollPane.ScrollBarPolicy.NEVER
//         );

//         scrollPane.setVbarPolicy(
//                 ScrollPane.ScrollBarPolicy.AS_NEEDED
//         );

//         scrollPane.setStyle(
//                 "-fx-background-color: "
//                         + BG_PAGE
//                         + ";" +

//                 "-fx-background: "
//                         + BG_PAGE
//                         + ";" +

//                 "-fx-border-color: transparent;"
//         );

//         root.setCenter(
//                 scrollPane
//         );

//         // =====================================================
//         // SCENE
//         // =====================================================

//         Rectangle2D bounds =
//                 Screen.getPrimary()
//                         .getVisualBounds();

//         aboutUsScene =
//                 new Scene(
//                         root,
//                         bounds.getWidth(),
//                         bounds.getHeight()
//                 );

//         return aboutUsScene;
//     }

//     // =========================================================
//     // TOP BAR
//     // =========================================================

//     private HBox createTopBar(
//             Runnable onBack
//     ) {

//         // =====================================================
//         // LOGO
//         // =====================================================

//         Label logo =
//                 new Label(
//                         "SpotFit"
//                 );

//         logo.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         27
//                 )
//         );

//         logo.setTextFill(
//                 Color.web(PRIMARY)
//         );

//         // =====================================================
//         // DOT
//         // =====================================================

//         Label dot =
//                 new Label(
//                         " ●"
//                 );

//         dot.setTextFill(
//                 Color.web(ACCENT)
//         );

//         dot.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         16
//                 )
//         );

//         // =====================================================
//         // LOGO BOX
//         // =====================================================

//         HBox logoBox =
//                 new HBox(
//                         2,
//                         logo,
//                         dot
//                 );

//         logoBox.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         // =====================================================
//         // SPACER
//         // =====================================================

//         Region spacer =
//                 new Region();

//         HBox.setHgrow(
//                 spacer,
//                 Priority.ALWAYS
//         );

//         // =====================================================
//         // BACK BUTTON
//         // =====================================================

//         Button backButton =
//                 new Button(
//                         "←  Back"
//                 );

//         backButton.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         14
//                 )
//         );

//         backButton.setStyle(
//                 "-fx-background-color: "
//                         + SOFT_BG
//                         + ";" +

//                 "-fx-text-fill: "
//                         + PRIMARY_DARK
//                         + ";" +

//                 "-fx-background-radius:20;" +

//                 "-fx-padding:9 18;" +

//                 "-fx-cursor:hand;" +

//                 "-fx-border-color:"
//                         + BORDER
//                         + ";" +

//                 "-fx-border-radius:20;"
//         );

//         // =====================================================
//         // HOVER
//         // =====================================================

//         addHoverAnimation(
//                 backButton
//         );

//         // =====================================================
//         // BACK ACTION
//         // =====================================================
//         //
//         // IMPORTANT:
//         //
//         // We DO NOT close the Stage.
//         //
//         // We call the callback supplied by WelcomePage.
//         //
//         // This returns the user to WelcomePage.
//         // =====================================================

//         backButton.setOnAction(
//                 e -> {

//                     if (onBack != null) {
//                         onBack.run();
//                     }
//                 }
//         );

//         // =====================================================
//         // TOP BAR
//         // =====================================================

//         HBox topBar =
//                 new HBox(
//                         logoBox,
//                         spacer,
//                         backButton
//                 );

//         topBar.setAlignment(
//                 Pos.CENTER_LEFT
//         );

//         topBar.setPadding(
//                 new Insets(
//                         18,
//                         45,
//                         10,
//                         45
//                 )
//         );

//         return topBar;
//     }

//     // =========================================================
//     // PAGE TITLE
//     // =========================================================

//     private VBox createPageTitle() {

//         Label small =
//                 new Label(
//                         "✦  ABOUT SPOTFIT  ✦"
//                 );

//         small.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         13
//                 )
//         );

//         small.setTextFill(
//                 Color.web(PRIMARY)
//         );

//         Label title =
//                 new Label(
//                         "Meet the People Behind SpotFit"
//                 );

//         title.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         38
//                 )
//         );

//         title.setTextFill(
//                 Color.web(TEXT_DARK)
//         );

//         Label subtitle =
//                 new Label(
//                         "Ideas • Teamwork • Learning • Innovation"
//                 );

//         subtitle.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.NORMAL,
//                         16
//                 )
//         );

//         subtitle.setTextFill(
//                 Color.web(TEXT_MUTED)
//         );

//         VBox box =
//                 new VBox(
//                         8,
//                         small,
//                         title,
//                         subtitle
//                 );

//         box.setAlignment(
//                 Pos.CENTER
//         );

//         box.setPadding(
//                 new Insets(
//                         10,
//                         0,
//                         12,
//                         0
//                 )
//         );

//         return box;
//     }

//     // =========================================================
//     // SHASHI SIR
//     // =========================================================

//     private VBox createShashiSection() {

//         Label heading =
//                 new Label(
//                         "A Special Thanks To"
//                 );

//         heading.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         14
//                 )
//         );

//         heading.setTextFill(
//                 Color.web(PRIMARY)
//         );

//         StackPane image =
//                 createLargeImageBox(
//                         SHASHI_IMAGE,
//                         "S"
//                 );

//         Label name =
//                 new Label(
//                         "Shashi Sir"
//                 );

//         name.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         27
//                 )
//         );

//         name.setTextFill(
//                 Color.web(TEXT_DARK)
//         );

//         Label organization =
//                 new Label(
//                         "Core2Web"
//                 );

//         organization.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         17
//                 )
//         );

//         organization.setTextFill(
//                 Color.web(PRIMARY)
//         );

//         Label message =
//                 new Label(
//                         "We sincerely thank Shashi Sir and Core2Web "
//                                 + "for their valuable guidance, knowledge, "
//                                 + "support and encouragement throughout "
//                                 + "our learning and project journey."
//                 );

//         message.setWrapText(
//                 true
//         );

//         message.setMaxWidth(
//                 800
//         );

//         message.setAlignment(
//                 Pos.CENTER
//         );

//         message.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.NORMAL,
//                         14
//                 )
//         );

//         message.setTextFill(
//                 Color.web(TEXT_MUTED)
//         );

//         VBox box =
//                 new VBox(
//                         13,
//                         heading,
//                         image,
//                         name,
//                         organization,
//                         message
//                 );

//         box.setAlignment(
//                 Pos.CENTER
//         );

//         box.setPadding(
//                 new Insets(35)
//         );

//         box.setMaxWidth(
//                 1200
//         );

//         applyCardStyle(
//                 box,
//                 true
//         );

//         addHoverAnimation(
//                 box
//         );

//         return box;
//     }

//     // =========================================================
//     // PROJECT
//     // =========================================================

//     private VBox createProjectSection() {

//         Label heading =
//                 new Label(
//                         "About Our Project"
//                 );

//         heading.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         25
//                 )
//         );

//         heading.setTextFill(
//                 Color.web(TEXT_DARK)
//         );

//         Label projectName =
//                 new Label(
//                         "SpotFit – Space Discovery & Booking Platform"
//                 );

//         projectName.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         19
//                 )
//         );

//         projectName.setTextFill(
//                 Color.web(PRIMARY)
//         );

//         TextFlow flow =
//                 new TextFlow(

//                         normalText(
//                                 "SpotFit – Space Discovery & Booking Platform "
//                                         + "is a "
//                         ),

//                         boldText(
//                                 "smart platform designed to make finding and "
//                                         + "booking activity-specific spaces simple, "
//                                         + "convenient, and personalized."
//                         ),

//                         normalText(
//                                 "\n\nWhether you are looking for a "
//                         ),

//                         boldText(
//                                 "coworking space, library, café, art studio, "
//                                         + "pottery workshop, music or recording studio, "
//                                         + "sports arena, gaming zone, or wellness center"
//                         ),

//                         normalText(
//                                 ", SpotFit helps you discover spaces that match "
//                                         + "your purpose, preferences, budget, location, "
//                                         + "amenities, and availability."
//                         ),

//                         headingText(
//                                 "\n\nOur Platform"
//                         ),

//                         normalText(
//                                 "\n\nSpotFit connects three important participants:"
//                         ),

//                         highlightText(
//                                 "\n\nUsers"
//                         ),

//                         normalText(
//                                 " can explore spaces based on their interests "
//                                         + "and activities, view available offerings, "
//                                         + "check availability, make bookings, "
//                                         + "complete payments, and receive a unique "
//                                         + "verification code for their reservation."
//                         ),

//                         highlightText(
//                                 "\n\nOwners"
//                         ),

//                         normalText(
//                                 " can manage their businesses, create and manage "
//                                         + "spaces, define offerings and resources, "
//                                         + "configure availability, and manage their "
//                                         + "space operations."
//                         ),

//                         highlightText(
//                                 "\n\nManagers"
//                         ),

//                         normalText(
//                                 " can manage assigned spaces, monitor bookings, "
//                                         + "verify customer bookings using verification "
//                                         + "codes, and handle check-in and check-out "
//                                         + "operations."
//                         ),

//                         headingText(
//                                 "\n\nHow SpotFit Works"
//                         ),

//                         boldText(
//                                 "\n\nDiscover  →  Explore  →  Select  →  "
//                                         + "Check Availability  →  Book  →  Pay  →  "
//                                         + "Verify  →  Check-In"
//                         ),

//                         headingText(
//                                 "\n\nOur Vision"
//                         ),

//                         normalText(
//                                 "\n\nOur vision is to create a "
//                         ),

//                         boldText(
//                                 "single, reliable, and user-friendly platform "
//                                         + "for discovering and accessing the right "
//                                         + "space for every activity."
//                         ),

//                         normalText(
//                                 " SpotFit aims to simplify space management "
//                                         + "for owners while helping users find places "
//                                         + "where they can work, learn, create, practice, "
//                                         + "relax, and connect."
//                         ),

//                         createTagline()
//                 );

//         flow.setMaxWidth(
//                 1050
//         );

//         flow.setLineSpacing(
//                 5
//         );

//         VBox card =
//                 new VBox(
//                         15,
//                         heading,
//                         projectName,
//                         flow
//                 );

//         card.setPadding(
//                 new Insets(32)
//         );

//         card.setMaxWidth(
//                 1200
//         );

//         applyCardStyle(
//                 card,
//                 false
//         );

//         return card;
//     }

//     // =========================================================
//     // TEAM
//     // =========================================================

//     private VBox createTeamSection() {

//         Label heading =
//                 new Label(
//                         "Our Team"
//                 );

//         heading.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         25
//                 )
//         );

//         heading.setTextFill(
//                 Color.web(TEXT_DARK)
//         );

//         Label subtitle =
//                 new Label(
//                         "The creative minds behind SpotFit ✨"
//                 );

//         subtitle.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.NORMAL,
//                         15
//                 )
//         );

//         subtitle.setTextFill(
//                 Color.web(TEXT_MUTED)
//         );

//         HBox row1 =
//                 new HBox(20);

//         row1.setAlignment(
//                 Pos.CENTER
//         );

//         row1.getChildren().addAll(

//                 createTeamMember(
//                         "Sayali Wagh",
//                         SAYALI_IMAGE,
//                         "S"
//                 ),

//                 createTeamMember(
//                         "Aarya Deshpande",
//                         AARYA_IMAGE,
//                         "A"
//                 ),

//                 createTeamMember(
//                         "Sakshi Tupe",
//                         SAKSHI_IMAGE,
//                         "S"
//                 )
//         );

//         HBox row2 =
//                 new HBox(20);

//         row2.setAlignment(
//                 Pos.CENTER
//         );

//         row2.getChildren().addAll(

//                 createTeamMember(
//                         "Prathvita Pichare",
//                         PRATHVITA_IMAGE,
//                         "P"
//                 ),

//                 createTeamMember(
//                         "Vishwajit Virkar",
//                         VISHWAJIT_IMAGE,
//                         "V"
//                 )
//         );

//         VBox team =
//                 new VBox(
//                         20,
//                         heading,
//                         subtitle,
//                         row1,
//                         row2
//                 );

//         team.setAlignment(
//                 Pos.CENTER
//         );

//         team.setPadding(
//                 new Insets(32)
//         );

//         team.setMaxWidth(
//                 1200
//         );

//         applyCardStyle(
//                 team,
//                 false
//         );

//         return team;
//     }

//     // =========================================================
//     // TEAM MEMBER
//     // =========================================================

//     private VBox createTeamMember(
//             String name,
//             String imagePath,
//             String initial
//     ) {

//         StackPane imageBox =
//                 createPersonImageBox(
//                         imagePath,
//                         initial
//                 );

//         Label nameLabel =
//                 new Label(
//                         name
//                 );

//         nameLabel.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         16
//                 )
//         );

//         nameLabel.setTextFill(
//                 Color.web(TEXT_DARK)
//         );

//         VBox card =
//                 new VBox(
//                         14,
//                         imageBox,
//                         nameLabel
//                 );

//         card.setAlignment(
//                 Pos.CENTER
//         );

//         card.setPrefWidth(
//                 200
//         );

//         card.setMinHeight(
//                 190
//         );

//         card.setPadding(
//                 new Insets(18)
//         );

//         card.setStyle(
//                 "-fx-background-color: "
//                         + SOFT_BG
//                         + ";" +

//                 "-fx-background-radius:18;" +

//                 "-fx-border-color:"
//                         + BORDER
//                         + ";" +

//                 "-fx-border-radius:18;"
//         );

//         addHoverAnimation(
//                 card
//         );

//         return card;
//     }

//     // =========================================================
//     // PEOPLE SECTION
//     // =========================================================

//     private VBox createImagePeopleSection(
//             String title,
//             String[] names,
//             String[] images
//     ) {

//         Label heading =
//                 new Label(
//                         title
//                 );

//         heading.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         25
//                 )
//         );

//         heading.setTextFill(
//                 Color.web(TEXT_DARK)
//         );

//         HBox row =
//                 new HBox(25);

//         row.setAlignment(
//                 Pos.CENTER
//         );

//         for (
//                 int i = 0;
//                 i < names.length;
//                 i++
//         ) {

//             row.getChildren().add(
//                     createTeamMember(
//                             names[i],
//                             images[i],
//                             names[i].substring(0, 1)
//                     )
//             );
//         }

//         VBox section =
//                 new VBox(
//                         20,
//                         heading,
//                         row
//                 );

//         section.setAlignment(
//                 Pos.CENTER
//         );

//         section.setPadding(
//                 new Insets(32)
//         );

//         section.setMaxWidth(
//                 1200
//         );

//         applyCardStyle(
//                 section,
//                 false
//         );

//         return section;
//     }

//     // =========================================================
//     // MENTORS
//     // =========================================================

//     private VBox createMentorTeamLeadSection() {

//         Label heading =
//                 new Label(
//                         "Mentors & Team Leads"
//                 );

//         heading.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         25
//                 )
//         );

//         heading.setTextFill(
//                 Color.web(TEXT_DARK)
//         );

//         Label text =
//                 new Label(
//                         "A big thank you to everyone who supported, "
//                                 + "guided and encouraged us during the development "
//                                 + "of SpotFit."
//                 );

//         text.setWrapText(
//                 true
//         );

//         text.setMaxWidth(
//                 850
//         );

//         text.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.NORMAL,
//                         15
//                 )
//         );

//         text.setTextFill(
//                 Color.web(TEXT_MUTED)
//         );

//         VBox box =
//                 new VBox(
//                         15,
//                         heading,
//                         text
//                 );

//         box.setAlignment(
//                 Pos.CENTER
//         );

//         box.setPadding(
//                 new Insets(32)
//         );

//         box.setMaxWidth(
//                 1200
//         );

//         applyCardStyle(
//                 box,
//                 false
//         );

//         return box;
//     }

//     // =========================================================
//     // FOOTER
//     // =========================================================

//     private VBox createFooter() {

//         Label title =
//                 new Label(
//                         "SpotFit"
//                 );

//         title.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         24
//                 )
//         );

//         title.setTextFill(
//                 Color.web(PRIMARY)
//         );

//         Label text =
//                 new Label(
//                         "Find Your Space. Fit Your Purpose."
//                 );

//         text.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.NORMAL,
//                         14
//                 )
//         );

//         text.setTextFill(
//                 Color.web(TEXT_MUTED)
//         );

//         VBox footer =
//                 new VBox(
//                         8,
//                         title,
//                         text
//                 );

//         footer.setAlignment(
//                 Pos.CENTER
//         );

//         footer.setPadding(
//                 new Insets(30)
//         );

//         return footer;
//     }

//     // =========================================================
//     // LARGE IMAGE
//     // =========================================================

//     private StackPane createLargeImageBox(
//             String imagePath,
//             String initial
//     ) {

//         StackPane box =
//                 new StackPane();

//         box.setPrefSize(
//                 180,
//                 180
//         );

//         ImageView imageView =
//                 loadImage(
//                         imagePath,
//                         160,
//                         160
//                 );

//         if (imageView != null) {

//             box.getChildren().add(
//                     imageView
//             );

//         } else {

//             Circle circle =
//                     new Circle(
//                             75,
//                             Color.web(SOFT_BG)
//                     );

//             Label label =
//                     new Label(
//                             initial
//                     );

//             label.setFont(
//                     Font.font(
//                             FONT_NAME,
//                             FontWeight.BOLD,
//                             40
//                     )
//             );

//             label.setTextFill(
//                     Color.web(PRIMARY)
//             );

//             box.getChildren().addAll(
//                     circle,
//                     label
//             );
//         }

//         return box;
//     }

//     // =========================================================
//     // PERSON IMAGE
//     // =========================================================

//     private StackPane createPersonImageBox(
//             String imagePath,
//             String initial
//     ) {

//         StackPane box =
//                 new StackPane();

//         box.setPrefSize(
//                 130,
//                 130
//         );

//         ImageView imageView =
//                 loadImage(
//                         imagePath,
//                         115,
//                         115
//                 );

//         if (imageView != null) {

//             box.getChildren().add(
//                     imageView
//             );

//         } else {

//             Circle circle =
//                     new Circle(
//                             55,
//                             Color.web(SOFT_BG)
//                     );

//             Label label =
//                     new Label(
//                             initial
//                     );

//             label.setFont(
//                     Font.font(
//                             FONT_NAME,
//                             FontWeight.BOLD,
//                             32
//                     )
//             );

//             label.setTextFill(
//                     Color.web(PRIMARY)
//             );

//             box.getChildren().addAll(
//                     circle,
//                     label
//             );
//         }

//         return box;
//     }

//     // =========================================================
//     // IMAGE LOADER
//     // =========================================================

//     private ImageView loadImage(
//             String path,
//             double width,
//             double height
//     ) {

//         try {

//             var stream =
//                     AboutUs.class.getResourceAsStream(
//                             path
//                     );

//             if (stream == null) {
//                 return null;
//             }

//             Image image =
//                     new Image(stream);

//             ImageView imageView =
//                     new ImageView(image);

//             imageView.setFitWidth(
//                     width
//             );

//             imageView.setFitHeight(
//                     height
//             );

//             imageView.setPreserveRatio(
//                     true
//             );

//             return imageView;

//         } catch (Exception e) {

//             return null;
//         }
//     }

//     // =========================================================
//     // TEXT HELPERS
//     // =========================================================

//     private Text normalText(
//             String text
//     ) {

//         Text t =
//                 new Text(text);

//         t.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.NORMAL,
//                         15
//                 )
//         );

//         t.setFill(
//                 Color.web(TEXT_DARK)
//         );

//         return t;
//     }

//     private Text boldText(
//             String text
//     ) {

//         Text t =
//                 new Text(text);

//         t.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         15
//                 )
//         );

//         t.setFill(
//                 Color.web(TEXT_DARK)
//         );

//         return t;
//     }

//     private Text headingText(
//             String text
//     ) {

//         Text t =
//                 new Text(text);

//         t.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         18
//                 )
//         );

//         t.setFill(
//                 Color.web(PRIMARY_DARK)
//         );

//         return t;
//     }

//     private Text highlightText(
//             String text
//     ) {

//         Text t =
//                 new Text(text);

//         t.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         16
//                 )
//         );

//         t.setFill(
//                 Color.web(PRIMARY)
//         );

//         return t;
//     }

//     private Text createTagline() {

//         Text tagline =
//                 new Text(
//                         "\n\nSpotFit — Find Your Space. Fit Your Purpose."
//                 );

//         tagline.setFont(
//                 Font.font(
//                         FONT_NAME,
//                         FontWeight.BOLD,
//                         17
//                 )
//         );

//         tagline.setFill(
//                 Color.web(PRIMARY)
//         );

//         return tagline;
//     }

//     // =========================================================
//     // CARD STYLE
//     // =========================================================

//     private void applyCardStyle(
//             VBox box,
//             boolean special
//     ) {

//         box.setStyle(
//                 "-fx-background-color: "
//                         + CARD_BG
//                         + ";" +

//                 "-fx-background-radius:22;" +

//                 "-fx-border-color:"
//                         + BORDER
//                         + ";" +

//                 "-fx-border-radius:22;"
//         );
//     }

//     // =========================================================
//     // HOVER ANIMATION
//     // =========================================================

//     private void addHoverAnimation(
//             javafx.scene.Node node
//     ) {

//         node.setCursor(
//                 Cursor.HAND
//         );

//         ScaleTransition scaleUp =
//                 new ScaleTransition(
//                         Duration.millis(150),
//                         node
//                 );

//         scaleUp.setToX(
//                 1.02
//         );

//         scaleUp.setToY(
//                 1.02
//         );

//         ScaleTransition scaleDown =
//                 new ScaleTransition(
//                         Duration.millis(150),
//                         node
//                 );

//         scaleDown.setToX(
//                 1.0
//         );

//         scaleDown.setToY(
//                 1.0
//         );

//         node.setOnMouseEntered(
//                 e -> scaleUp.playFromStart()
//         );

//         node.setOnMouseExited(
//                 e -> scaleDown.playFromStart()
//         );
//     }
// }

package com.eklavya.spotfit.view.common;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AboutUs {

        // =========================================================
        // COLORS
        // =========================================================

        private static final String BG = "#050608";
        private static final String CARD = "#101419";
        private static final String CARD_2 = "#151A20";
        private static final String CARD_3 = "#1A2027";

        private static final String ORANGE = "#FF7900";
        private static final String ORANGE_LIGHT = "#FF9B3D";
        private static final String ORANGE_DARK = "#B84E00";

        private static final String WHITE = "#FFFFFF";
        private static final String TEXT = "#F1F1F1";
        private static final String MUTED = "#A6ADB5";
        private static final String MUTED_DARK = "#737A83";

        private static final String BORDER = "#292F36";
        private static final String BORDER_ORANGE = "#713300";

        private static final String FONT = "Segoe UI";

        // =========================================================
        // RESOURCE PATHS
        // =========================================================

        private static final String OWNER_BACKGROUND = "/images/owner_background.png";

        private static final String SHASHI_IMAGE = "/images/About/Shashi.png";

        private static final String SAYALI_IMAGE = "/images/About/Sayali.png";

        private static final String AARYA_IMAGE = "/images/About/Aarya.png";

        private static final String SAKSHI_IMAGE = "/images/About/Sakshi.png";

        private static final String PRATHVITA_IMAGE = "/images/About/Prathvita.png";

        private static final String VISHWAJIT_IMAGE = "/images/vishwajit_virkar.jpg";

        private static final String SACHIN_IMAGE = "/images/About/Sachine.png";

        private static final String PRAMOD_IMAGE = "/images/About/Pramode.png";

        private static final String AKSHAY_IMAGE = "/images/akshay_sir.jpg";

        private static final String SHIV_IMAGE = "/images/About/Shiv.png";

        private static final String SUBODH_IMAGE = "/images/About/Subod.png";

        // =========================================================
        // SCENE
        // =========================================================

        private Scene aboutUsScene;

        // =========================================================
        // STANDALONE TEST
        // =========================================================

        public void start(Stage stage) {

                Scene scene = getAboutUsScene(stage::close);

                stage.setTitle("SpotFit - About Us");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
        }

        // =========================================================
        // GET ABOUT US SCENE
        // =========================================================

        public Scene getAboutUsScene(Runnable onBack) {

                StackPane mainRoot = new StackPane();

                mainRoot.setStyle(
                                "-fx-background-color:" + BG + ";");

                // -----------------------------------------------------
                // Background image
                // -----------------------------------------------------

                ImageView background = loadImage(
                                OWNER_BACKGROUND,
                                1600,
                                1000);

                if (background != null) {

                        background.fitWidthProperty().bind(
                                        mainRoot.widthProperty());

                        background.fitHeightProperty().bind(
                                        mainRoot.heightProperty());

                        background.setPreserveRatio(false);
                        background.setOpacity(0.38);
                }

                // -----------------------------------------------------
                // Dark overlay
                // -----------------------------------------------------

                Rectangle darkOverlay = new Rectangle();

                darkOverlay.widthProperty().bind(
                                mainRoot.widthProperty());

                darkOverlay.heightProperty().bind(
                                mainRoot.heightProperty());

                darkOverlay.setFill(
                                Color.web(
                                                "#030406",
                                                0.72));

                // -----------------------------------------------------
                // Page
                // -----------------------------------------------------

                BorderPane page = new BorderPane();

                page.setStyle(
                                "-fx-background-color:transparent;");

                page.setTop(
                                createTopBar(onBack));

                // -----------------------------------------------------
                // Main scroll content
                // -----------------------------------------------------

                VBox content = new VBox(24);

                content.setAlignment(Pos.TOP_CENTER);

                content.setPadding(
                                new Insets(
                                                20,
                                                55,
                                                55,
                                                55));

                content.getChildren().addAll(
                                createHero(),
                                createShashiSection(),
                                createProjectCard(),
                                createTeamCard(),
                                createVisionSection(),
                                createImagePeopleSection(
                                                "Thanks to Our Instructors",
                                                new String[] {
                                                                "Sachine Patil",
                                                                "Pramod Bansode",
                                                                "Akshay Jagtap"
                                                },
                                                new String[] {
                                                                SACHIN_IMAGE,
                                                                PRAMOD_IMAGE,
                                                                AKSHAY_IMAGE
                                                }),
                                createImagePeopleSection(
                                                "Thanks to Our Super Mentors",
                                                new String[] {
                                                                "Shivkumar Tengse",
                                                                "Subodh Yelgandharwar"
                                                },
                                                new String[] {
                                                                SHIV_IMAGE,
                                                                SUBODH_IMAGE
                                                }),
                                createMentorTeamLeadSection(),
                                createFooter());

                ScrollPane scrollPane = new ScrollPane(content);

                scrollPane.setFitToWidth(true);
                scrollPane.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setPannable(true);

                scrollPane.setStyle(
                                "-fx-background-color:transparent;" +
                                                "-fx-background:transparent;" +
                                                "-fx-border-color:transparent;");

                page.setCenter(scrollPane);

                if (background != null) {
                        mainRoot.getChildren().add(background);
                }

                mainRoot.getChildren().add(darkOverlay);
                mainRoot.getChildren().add(page);

                Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

                aboutUsScene = new Scene(
                                mainRoot,
                                bounds.getWidth(),
                                bounds.getHeight());

                return aboutUsScene;
        }

        // =========================================================
        // TOP BAR
        // =========================================================

        private HBox createTopBar(Runnable onBack) {

                Label logo = new Label("SpotFit");

                logo.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                27));

                logo.setTextFill(Color.web(ORANGE));

                Label dot = new Label(" •");

                dot.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                21));

                dot.setTextFill(Color.web(ORANGE_LIGHT));

                HBox logoBox = new HBox(2, logo, dot);

                logoBox.setAlignment(Pos.CENTER_LEFT);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                Button back = new Button("←  Back");

                back.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                13));

                back.setTextFill(Color.web(WHITE));

                back.setStyle(
                                "-fx-background-color:#0B0E12;" +
                                                "-fx-border-color:#713300;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:20;" +
                                                "-fx-background-radius:20;" +
                                                "-fx-padding:8 19;" +
                                                "-fx-cursor:hand;");

                back.setOnAction(e -> {
                        if (onBack != null) {
                                onBack.run();
                        }
                });

                addHoverAnimation(back);

                HBox bar = new HBox(
                                logoBox,
                                spacer,
                                back);

                bar.setAlignment(Pos.CENTER_LEFT);

                bar.setPadding(
                                new Insets(
                                                17,
                                                45,
                                                12,
                                                45));

                return bar;
        }

        // =========================================================
        // HERO
        // =========================================================

        private StackPane createHero() {

                StackPane hero = new StackPane();

                hero.setPrefHeight(210);
                hero.setMaxWidth(1250);

                Rectangle background = new Rectangle();

                background.widthProperty().bind(
                                hero.widthProperty());

                background.heightProperty().bind(
                                hero.heightProperty());

                background.setFill(
                                Color.web(
                                                "#0B0E12",
                                                0.97));

                Rectangle accentLine = new Rectangle(
                                70,
                                3);

                accentLine.setFill(
                                Color.web(ORANGE));

                accentLine.setArcWidth(5);
                accentLine.setArcHeight(5);
                accentLine.setTranslateY(-78);

                Label eyebrow = new Label(
                                "ABOUT SPOTFIT");

                eyebrow.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                11));

                eyebrow.setTextFill(
                                Color.web(ORANGE_LIGHT));

                eyebrow.setStyle(
                                "-fx-background-color:#211208;" +
                                                "-fx-background-radius:20;" +
                                                "-fx-border-color:#713300;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:20;" +
                                                "-fx-padding:6 14;");

                Label title = new Label(
                                "Built for People. Designed for Purpose.");

                title.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                34));

                title.setTextFill(
                                Color.web(WHITE));

                title.setAlignment(Pos.CENTER);
                title.setTextAlignment(
                                javafx.scene.text.TextAlignment.CENTER);

                Label subtitle = new Label(
                                "Discover the people, ideas and vision behind SpotFit.");

                subtitle.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                14));

                subtitle.setTextFill(
                                Color.web(MUTED));

                subtitle.setAlignment(Pos.CENTER);

                VBox text = new VBox(
                                10,
                                eyebrow,
                                title,
                                subtitle);

                text.setAlignment(Pos.CENTER);

                hero.getChildren().addAll(
                                background,
                                accentLine,
                                text);

                hero.setStyle(
                                "-fx-background-radius:24;" +
                                                "-fx-border-color:#292F36;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:24;");

                return hero;
        }

        // =========================================================
        // SPECIAL THANKS
        // =========================================================

        private VBox createShashiSection() {

                VBox card = new VBox();

                card.setMaxWidth(1250);
                card.setPrefHeight(260);

                card.setPadding(
                                new Insets(
                                                28,
                                                40,
                                                28,
                                                40));

                card.setStyle(
                                "-fx-background-color:#13100D;" +
                                                "-fx-background-radius:24;" +
                                                "-fx-border-color:#713300;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:24;");

                HBox mainRow = new HBox(34);

                mainRow.setAlignment(Pos.CENTER_LEFT);

                // Photo
                StackPane photo = createCircularPhoto(
                                SHASHI_IMAGE,
                                145);

                // Text content
                Label heading = new Label("A SPECIAL THANKS TO");

                heading.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                12));

                heading.setTextFill(Color.web(ORANGE));

                Label name = new Label("Shashi Sir");

                name.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                31));

                name.setTextFill(Color.web(WHITE));

                Label organization = new Label("Core2Web");

                organization.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                16));

                organization.setTextFill(
                                Color.web(ORANGE_LIGHT));

                TextFlow message = new TextFlow(
                                normalText(
                                                "We sincerely thank Shashi Sir and Core2Web "
                                                                + "for their valuable guidance, knowledge, "
                                                                + "support and encouragement throughout our "
                                                                + "learning and project journey."));

                message.setMaxWidth(650);
                message.setLineSpacing(3);

                VBox details = new VBox(
                                6,
                                heading,
                                name,
                                organization,
                                message);

                details.setAlignment(
                                Pos.CENTER_LEFT);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                Rectangle line = new Rectangle(
                                55,
                                2);

                line.setFill(Color.web(ORANGE));

                mainRow.getChildren().addAll(
                                photo,
                                details,
                                spacer,
                                line);

                card.getChildren().add(mainRow);

                return card;
        }

        // =========================================================
        // PROJECT CARD
        // =========================================================

        private VBox createProjectCard() {

                Label icon = new Label("✦");

                icon.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                21));

                icon.setTextFill(Color.web(ORANGE));

                icon.setStyle(
                                "-fx-background-color:#29160A;" +
                                                "-fx-background-radius:30;" +
                                                "-fx-padding:8 13;");

                Label heading = createCardTitle("About Our Project");

                HBox headingRow = new HBox(
                                12,
                                icon,
                                heading);

                headingRow.setAlignment(
                                Pos.CENTER_LEFT);

                Label projectName = new Label(
                                "SpotFit – Space Discovery & Booking Platform");

                projectName.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                17));

                projectName.setTextFill(
                                Color.web(ORANGE_LIGHT));

                projectName.setWrapText(true);

                TextFlow intro = new TextFlow(
                                normalText(
                                                "SpotFit – Space Discovery & Booking Platform "
                                                                + "is a "),
                                boldText(
                                                "smart platform designed to make finding and "
                                                                + "booking activity-specific spaces simple, "
                                                                + "convenient, and personalized."));

                TextFlow spaces = new TextFlow(
                                normalText(
                                                "Whether you are looking for a "),
                                boldText(
                                                "coworking space, library, café, art studio, "
                                                                + "pottery workshop, music or recording studio, "
                                                                + "sports arena, gaming zone, or wellness center"),
                                normalText(
                                                ", SpotFit helps you discover spaces that "
                                                                + "match your purpose, preferences, budget, "
                                                                + "location, amenities, and availability."));

                Label platformHeading = new Label("Our Platform");

                platformHeading.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                17));

                platformHeading.setTextFill(
                                Color.web(ORANGE));

                TextFlow participants = new TextFlow(
                                normalText(
                                                "SpotFit connects three important participants:"));

                VBox userBlock = createProjectRoleBlock(
                                "Users",
                                " can explore spaces based on their interests and "
                                                + "activities, view available offerings, check "
                                                + "availability, make bookings, complete payments, "
                                                + "and receive a unique verification code for their "
                                                + "reservation.");

                VBox ownerBlock = createProjectRoleBlock(
                                "Owners",
                                " can manage their businesses, create and manage "
                                                + "spaces, define offerings and resources, configure "
                                                + "availability, and manage their space operations.");

                VBox managerBlock = createProjectRoleBlock(
                                "Managers",
                                " can manage assigned spaces, monitor bookings, "
                                                + "verify customer bookings using verification codes, "
                                                + "and handle check-in and check-out operations.");

                VBox card = new VBox(
                                16,
                                headingRow,
                                projectName,
                                intro,
                                spaces,
                                platformHeading,
                                participants,
                                userBlock,
                                ownerBlock,
                                managerBlock);

                card.setAlignment(Pos.TOP_LEFT);

                card.setPadding(new Insets(28));

                card.setMaxWidth(1250);

                applyCardStyle(card, false);

                addHoverAnimation(card);

                return card;
        }

        private VBox createProjectRoleBlock(
                        String role,
                        String description) {

                TextFlow flow = new TextFlow(
                                highlightText(role),
                                normalText(description));

                flow.setLineSpacing(2);

                VBox box = new VBox(flow);

                box.setPadding(
                                new Insets(
                                                2,
                                                0,
                                                2,
                                                0));

                return box;
        }

        // =========================================================
        // OUR TEAM
        // =========================================================

        private VBox createTeamCard() {

                Label icon = new Label("♧");

                icon.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                22));

                icon.setTextFill(Color.web(ORANGE));

                icon.setStyle(
                                "-fx-background-color:#29160A;" +
                                                "-fx-background-radius:30;" +
                                                "-fx-padding:7 12;");

                Label heading = createCardTitle("Our Team");

                HBox headingRow = new HBox(
                                12,
                                icon,
                                heading);

                headingRow.setAlignment(
                                Pos.CENTER_LEFT);

                Label subtitle = new Label(
                                "Five minds. One vision. One SpotFit.");

                subtitle.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                13));

                subtitle.setTextFill(Color.web(MUTED));

                // -----------------------------------------------------
                // Row 1: exactly 3 people
                // -----------------------------------------------------

                HBox row1 = new HBox(24);

                row1.setAlignment(Pos.CENTER);

                row1.getChildren().addAll(
                                createCompactTeamMember(
                                                "Sayali Wagh",
                                                SAYALI_IMAGE),
                                createCompactTeamMember(
                                                "Aarya Deshpande",
                                                AARYA_IMAGE),
                                createCompactTeamMember(
                                                "Sakshi Tupe",
                                                SAKSHI_IMAGE));
                HBox row2 = new HBox(24);

                row2.setAlignment(Pos.CENTER);

                row2.getChildren().addAll(
                                createCompactTeamMember(
                                                "Prathvita Pichare",
                                                PRATHVITA_IMAGE),
                                createCompactTeamMember(
                                                "Vishwajit Virkar",
                                                VISHWAJIT_IMAGE));

                VBox card = new VBox(
                                18,
                                headingRow,
                                subtitle,
                                row1,
                                row2);

                card.setAlignment(
                                Pos.TOP_CENTER);

                card.setPadding(
                                new Insets(28));

                card.setMaxWidth(1250);

                applyCardStyle(card, false);

                addHoverAnimation(card);

                return card;
        }

        // =========================================================
        // TEAM MEMBER CARD
        // =========================================================

        private VBox createCompactTeamMember(
                        String name,
                        String imagePath) {

                StackPane photo = createCircularPhoto(
                                imagePath,
                                105);

                Label nameLabel = new Label(name);

                nameLabel.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                12));

                nameLabel.setTextFill(Color.web(WHITE));
                nameLabel.setWrapText(true);
                nameLabel.setAlignment(Pos.CENTER);
                nameLabel.setMaxWidth(170);

                Label role = new Label("SpotFit Team");

                role.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                10));

                role.setTextFill(
                                Color.web(MUTED_DARK));

                VBox box = new VBox(
                                8,
                                photo,
                                nameLabel,
                                role);

                box.setAlignment(Pos.CENTER);

                box.setPrefWidth(190);
                box.setPrefHeight(235);

                box.setPadding(new Insets(15));

                box.setStyle(
                                "-fx-background-color:" + CARD_3 + ";" +
                                                "-fx-background-radius:18;" +
                                                "-fx-border-color:#2A3038;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:18;");

                addHoverAnimation(box);

                return box;
        }

        // =========================================================
        // VISION
        // =========================================================

        private VBox createVisionSection() {

                Label icon = new Label("◈");

                icon.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                22));

                icon.setTextFill(Color.web(ORANGE));

                Label heading = createCardTitle("Our Vision");

                HBox headingRow = new HBox(
                                12,
                                icon,
                                heading);

                headingRow.setAlignment(
                                Pos.CENTER);

                Label vision = new Label(
                                "To create a single, reliable, and user-friendly "
                                                + "platform for discovering and accessing the right "
                                                + "space for every activity.");

                vision.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                15));

                vision.setTextFill(Color.web(MUTED));
                vision.setWrapText(true);
                vision.setMaxWidth(850);
                vision.setAlignment(Pos.CENTER);

                HBox features = new HBox(65);

                features.setAlignment(Pos.CENTER);

                features.getChildren().addAll(
                                createVisionFeature(
                                                "⌕",
                                                "Discover",
                                                "Find the right space"),
                                createVisionFeature(
                                                "▣",
                                                "Book",
                                                "Simple & secure"),
                                createVisionFeature(
                                                "✦",
                                                "Enjoy",
                                                "Your space, your way"));

                VBox card = new VBox(
                                18,
                                headingRow,
                                vision,
                                features);

                card.setAlignment(Pos.CENTER);

                card.setPadding(new Insets(28));

                card.setMaxWidth(1250);

                applyCardStyle(card, true);

                return card;
        }

        // =========================================================
        // VISION FEATURE
        // =========================================================

        private VBox createVisionFeature(
                        String symbol,
                        String title,
                        String description) {

                Label icon = new Label(symbol);

                icon.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                18));

                icon.setTextFill(Color.web(ORANGE));

                icon.setStyle(
                                "-fx-background-color:#29160A;" +
                                                "-fx-background-radius:30;" +
                                                "-fx-padding:8 12;");

                Label titleLabel = new Label(title);

                titleLabel.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                13));

                titleLabel.setTextFill(Color.web(WHITE));

                Label desc = new Label(description);

                desc.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                10));

                desc.setTextFill(
                                Color.web(MUTED_DARK));

                VBox box = new VBox(
                                7,
                                icon,
                                titleLabel,
                                desc);

                box.setAlignment(Pos.CENTER);

                return box;
        }

        // =========================================================
        // INSTRUCTORS / SUPER MENTORS
        // =========================================================

        private VBox createImagePeopleSection(
                        String title,
                        String[] names,
                        String[] images) {

                Label icon = new Label("♟");

                icon.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                18));

                icon.setTextFill(Color.web(ORANGE));

                Label heading = createCardTitle(title);

                HBox headingRow = new HBox(
                                10,
                                icon,
                                heading);

                headingRow.setAlignment(
                                Pos.CENTER_LEFT);

                HBox row = new HBox(30);

                row.setAlignment(Pos.CENTER);

                int count = Math.min(
                                names.length,
                                images.length);

                for (int i = 0; i < count; i++) {

                        row.getChildren().add(
                                        createSmallPerson(
                                                        names[i],
                                                        images[i]));
                }

                VBox section = new VBox(
                                20,
                                headingRow,
                                row);

                section.setAlignment(
                                Pos.CENTER);

                section.setPadding(
                                new Insets(28));

                section.setMaxWidth(1250);

                applyCardStyle(section, false);

                addHoverAnimation(section);

                return section;
        }

        // =========================================================
        // SMALL PERSON
        // =========================================================

        private VBox createSmallPerson(
                        String name,
                        String imagePath) {

                StackPane photo = createCircularPhoto(
                                imagePath,
                                95);

                Label nameLabel = new Label(name);

                nameLabel.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                12));

                nameLabel.setTextFill(Color.web(WHITE));
                nameLabel.setWrapText(true);
                nameLabel.setAlignment(Pos.CENTER);
                nameLabel.setMaxWidth(160);

                VBox box = new VBox(
                                8,
                                photo,
                                nameLabel);

                box.setAlignment(Pos.CENTER);

                box.setPrefWidth(175);
                box.setPrefHeight(150);

                box.setPadding(new Insets(12));

                box.setStyle(
                                "-fx-background-color:" + CARD_3 + ";" +
                                                "-fx-background-radius:18;" +
                                                "-fx-border-color:#2A3038;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:18;");

                addHoverAnimation(box);

                return box;
        }

        // =========================================================
        // MENTORS / TEAM LEADS
        // =========================================================

        private StackPane createMentorTeamLeadSection() {

                StackPane card = new StackPane();

                card.setMaxWidth(1250);
                card.setPrefHeight(175);

                Rectangle background = new Rectangle();

                background.widthProperty().bind(
                                card.widthProperty());

                background.heightProperty().bind(
                                card.heightProperty());

                background.setFill(
                                Color.web(
                                                "#13100D",
                                                0.96));

                Circle glow = new Circle(
                                150,
                                Color.web(
                                                "#FF6500",
                                                0.07));

                glow.setTranslateX(480);

                Label icon = new Label("✦");

                icon.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                22));

                icon.setTextFill(Color.web(ORANGE));

                Label heading = createCardTitle(
                                "Mentors & Team Leads: Mansi, Dhanashree, Sumit and Harshwardhan");

                HBox headingRow = new HBox(
                                12,
                                icon,
                                heading);

                headingRow.setAlignment(
                                Pos.CENTER);

                Label text = new Label(
                                "A big thank you to everyone who supported, "
                                                + "guided and encouraged us during the development "
                                                + "of SpotFit.");

                text.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                14));

                text.setTextFill(Color.web(MUTED));
                text.setWrapText(true);
                text.setMaxWidth(720);
                text.setAlignment(Pos.CENTER);

                VBox content = new VBox(
                                12,
                                headingRow,
                                text);

                content.setAlignment(Pos.CENTER);

                card.getChildren().addAll(
                                background,
                                glow,
                                content);

                card.setStyle(
                                "-fx-background-radius:22;" +
                                                "-fx-border-color:#713300;" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:22;");

                return card;
        }

        // =========================================================
        // FOOTER
        // =========================================================

        private VBox createFooter() {

                Rectangle line = new Rectangle(
                                70,
                                2);

                line.setFill(
                                Color.web(ORANGE_DARK));

                Label logo = new Label("SpotFit •");

                logo.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                22));

                logo.setTextFill(Color.web(ORANGE));

                Label tagline = new Label(
                                "Find Your Space. Fit Your Purpose.");

                tagline.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                12));

                tagline.setTextFill(
                                Color.web(MUTED_DARK));

                VBox footer = new VBox(
                                9,
                                line,
                                logo,
                                tagline);

                footer.setAlignment(Pos.CENTER);

                footer.setPadding(
                                new Insets(
                                                20,
                                                0,
                                                10,
                                                0));

                return footer;
        }

        // =========================================================
        // CIRCULAR PHOTO
        // =========================================================

        private StackPane createCircularPhoto(
                        String imagePath,
                        double size) {

                StackPane container = new StackPane();

                container.setMinSize(
                                size + 18,
                                size + 18);

                container.setPrefSize(
                                size + 18,
                                size + 18);

                container.setMaxSize(
                                size + 18,
                                size + 18);

                // Outer dark ring
                Circle outer = new Circle(
                                size / 2 + 7);

                outer.setFill(
                                Color.web("#24150B"));

                // Orange border ring
                Circle ring = new Circle(
                                size / 2 + 3);

                ring.setFill(
                                Color.web(ORANGE));

                ImageView image = loadImage(
                                imagePath,
                                size,
                                size);

                if (image != null) {

                        Circle clip = new Circle(
                                        size / 2,
                                        size / 2,
                                        size / 2);

                        image.setClip(clip);

                        container.getChildren().addAll(
                                        outer,
                                        ring,
                                        image);

                } else {

                        // IMPORTANT:
                        // No question mark is displayed when an image is missing.
                        // The card simply shows a clean empty circular area.
                        Circle emptyPhoto = new Circle(
                                        size / 2,
                                        Color.web(CARD_3));

                        container.getChildren().addAll(
                                        outer,
                                        ring,
                                        emptyPhoto);
                }

                return container;
        }

        // =========================================================
        // IMAGE LOADER
        // =========================================================

        private ImageView loadImage(
                        String path,
                        double width,
                        double height) {

                try {

                        var stream = AboutUs.class.getResourceAsStream(path);

                        if (stream == null) {

                                System.out.println(
                                                "ABOUT US IMAGE NOT FOUND: " + path);

                                return null;
                        }

                        Image image = new Image(stream);

                        stream.close();

                        if (image.isError()) {

                                System.out.println(
                                                "ERROR READING IMAGE: " + path);

                                return null;
                        }

                        ImageView imageView = new ImageView(image);

                        imageView.setFitWidth(width);
                        imageView.setFitHeight(height);
                        imageView.setPreserveRatio(false);

                        return imageView;

                } catch (Exception e) {

                        System.out.println(
                                        "ERROR LOADING IMAGE: " + path);

                        return null;
                }
        }

        // =========================================================
        // CARD TITLE
        // =========================================================

        private Label createCardTitle(String text) {

                Label label = new Label(text);

                label.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                22));

                label.setTextFill(Color.web(WHITE));

                return label;
        }

        // =========================================================
        // TEXT HELPERS
        // =========================================================

        private Text normalText(String text) {

                Text t = new Text(text);

                t.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.NORMAL,
                                                13));

                t.setFill(Color.web(MUTED));

                return t;
        }

        private Text boldText(String text) {

                Text t = new Text(text);

                t.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                13));

                t.setFill(Color.web(TEXT));

                return t;
        }

        private Text highlightText(String text) {

                Text t = new Text(text);

                t.setFont(
                                Font.font(
                                                FONT,
                                                FontWeight.BOLD,
                                                14));

                t.setFill(Color.web(ORANGE_LIGHT));

                return t;
        }

        // =========================================================
        // CARD STYLE
        // =========================================================

        private void applyCardStyle(
                        VBox box,
                        boolean special) {

                if (special) {

                        box.setStyle(
                                        "-fx-background-color:" + CARD + ";" +
                                                        "-fx-background-radius:22;" +
                                                        "-fx-border-color:" + BORDER_ORANGE + ";" +
                                                        "-fx-border-width:1;" +
                                                        "-fx-border-radius:22;");

                } else {

                        box.setStyle(
                                        "-fx-background-color:" + CARD + ";" +
                                                        "-fx-background-radius:22;" +
                                                        "-fx-border-color:" + BORDER + ";" +
                                                        "-fx-border-width:1;" +
                                                        "-fx-border-radius:22;");
                }
        }

        // =========================================================
        // HOVER ANIMATION
        // =========================================================

        private void addHoverAnimation(Node node) {

                node.setCursor(Cursor.HAND);

                ScaleTransition scaleUp = new ScaleTransition(
                                Duration.millis(150),
                                node);

                scaleUp.setToX(1.025);
                scaleUp.setToY(1.025);

                ScaleTransition scaleDown = new ScaleTransition(
                                Duration.millis(150),
                                node);

                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);

                node.setOnMouseEntered(
                                e -> scaleUp.playFromStart());

                node.setOnMouseExited(
                                e -> scaleDown.playFromStart());
        }
}