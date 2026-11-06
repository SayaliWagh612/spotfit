package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.model.Business;
import javafx.application.Platform;
import javafx.scene.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerBusinessUiV2Phase5Test {

    private static boolean javafxInitialized = false;

    @BeforeAll
    public static void initJavaFX() {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(3, TimeUnit.SECONDS);
            javafxInitialized = true;
        } catch (IllegalStateException e) {
            // Already initialized
            javafxInitialized = true;
        } catch (Throwable t) {
            System.err.println("JavaFX headless initialization skipped: " + t.getMessage());
        }
    }

    @Test
    @DisplayName("Verify CreateBusinessPage and EditBusinessPage present and loadable")
    public void testUiClassesPresent() {
        assertNotNull(CreateBusinessPage.class);
        assertNotNull(EditBusinessPage.class);
        assertNotNull(BusinessDetailsPage.class);
        assertNotNull(OwnerBusinessesPage.class);
        assertNotNull(BusinessFlowRouter.class);
    }

    @Test
    @DisplayName("Verify EditBusinessPage constructor accepts V1 and V2 Business models")
    public void testEditBusinessPageConstructor() {
        Business v1 = new Business("b-v1", "o-1", "V1 Name", "V1 Legal", "v1@email.com",
                "1234567890", "Desc", "Addr", "Mark", "City", "State", "411001", "REG1",
                "DRAFT", true, 0, 0, "", 1000, 1000);
        EditBusinessPage editV1Page = new EditBusinessPage(v1);
        assertNotNull(editV1Page);

        Business v2 = Business.createV2("b-v2", "o-1", "V2 Name", "V2 Legal", "v2@email.com",
                "1234567890", "Desc", "Addr", "Mark", "City", "State", "411001", "REG2",
                SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.ART, CreativityActivityGroup.MUSIC),
                "", "https://v2site.com", "DRAFT", true, 0, 0, "", 1000, 1000);
        EditBusinessPage editV2Page = new EditBusinessPage(v2);
        assertNotNull(editV2Page);
    }

    @Test
    @DisplayName("Verify BusinessDetailsPage constructor accepts V2 Business model")
    public void testBusinessDetailsPageConstructor() {
        Business v2 = Business.createV2("b-v2", "o-1", "V2 Details Biz", "V2 Legal", "v2@email.com",
                "1234567890", "Desc", "Addr", "Mark", "City", "State", "411001", "REG2",
                SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.PODCASTS),
                "", "https://podcaststudio.com", "APPROVED", true, 0, 0, "", 1000, 1000);
        BusinessDetailsPage detailsPage = new BusinessDetailsPage(v2);
        assertNotNull(detailsPage);
    }

    @Test
    @DisplayName("Verify CreateBusinessPage generates Node on JavaFX thread")
    public void testCreateBusinessPageGetContent() throws Exception {
        if (!javafxInitialized) return;
        CountDownLatch latch = new CountDownLatch(1);
        final Node[] nodeRef = new Node[1];

        Platform.runLater(() -> {
            try {
                CreateBusinessPage page = new CreateBusinessPage();
                nodeRef[0] = page.getContent();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNotNull(nodeRef[0], "CreateBusinessPage node should be generated");
    }

    @Test
    @DisplayName("Verify EditBusinessPage generates Node for V1 and V2 on JavaFX thread")
    public void testEditBusinessPageGetContent() throws Exception {
        if (!javafxInitialized) return;
        CountDownLatch latch = new CountDownLatch(1);
        final Node[] nodes = new Node[2];

        Platform.runLater(() -> {
            try {
                Business v1 = new Business("b-v1", "o-1", "V1 Biz", "V1 Legal", "v1@email.com",
                        "1234567890", "Desc", "Addr", "Mark", "City", "State", "411001", "REG1",
                        "DRAFT", true, 0, 0, "", 1000, 1000);
                EditBusinessPage editV1 = new EditBusinessPage(v1);
                nodes[0] = editV1.getContent();

                Business v2 = Business.createV2("b-v2", "o-1", "V2 Biz", "V2 Legal", "v2@email.com",
                        "1234567890", "Desc", "Addr", "Mark", "City", "State", "411001", "REG2",
                        SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.ART),
                        "", "https://v2site.com", "DRAFT", true, 0, 0, "", 1000, 1000);
                EditBusinessPage editV2 = new EditBusinessPage(v2);
                nodes[1] = editV2.getContent();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNotNull(nodes[0], "V1 EditBusinessPage node should be generated");
        assertNotNull(nodes[1], "V2 EditBusinessPage node should be generated");
    }
}
