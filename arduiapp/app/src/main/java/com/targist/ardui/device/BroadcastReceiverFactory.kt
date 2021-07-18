package com.targist.ardui.device

import androidx.lifecycle.LifecycleOwner

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BroadcastReceiverFactory {
    fun create(@Assisted lifecycleOwner: LifecycleOwner): AppBroadcastReceiver
}