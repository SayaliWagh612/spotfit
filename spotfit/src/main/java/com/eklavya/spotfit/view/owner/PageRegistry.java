package com.eklavya.spotfit.view.owner;

/**
 * Router kept for backward compatibility with every existing
 * `PageRegistry.go("key")` call site across the Owner module (sidebar
 * items, the gear icon, the avatar, "Back to Dashboard" buttons, etc.)
 * so none of those call sites had to change.
 *
 * What changed is what happens INSIDE go(): it used to build a brand new
 * Scene - sidebar, top bar and all - on every single call, and cache it
 * in a Map<String,Scene> (sceneCache) purely to avoid redoing that full
 * rebuild on the next click to the same page.
 *
 * Now OwnerShell builds the sidebar + top bar exactly once, so there is
 * nothing expensive left to rebuild and nothing left to cache - go(key)
 * just asks OwnerShell to swap its content pane.
 */
public class PageRegistry {

    public static void go(String key) {
        OwnerShell.show(key);
    }

    /**
     * No-op now. Nothing is cached anymore, so there's nothing to
     * invalidate - kept only so existing call sites (e.g. the space-scope
     * ComboBox inside OwnerUi.pageShell, still used by pages that have not
     * been converted to OwnerShell yet) keep compiling unchanged.
     */
    public static void invalidate(String key) {
    }

    public static void invalidateAll() {
    }
}

