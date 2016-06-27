package com.kerawa.app.utilities;

import android.util.Log;

import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;

/**
 * Created by root on 18/11/15.
 */
public  class ContainerLoadedCallback implements
        ContainerHolder.ContainerAvailableListener {
    @Override
    public void onContainerAvailable(ContainerHolder containerHolder,
                                     String containerVersion) {
        // Load each container when it becomes available
        Container container = containerHolder.getContainer();
        registerCallbacksForContainer(container);
    }

    public static void registerCallbacksForContainer(Container container) {
        Log.e("Logging a Callback!", "GTM");
    }
}